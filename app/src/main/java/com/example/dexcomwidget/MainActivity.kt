package com.example.dexcomwidget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(40, 60, 40, 40) }
  layout.addView(TextView(this).apply { text = "Dexcom Glucose Widget"; textSize = 24f })
  layout.addView(TextView(this).apply { text = "1. Keep Dexcom G7 notifications enabled.\n2. Allow notification access below.\n3. Long-press your home screen and add the widget.\n\nUnofficial companion. Always confirm readings and alerts in the official Dexcom app/receiver."; textSize = 16f; setPadding(0, 24, 0, 24) })
  layout.addView(Button(this).apply { text = "Open notification access settings"; setOnClickListener { startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) } })
  layout.addView(Button(this).apply { text = "Refresh widget"; setOnClickListener { sendBroadcast(Intent(this@MainActivity, GlucoseWidgetProvider::class.java).setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) } })
  setContentView(layout)
 }
}
