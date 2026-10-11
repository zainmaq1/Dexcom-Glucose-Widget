package com.example.dexcomwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Color
import android.widget.RemoteViews

class GlucoseWidgetProvider : AppWidgetProvider() {
 override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
  val p = context.getSharedPreferences(GlucoseLogic.PREFS, Context.MODE_PRIVATE)
  val current = p.getString("current_raw", null)?.toFloatOrNull()
  val last = p.getString("last_raw", null)?.toFloatOrNull()
  val display = p.getString("current_display", null)
  val lastDisplay = p.getString("last_display", "—") ?: "—"
  val time = p.getString("updated_at", null) ?: "Waiting for G7 notification"
  val color = when (current?.let { GlucoseLogic.category(it) }) { "green" -> Color.rgb(35,130,65); "yellow" -> Color.rgb(190,135,0); "red" -> Color.rgb(190,35,35); else -> Color.rgb(80,80,80) }
  val main = if (current != null && display != null) "${GlucoseLogic.trend(current,last)}${GlucoseLogic.status(current)}  $display mmol/L" else "Waiting for Dexcom G7…"
  for (id in ids) {
   val v = RemoteViews(context.packageName, R.layout.glucose_widget)
   v.setTextViewText(R.id.current_text, main)
   v.setTextViewText(R.id.last_text, "Last: $lastDisplay" + if (last != null) " mmol/L" else "")
   v.setTextViewText(R.id.time_text, if (current != null) "Updated $time" else "Enable access; wait for a G7 reading")
   v.setInt(R.id.widget_root, "setBackgroundColor", color)
   manager.updateAppWidget(id, v)
  }
 }
}
