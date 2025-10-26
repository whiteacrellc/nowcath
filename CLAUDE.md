# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Cath Rmdr is an Android application that reminds people who use intermittent catheters when to cath. It's a privacy-focused app that collects no personal information and doesn't connect to the internet at all.

**Package name**: `org.tomw.cathrmdr`
**Min SDK**: 29 (Android 10)
**Target SDK**: 34 (Android 14)

## Build Commands

```bash
# Build the project
./gradlew build

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install on connected device/emulator
./gradlew installDebug

# Run tests
./gradlew test

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Clean build
./gradlew clean
```

## Configuration

The app requires an `apiKey` property in `gradle.properties` for the `API_KEY` build config field. Note: While this is configured in the build, the app currently doesn't use it since it doesn't connect to the internet.

## Architecture

### Tech Stack
- **UI**: 100% Jetpack Compose with Material 3
- **Navigation**: Jetpack Navigation Compose
- **State Management**: Compose state and SharedPreferences
- **Scheduling**: AlarmManager with WorkManager fallback for backward compatibility
- **Audio**: MediaPlayer with custom sound resources

### Key Components

**MainActivity** (`MainActivity.kt:26`)
- Single Activity hosting Compose navigation
- Handles runtime permission requests (notifications for Android 13+)
- Theme management integration

**Navigation Structure** (`MainActivity.kt:75-106`)
Three main screens:
1. `main` - MainScreen: Primary alarm configuration UI
2. `sounds` - SoundSettingsScreen: Sound selection
3. `privacy` - PrivacyScreen: Privacy policy display

**Notification System** (`notifications/NotificationManager.kt`)
- Uses AlarmManager with `setRepeating()` for exact repeating alarms
- Requires `SCHEDULE_EXACT_ALARM` permission (Android 12+)
- Falls back to `setInexactRepeating()` if exact alarms not available
- WorkManager implementation kept for backward compatibility
- Broadcasts to `AlarmReceiver` which triggers notifications

**Audio System** (`audio/AudioManager.kt`)
- Custom sounds stored in `app/src/main/res/raw/` (alarm1.wav through alarm4.wav)
- Uses `USAGE_ALARM` audio attributes for system-level alarm behavior
- Haptic feedback via Vibrator with waveform patterns
- Automatic MediaPlayer cleanup and error handling

**Data Persistence** (`data/PreferencesManager.kt`)
- SharedPreferences-based storage for:
  - Alarm interval settings
  - Selected sound option
  - Next alert time tracking
  - Theme preference
  - Alarm active state

**Theme System** (`ui/theme/ThemeManager.kt`)
- Three theme options: Light, Dark, Auto
- Reactive updates via Compose state
- Persisted to SharedPreferences

### Important Constraints

**Minimum Alert Time**: The app enforces a 15-minute (900 seconds) minimum for alarm intervals (`MainScreen.kt:434-437`)

**Alarm Persistence**: `BootReceiver` (`notifications/BootReceiver.kt`) restarts alarms after device reboot by listening for `BOOT_COMPLETED` and `MY_PACKAGE_REPLACED` intents.

### Source Structure

```
app/src/main/java/org/tomw/cathrmdr/
├── MainActivity.kt                    # Entry point, navigation setup
├── audio/
│   └── AudioManager.kt                # Sound playback and haptics
├── data/
│   ├── PreferencesManager.kt          # SharedPreferences wrapper
│   └── SoundOption.kt                 # Sound selection enum
├── notifications/
│   ├── NotificationManager.kt         # Alarm scheduling and notifications
│   └── BootReceiver.kt                # Boot/package replacement handling
└── ui/
    ├── components/
    │   ├── MainScreen.kt              # Primary alarm UI
    │   ├── SoundSettingsScreen.kt     # Sound selection UI
    │   └── PrivacyScreen.kt           # Privacy policy UI
    └── theme/
        ├── ThemeManager.kt            # Theme state management
        ├── Theme.kt                   # Compose theme definition
        ├── Color.kt                   # Color definitions
        └── Type.kt                    # Typography definitions
```

### Testing

Test structure located at `app/src/test/java/` - currently has basic test scaffolding.
