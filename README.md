# O/U 2.5 Pro — Android testing package

Includes a mobile-first O/U 2.5 web app and Android Studio WebView wrapper source.

### Quick Android test
Open `www/index.html` in Chrome on Android, then use **Add to Home screen / Install app** when available.

### Build an APK
Open the `android/` folder in Android Studio. Install Android SDK Platform 35 and Build Tools, sync Gradle, then run the `app` configuration on a connected Android phone or emulator.

This package does not contain a compiled APK because this build environment does not have the Android SDK/Gradle toolchain. The Android source is ready for Android Studio.

### Features
- OVER 2.5 and UNDER 2.5 predictions
- WAIT when signals are too close
- Bankroll and risk-per-pick
- Recent goal-history input
- Odds-aware scoring
- Mobile UI

The scores are model scores, not guaranteed probabilities. Virtual football results are uncertain. The app does not place bets or bypass bookmaker controls.
