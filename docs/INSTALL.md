# Install on Philips 43PUS6412/12

Use the testing APK only after reading TESTING.md. No TV connection was available
to perform these steps automatically.

## USB installation

1. Download `Lumen-TV-0.4.0-debug.apk` to a computer, then copy it to a USB drive.
2. On the TV, check Settings → About for its Android version (must be 6.0+).
3. Open the USB APK with a trusted file manager already available on the TV.
   Philips' menu names vary with firmware. If required, temporarily permit
   installation from that source in Security / Unknown sources.
4. Confirm installation, then restore the unknown-source restriction.
5. Open **Lumen TV** from the Android TV app launcher.

A file manager may be necessary because some Philips media browsers show only
media files, not APK installers. Use the official Play Store for utilities.

## Optional ADB installation

Use your own computer with Android platform-tools. Enable developer options and
USB/network debugging only if your firmware supports it. Authorize your own
computer on the TV. Example for a TV offering network ADB:

```sh
adb connect YOUR_TV_IP:5555
adb install -r Lumen-TV-0.4.0-debug.apk
adb shell monkey -p dev.lumentv -c android.intent.category.LEANBACK_LAUNCHER 1
```

Network debugging is not guaranteed to be available on every Philips firmware.
Disable debugging after use. No root or DRM modification is needed.

## First launch

- Settings → Test player. Moving test patterns and a tone should play for 20 s.
- Pause/Play, ±10 s, Audio, Retry, Diagnostics and Back work with the D-pad.
- Enter a personal TMDB token/key in Settings to load real discovery data.
- Enable FramexTV only when you have permission for the content you access.
- Select a movie or a series episode; the provider is loaded in a single WebView.
- Player controls hide after five seconds. Press any D-pad direction to reveal
  them. With controls hidden, Left/Right seeks 10 seconds and OK pauses/plays.
- When the timeline becomes available, focus it and use Left/Right for exact
  30-second steps. Select Player UI to hand the remote to FramexTV's own controls;
  press Back or Menu to return to Lumen controls.
- Version 0.4.0 uses the same debug signature as 0.2.0/0.3.0 and installs over them while
  retaining the TMDB key, watchlist, progress and settings.

## Troubleshooting

- **Metadata absent:** verify TMDB key, internet, TV date/time; retry from Home.
- **Offline clip works, provider blank:** inspect Diagnostics and record provider
  resources seen, HTTP error, console-error count and renderer state. Update the
  official Android System WebView if possible.
- **No resume row:** progress must be valid, at least 5 s, below 95%, and actually
  reported by FramexTV. The app never invents provider progress from elapsed time.
- **Timeline says it is waiting:** the provider did not send its optional progress
  event. Relative ±10-second seeking and Player UI still work, but exact timeline
  seeking and Continue Watching updates require that event.
- **Authentication / permission / regional restriction:** satisfy the provider's
  normal requirements. The app does not bypass these; unsupported flows can stop
  playback. Provider iframe cookies are allowed; popups and permission requests
  remain blocked.
- **Unsupported codec / buffering / poor performance:** record provider, Android,
  WebView and error from Diagnostics. Try a different Source. Player UI may expose
  controls supplied by the provider itself; Lumen has no documented direct
  quality-control command.
- **Update reports incompatible signing key:** the new build was signed with a
  different debug key. Uninstalling removes watchlist/progress; a consistent
  private signing key is recommended for ongoing personal builds.

Remove app: TV Settings → Apps → Lumen TV → Uninstall. Clearing app data removes
TMDB credentials, watchlist, progress and provider site storage.
