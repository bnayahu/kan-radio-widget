# רדיו כאן - Android Widget

An Android home screen widget for listening to Israeli Kan radio channels directly from your device.

## Features

- 8 Kan radio channels:
  - כאן בית (Kan Bet) - 95.5FM
  - כאן גימל (Kan Gimmel) - 97.8FM
  - כאן 88 - 88.0FM
  - כאן מורשת (Kan Moreshet) - 98.8FM
  - כאן קול המוזיקה (Kan Kol HaMusica) - 95.0FM
  - מכאן (Makan) - 102FM
  - כאן תרבות (Kan Tarbut) - 100.4FM
  - כאן רקע (Kan Reka) - 104.5FM

- Home screen widget for quick access
- Simple and intuitive Hebrew interface (RTL)
- **Audio continues playing in the background** 🎵
- Foreground service with notification controls
- Remembers your last station
- Survives device reboot
- Beautiful gradient design

## Installation

### Prerequisites

- Android Studio Arctic Fox or newer
- Android SDK API 21 (Lollipop) or higher
- Kotlin 2.1.0

### Build Instructions

1. **Clone or download this repository** to your computer

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the `radio-widget` folder

3. **Build the project**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Install on device**
   - Connect your Android device via USB
   - Enable USB debugging on your device
   - Run the app from Android Studio or:
   ```bash
   ./gradlew installDebug
   ```

5. **Add the widget to your home screen**
   - Long-press on an empty area of your home screen
   - Tap "Widgets"
   - Find "רדיו כאן"
   - Drag it to your home screen
   - Select your preferred radio station

## Usage

1. Add the widget to your home screen
2. Tap the widget to open the configuration screen
3. Select a radio station from the list
4. The station will start playing in the background
5. Use the widget to control playback (play/stop)
6. Pull down the notification shade for additional controls

## Technical Details

- **Streaming Protocol**: HLS (HTTP Live Streaming) - m3u8 format
- **Stream URLs**: Official from `kancdn.medonecdn.net` (Kan's official CDN)
- **Player Library**: ExoPlayer for HLS playback support
- **Target SDK**: Android API 34
- **Min SDK**: Android API 21 (Android 5.0 Lollipop)
- **Language**: Kotlin
- **Stream Source**: Extracted from official kan.org.il player
- **Background Playback**: Uses Foreground Service with media playback notification
- **Persistence**: Saves your last station using SharedPreferences
- **Boot Persistence**: Automatically restores widget state after device reboot

### Architecture

The widget consists of several key components:

1. **RadioWidget** - AppWidgetProvider that handles widget lifecycle and UI updates
2. **RadioPlaybackService** - Foreground service that manages audio playback
3. **RadioConfigActivity** - Configuration activity for selecting stations
4. **RadioPlayer** - Media player wrapper using ExoPlayer
5. **WidgetUpdateReceiver** - BroadcastReceiver for updating widget state
6. **BootReceiver** - BroadcastReceiver for restoring widget after device boot

This architecture ensures reliable background playback with proper lifecycle management and system integration.

## Troubleshooting

**Widget won't appear:**
- Make sure the app is installed correctly
- Try rebooting your device
- Check that you have sufficient space on your home screen

**Audio won't play:**
- Check your internet connection
- The Kan servers might be down temporarily
- Try a different station
- Check Android's audio permissions

**No sound:**
- Check your device volume
- Make sure the notification shows the player is active
- Check if other audio apps work correctly

**Widget not updating:**
- Check that the app has necessary permissions
- Try removing and re-adding the widget
- Verify the RadioPlaybackService is running

## Permissions

The app requires the following permissions:

- **INTERNET** - To stream radio content
- **FOREGROUND_SERVICE** - To keep audio playing in the background
- **FOREGROUND_SERVICE_MEDIA_PLAYBACK** - For media playback notification
- **POST_NOTIFICATIONS** - To show playback controls notification
- **RECEIVE_BOOT_COMPLETED** - To restore widget state after reboot
- **WAKE_LOCK** - To prevent sleep during playback

## License

This is an unofficial fan-made application. All radio content is owned by the Israeli Public Broadcasting Corporation (כאן - התאגיד הישראלי לשידור).

## Credits

Radio streams provided by [Kan - Israeli Public Broadcasting](https://www.kan.org.il/)
