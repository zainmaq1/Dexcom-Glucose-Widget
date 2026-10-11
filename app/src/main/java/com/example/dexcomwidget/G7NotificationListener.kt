package com.example.dexcomwidget

import android.app.Notification
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class G7NotificationListener : NotificationListenerService() {
 override fun onNotificationPosted(sbn: StatusBarNotification) {
  if (!sbn.packageName.lowercase(Locale.ROOT).contains("dexcom")) return
  val e = sbn.notification.extras
  val text = listOf(
   e.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty(),
   e.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty(),
   e.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString().orEmpty(),
   e.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)?.joinToString(" ") { it.toString() }.orEmpty()
  ).filter { it.isNotBlank() }.joinToString(" ")
  val value = GlucoseLogic.extractGlucose(text) ?: return
  val p = getSharedPreferences(GlucoseLogic.PREFS, Context.MODE_PRIVATE)
  val current = p.getString("current_raw", null)?.toFloatOrNull()
  if (current == null || current != value) {
   p.edit().putString("last_raw", current?.toString() ?: p.getString("last_raw", null))
    .putString("current_raw", value.toString()).putString("current_display", GlucoseLogic.format(value))
    .putString("last_display", current?.let { GlucoseLogic.format(it) } ?: p.getString("last_display", "—"))
    .putString("updated_at", SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())).apply()
  }
  sendBroadcast(Intent(this, GlucoseWidgetProvider::class.java).setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE))
 }
}
