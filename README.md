# Dexcom Glucose Widget

Unofficial Android home-screen widget reading notification text from a Dexcom-named app. Displays current value, previous distinct value, trend emoji, status emoji, and color.

## GitHub build (phone-friendly)
1. Download the project ZIP and extract it.
2. Create a GitHub repository and upload all files/folders, including `.github`.
3. Open **Actions** → **Build Android APK** → **Run workflow** (use GitHub in Chrome with Desktop site if needed).
4. When complete, open the run and download the **Dexcom-Glucose-Widget-debug-apk** artifact.
5. Extract the downloaded artifact ZIP and install `app-debug.apk` on your Android phone.

## Setup
1. Open the installed app and tap **Open notification access settings**.
2. Enable **Dexcom G7 notification reader**.
3. Long-press the home screen → **Widgets** → **Dexcom Glucose Widget** → add it.
4. Keep Dexcom notifications enabled.

## Important parsing detail
This version only accepts glucose numbers explicitly marked `mmol/L` or `mg/dL`; it will not guess the unit from a bare number. Dexcom notification formats vary by app/region, so if the notification doesn't include units, the parser will need adjustment to match the exact notification text. Do not rely on this unofficial widget for treatment decisions; confirm readings and alerts in the official Dexcom app/receiver.

Rules: green + 🙂 for 4.5–10.0; yellow + 🤨 for 3.5–4.4 or above 10.0 through 13.0; red + ‼️ below 3.5 or above 13.1. Higher than previous shows 🫪 first; lower shows 🫨 first. The 13.0–13.1 gap is yellow as a conservative fallback because the requested boundaries leave it unspecified.
