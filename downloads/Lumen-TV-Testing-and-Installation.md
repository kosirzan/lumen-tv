# Archived 0.1.0 testing evidence

This file preserves the original 0.1.0 emulator evidence. For the current
0.3.0 APK, download/install instructions, player improvements, checksums and
remaining physical-TV limits, use [README.md](README.md),
[../docs/INSTALL.md](../docs/INSTALL.md), and
[../docs/TESTING.md](../docs/TESTING.md).

# Testing status — Lumen TV 0.1.0

25 October 2026. **Installable testing build; end-to-end streaming is not verified.**

## Verified results

| Check | Result |
|---|---|
| Kotlin / Android SDK debug build | PASS |
| Minimum SDK in compiled APK | 23 (Android 6.0) |
| Target / compile SDK | 35 |
| JVM unit tests | 11 passed, 0 failed |
| Android lint | 0 errors, 18 warnings |
| APK signature | Verified; v1 and v2 signatures present |
| Installation on Android 6 emulator | PASS |
| Android instrumentation tests | 3 passed, 0 failed; 24.79 s test execution |
| D-pad Home → Settings → Back | PASS |
| Watchlist add/remove and progress/completion store | PASS |
| Offline H.264 playback progress, D-pad pause/resume and seek commands | PASS |
| Native decoded video image | Visually confirmed in screenshot |
| Live TMDB requests with personal key | NOT TESTED — no personal key supplied |
| FramexTV first frame / controls / telemetry / resume | NOT TESTED |
| Physical Philips television | NOT TESTED |
| GitHub repository | Published at https://github.com/kosirzan/lumen-tv |\n| GitHub Actions | See repository Actions for current results; original evidence below ran in the cloud workspace |

## Environment and scope

Official Android 6.0 / API 23 Google APIs x86_64 emulator, emulator 37.2.12,
1280×720 at 213 dpi (about 960×540 logical dp), 1536 MiB configured memory.
Software CPU emulation and graphics; hardware acceleration unavailable. The
emulator used a generic Android image with a TV-sized display, not Philips
firmware or a full Android TV system image. Navigation tests use real D-pad key
events. The emulated audio output was disabled: audible sound is not verified.

The native clip is a generated 20-second 640×360 H.264 baseline / AAC test pattern.
A screenshot shows decoded frames and a focused Pause control. This proves the
native diagnostic path, not FramexTV playback, live streaming, 1080p/4K smoothness,
codec compatibility on Philips, or long-session reliability.

The player regression test initially failed because Select arrived before any
button had input focus in Android touch mode. The final correction permits
initial focus in touch mode and keeps the video surface out of the focus tree.
The final APK was reinstalled and all three device tests rerun successfully.
Initial failure records are preserved separately; they are not the final result.
A separate unit regression verifies that reloading a paused embed does not
request autoplay.

## Lint warnings

Warnings include pinned older dependency versions, television-only fixed
orientation, English UI strings, vector banner sizing, and backup configuration
for newer Android versions. They are not suppressed as errors or hidden behind
a lint baseline. See the full XML/HTML report in `evidence/final`.

## Unverified provider behavior and limitations

TMDB needs the user's own key and compliance with its non-commercial attribution
terms. The live catalogue, search, pagination and season/episode flows need a
credentialed integration test. Unit tests do not claim to verify those APIs.

The FramexTV adapter implements the public embed contract. No permitted video
was played through that provider in this environment. Playback, command delivery,
subtitle track indices, fallback behavior and progress-event delivery remain
unverified. Public documentation does not establish catalogue distribution rights
or guarantee a free service tier. Enable it only for content you may access.

An obsolete TV WebView may be unable to run the provider's JavaScript or media
stack even when this native APK installs. Provider login/forms, popups and
third-party cookies are restricted by this client. There is no stream extraction,
DRM workaround, browser fingerprint workaround, or undocumented API fallback.
No documented quality-selection or subtitle-language enumeration API was found;
this app exposes subtitle indices and provider selection, not a native quality
or audio-track catalogue. Play/Pause labels indicate the requested state because
the documented API has no acknowledgement contract.

Physical acceptance checklist: [PHYSICAL_TV_CHECKLIST.md](PHYSICAL_TV_CHECKLIST.md).
Research and exact documented contract: [RESEARCH.md](RESEARCH.md).

## APK identity

Filename: `Lumen-TV-0.1.0-debug.apk`  
Package: `dev.lumentv`  
Version: `0.1.0` / versionCode `1`  
Size: 7,690,989 bytes  
SHA-256: `cbf61985e58ef4ec1ea8f8be61ef975ccf0b89b478c5fe64da2071b597b0e185`

