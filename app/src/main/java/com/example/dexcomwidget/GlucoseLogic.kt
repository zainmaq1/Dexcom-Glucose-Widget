package com.example.dexcomwidget

import java.util.Locale

object GlucoseLogic {
    const val PREFS = "glucose_widget_prefs"

    fun extractGlucose(input: String): Float? {
        val s = input.replace(',', '.')

        // Match glucose readings explicitly labelled in mmol/L.
        Regex(
            """(?i)(\d{1,2}(?:\.\d{1,2})?)\s*(?:[↑↗→↘↓↙⬆⬇⇧⇩]\s*)?mmol\s*/?\s*l"""
        ).find(s)?.groupValues?.get(1)?.toFloatOrNull()?.let {
            if (it in 1.0f..40.0f) return it
        }

        // Match glucose readings explicitly labelled in mg/dL.
        Regex(
            """(?i)(\d{2,3}(?:\.\d{1,2})?)\s*(?:[↑↗→↘↓↙⬆⬇⇧⇩]\s*)?mg\s*/?\s*dl"""
        ).find(s)?.groupValues?.get(1)?.toFloatOrNull()?.let {
            if (it in 18.0f..720.0f) return it / 18.0182f
        }

        // Never guess the units from an unlabelled number.
        return null
    }

    fun format(v: Float) =
        String.format(Locale.getDefault(), "%.1f", v)

    fun category(v: Float) = when {
        v < 3.5f || v > 13.1f -> "red"
        (v >= 3.5f && v <= 4.4f) ||
            (v > 10.0f && v <= 13.0f) -> "yellow"
        v >= 4.5f && v <= 10.0f -> "green"
        else -> "yellow"
    }

    fun trend(current: Float?, last: Float?) = when {
        current == null || last == null || current == last -> ""
        current > last -> "🫪 "
        else -> "🫨 "
    }

    fun status(v: Float) = when (category(v)) {
        "green" -> "🙂"
        "yellow" -> "🤨"
        else -> "‼️"
    }
}
