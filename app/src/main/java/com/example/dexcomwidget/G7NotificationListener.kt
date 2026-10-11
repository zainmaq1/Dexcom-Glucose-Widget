</> Kotlin
package com.example.dexcomwidget

import android.app.Notification
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class G7NotificationListener : NotificationListenerService() {

    companion object {
        private const val TAG = "G7Widget"
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "LISTENER CONNECTED")

        try {
            val active = activeNotifications ?: emptyArray()
            Log.d(TAG, "Active notifications: ${active.size}")

            for (sbn in active) {
                processNotification(sbn)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading active notifications", e)
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Log.d(TAG, "Notification posted from package: ${sbn.packageName}")
        processNotification(sbn)
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.w(TAG, "LISTENER DISCONNECTED")

        requestRebind(
            android.content.ComponentName(
                this,
                G7NotificationListener::class.java
            )
        )
    }

    private fun processNotification(sbn: StatusBarNotification) {
        val packageName = sbn.packageName.lowercase(Locale.ROOT)

        // Package identifiers are not display names.
        // Match the package identifier using "dexcom".
        if (!packageName.contains("dexcom")) {
            Log.d(TAG, "Ignoring non-Dexcom package: $packageName")
            return
        }

        Log.d(TAG, "Dexcom package detected: $packageName")

        val extras = sbn.notification.extras

        val text = listOf(
            extras.getCharSequence(Notification.EXTRA_TITLE)
                ?.toString().orEmpty(),
            extras.getCharSequence(Notification.EXTRA_TEXT)
                ?.toString().orEmpty(),
            extras.getCharSequence(Notification.EXTRA_BIG_TEXT)
                ?.toString().orEmpty(),
            extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
                ?.joinToString(" ") { it.toString() }.orEmpty()
        )
            .filter { it.isNotBlank() }
            .joinToString(" ")

        // Diagnostic only: avoid logging unrelated notifications.
        Log.d(TAG, "Dexcom notification text: $text")

        val value = GlucoseLogic.extractGlucose(text)

        if (value == null) {
            Log.w(TAG, "Dexcom notification received, but no supported reading was found")
            return
        }

        Log.d(TAG, "Glucose extracted: ${GlucoseLogic.format(value)}")

        val prefs = getSharedPreferences(
            GlucoseLogic.PREFS,
            Context.MODE_PRIVATE
        )

        val current = prefs.getString("current_raw", null)?.toFloatOrNull()
        val editor = prefs.edit()

        if (current != null && current != value) {
            editor.putString("last_raw", current.toString())
            editor.putString("last_display", GlucoseLogic.format(current))
        }

        editor.putString("current_raw", value.toString())
        editor.putString("current_display", GlucoseLogic.format(value))
        editor.putString(
            "updated_at",
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        )
        editor.apply()

        updateWidget()
    }

    private fun updateWidget() {
        val manager = AppWidgetManager.getInstance(this)
        val ids = manager.getAppWidgetIds(
            android.content.ComponentName(
                this,
                GlucoseWidgetProvider::class.java
            )
        )

        Log.d(TAG, "Updating ${ids.size} widget instance(s)")

        if (ids.isNotEmpty()) {
            val intent = Intent(this, GlucoseWidgetProvider::class.java)
                .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)

            sendBroadcast(intent)
        }
    }
}
```
