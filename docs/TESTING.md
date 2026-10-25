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
| GitHub repository | Created by user: kosirzan/lumen-tv; publication follows the original tests |
| GitHub release / Actions | See repository for current status; original tests below ran in the cloud workspace |

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
instrumentation output and screenshots. GitHub CI is supplied as source but has
not been executed on GitHub. See CONTINUATION.md for exact publication steps.
