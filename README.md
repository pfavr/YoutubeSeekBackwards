# Headset Rewind

Hold your headset's voice-command button to rewind active media.
Default: **30 seconds**, adjustable from **1 to 120 seconds**. Android 9+.

[Download](https://github.com/pfavr/YoutubeSeekBackwards/releases)
the signed APK from GitHub Releases.

<img src="docs/screenshot.png" alt="Headset Rewind settings" width="320">

## Setup

1. Open the app and grant **Notification access**.
2. Set and save the rewind duration.
3. Start a seekable player, hold the headset button, and choose
   **Headset Rewind (long press)**, then **Always** if offered.

On Android 13+, enable **Allow restricted settings** in system App info if
Notification access is blocked. Clear another app's voice-command defaults if
it opens instead. Tested with Shokz OpenSwim Pro and Samsung S25 Ultra.

## Limits and privacy

Requires a headset that launches `ACTION_VOICE_COMMAND`, not `ACTION_ASSIST`.
Single/double/triple clicks are unchanged. Calls suspend seeking; lock-screen
delivery depends on Android. Ads, live streams, and player restrictions may
prevent seeking. A sent command is not proof the player honored it.

Notification access controls media sessions; notification contents are unused.
No network, microphone, Accessibility access, ads, analytics, or runtime libraries.

## Build and release

Use JDK 17 or 21, Android SDK 34, and build tools 35.0.0. Set `ANDROID_HOME`.

```sh
./gradlew :app:testReleaseUnitTest :app:lintRelease :app:assembleRelease
zipalign -f 4 app/build/outputs/apk/release/app-release-unsigned.apk /tmp/aligned.apk
apksigner sign --ks /path/to/release.p12 --v4-signing-enabled false \
  --out headset-rewind-v2.0.1.apk /tmp/aligned.apk
apksigner verify --verbose headset-rewind-v2.0.1.apk
```

On Windows use `gradlew.bat`. Keep signing keys/passwords outside the repository.
Publish the signed APK on GitHub with the matching source tag, currently `v2.0.1`.

## F-Droid

Submission requires review; submission does not mean acceptance.
The [build recipe](docs/fdroid/org.headsetrewind.yml) goes in
`fdroiddata/metadata/org.headsetrewind.yml`. It builds the public `v2.0.1` tag.
[Store metadata](fastlane/metadata/android/en-US) must be included in release tags.
For updates, increment the app version/code, add a version-code changelog, and tag.

F-Droid and GitHub use different signing keys. Switching channels (or from an
old debug build) requires uninstalling first, resetting settings and permissions.
Updates within one channel retain signing compatibility.

## License

[Apache-2.0](LICENSE). Copyright 2026 Headset Rewind contributors.
