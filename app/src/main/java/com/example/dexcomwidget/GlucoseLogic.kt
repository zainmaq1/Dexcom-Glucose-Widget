package com.example.dexcomwidget

import java.util.Locale

object GlucoseLogic {
 const val PREFS = "glucose_widget_prefs"
 fun extractGlucose(input: String): Float? {
  val s = input.replace(',', '.')
  Regex("""(?i)(\d{1,2}(?:\.\d{1,2})?)(?:\s*[↑↗→↘↓↙⬆⬇⇧⇩]\s*)?\s*mmol\s*/?\s*l""").find(s)?.groupValues?.get(1)?.toFloatOrNull()?.let { return it }
  Regex("""(?i)(\d{1,2}(?:\.\d{1,2})?)(?:\s*[↑↗→↘↓↙⬆⬇⇧⇩]\s*)?\s*mmol\s*/?\s*l""").find(s)?.groupValues?.get(1)?.toFloatOrNull()?.let { return it / 18.0182f }
  return null // Do not guess units if notification only shows a bare number.
 }
 fun format(v: Float) = String.format(Locale.getDefault(), "%.1f", v)
 fun category(v: Float) = when {
  v < 3.5f || v > 13.1f -> "red"
  (v >= 3.5f && v <= 4.4f) || (v > 10.0f && v <= 13.0f) -> "yellow"
  v >= 4.5f && v <= 10.0f -> "green"
  else -> "yellow"
 }
 fun trend(current: Float?, last: Float?) = when {
  current == null || last == null || current == last -> ""
  current > last -> "🫪 "
  else -> "🫨 "
 }
 fun status(v: Float) = when (category(v)) { "green" -> "🙂"; "yellow" -> "🤨"; else -> "‼️" }
}
