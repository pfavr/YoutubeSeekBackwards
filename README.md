# Headset Rewind

Hold your headset's voice-command button to rewind active media.
Default: **30 seconds**, adjustable from **1 to 120 seconds**. Android 9+.

[Download APK](https://github.com/pfavr/YoutubeSeekBackwards/releases)

<img src="docs/screenshot.png" alt="Headset Rewind settings" width="320">

## Setup

1. Grant **Notification access** and save a rewind duration.
2. Start a seekable player.
3. Hold the headset button; choose **Headset Rewind (long press)**, then **Always**.

If access is blocked on Android 13+, enable **Allow restricted settings** in
system App info. If another app opens, clear its voice-command defaults.

Requires a headset that launches voice commands. Other clicks are unchanged.
Calls, ads, live streams, and player restrictions can prevent seeking.

Notification contents are unused. No network, microphone, Accessibility access,
ads, or analytics.

## Build

JDK 17 or 21, Android SDK 34, build tools 35.0.0; set `ANDROID_HOME`.

```sh
./gradlew :app:testReleaseUnitTest :app:lintRelease :app:assembleRelease
```

Outputs an unsigned release APK. On Windows use `gradlew.bat`.

[Apache-2.0](LICENSE)