The APK is debug-signed for sideload testing. No release signing key, TMDB key,
Android SDK, emulator disk or user account credentials are in the handoff source.
Keep a stable private keystore for future updates; changing signing keys normally
requires uninstalling and losing local data.

## Evidence

`evidence/final/` contains build output, unit-test XML, lint XML/HTML, signature
verification, compiled manifest badging, installation output, final Android
instrumentation output and screenshots. GitHub CI is now available in the repository Actions tab. The original evidence
in this report ran in the cloud workspace. See CONTINUATION.md for remaining work.


---

# Install on Philips 43PUS6412/12

Use the testing APK only after reading TESTING.md. No TV connection was available
to perform these steps automatically.

## USB installation

1. Download `Lumen-TV-0.1.0-debug.apk` to a computer, then copy it to a USB drive.
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
adb install -r Lumen-TV-0.1.0-debug.apk
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

## Troubleshooting

- **Metadata absent:** verify TMDB key, internet, TV date/time; retry from Home.
- **Offline clip works, provider blank:** inspect Diagnostics, update the official
  Android System WebView if possible. Old WebView compatibility is unverified.
- **No resume row:** progress must be valid, at least 5 s, below 95%, and actually
  reported by FramexTV. The app never invents provider progress from elapsed time.
- **Authentication / permission / regional restriction:** satisfy the provider's
  normal requirements. The app does not bypass these; unsupported flows can stop
  playback. Third-party cookies, popups and provider login forms are restricted.
- **Unsupported codec / buffering / poor performance:** record provider, Android,
  WebView and error from Diagnostics. Start testing with 720p/1080p if the provider
  offers a quality selector; the app has no documented quality-control API.
- **Update reports incompatible signing key:** the new build was signed with a
  different debug key. Uninstalling removes watchlist/progress; a consistent
  private signing key is recommended for ongoing personal builds.

Remove app: TV Settings → Apps → Lumen TV → Uninstall. Clearing app data removes
TMDB credentials, watchlist, progress and provider site storage.


---

# Philips 43PUS6412/12 acceptance checklist

Record: firmware __________ Android/API __________ WebView/Chrome __________
Network: Ethernet / Wi-Fi __________ Resolution __________

No item below has been verified on the physical Philips TV.

| Test | Expected outcome | Result |
|---|---|---|
| APK install | Installs without root; TV launcher icon appears | Untested |
| Cold launch | No crash, readable content within overscan margins | Untested |
| D-pad navigation | Home, catalogue, watchlist, search, settings and every player action reachable | Untested |
| Back behavior | Player → details → home → TV launcher; no focus trap | Untested |
| Offline clip | Moving pattern, audible tone, pause/resume, ±10 seconds, retry | Untested |
| Live TMDB | Token accepted; popular/trending/search/paging work | Untested |
| Series selection | Correct season/episode names and TMDB identifier passed | Untested |
| FramexTV start | Permitted title starts with sound and acceptable startup delay | Untested |
| Provider controls | Play, pause, seek and volume actually change provider state | Untested |
| Subtitle index | Available selected track renders; unavailable track fails gracefully | Untested |
| Provider switch | Documented provider loads; valid resume position retained | Untested |
| Continue Watching | Exit after >5 seconds, relaunch, resume close to saved position | Untested |
| Episode progress | S1E1 and S1E2 positions remain separate | Untested |
| Completion | At ≥95%, exact movie/episode leaves Continue Watching | Untested |
| Watchlist persistence | Survives app force stop and TV restart | Untested |
| Backgrounding | Home/standby stops audio; returning resumes/reloads safely | Untested |
| Poor connectivity | UI remains usable, retry/error visible, no invented progress | Untested |
| Remote disconnect | Reconnection preserves usable focus | Untested |
| Sustained use | 30-minute browsing and 60-minute permitted playback without crash | Untested |
| 720p/1080p | Record startup delay, stalls and audio sync | Untested |
| 4K/HDR | Optional; no smoothness or codec guarantee | Untested |

For performance evidence with an authorized ADB connection:

```sh
adb shell dumpsys meminfo dev.lumentv
adb shell dumpsys gfxinfo dev.lumentv
adb logcat -s AndroidRuntime:E chromium:E
```

Do not publicly post logs containing user input, credentials or provider URLs.
The app's own Diagnostics intentionally omits TMDB tokens and full playback URLs.
Record actual observations; do not interpret emulator success as a Philips result.
