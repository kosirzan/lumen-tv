# Testing status — Lumen TV 0.5.0

28 October 2026. **Installable testing build; end-to-end streaming is not verified.**

## Verified results

| Check | Result |
|---|---|
| Kotlin / Android SDK debug build | PASS |
| Minimum SDK in compiled APK | 23 (Android 6.0) |
| Target / compile SDK | 35 |
| JVM unit tests | 13 passed, 0 failed |
| Android lint | 0 errors, 28 warnings |
| APK signature | Verified; v1 and v2 signatures present |
| Installation on Android 6 emulator | PASS |
| Android instrumentation tests | 3 passed, 0 failed; 24.79 s test execution |
| D-pad Home → Settings → Back | PASS |
| Watchlist add/remove and progress/completion store | PASS |
| Offline H.264 playback progress, D-pad pause/resume and seek commands | PASS |
| Native decoded video image | Visually confirmed in screenshot |
| Live TMDB requests with personal key | NOT TESTED — no personal key supplied |
| FramexTV host redirect and public embed contract | PASS — live HTTPS redirect and current public client documentation inspected |
| FramexTV first frame / controls / telemetry / resume | NOT TESTED on physical TV |
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
stack even when this native APK installs. Provider iframe cookies are allowed;
popups and permission requests remain blocked. There is no stream extraction,
DRM workaround, browser fingerprint workaround, or undocumented API fallback.
No documented quality-selection or subtitle-language enumeration API was found;
this app exposes subtitle indices and provider selection, not a native quality
or audio-track catalogue. Play/Pause labels indicate the requested state because
the documented API has no acknowledgement contract.

Version 0.2.0 addressed a concrete integration failure observed on 28 October:
the old `.xyz` embed redirects to `.cc`, while 0.1.0 restricted its iframe and
origin validation to `.xyz`. The app now starts on `.cc`, permits only the known
HTTPS migration aliases, matches the provider's documented iframe environment,
and adds privacy-preserving provider resource, HTTP, console-error-count and
renderer diagnostics. Console text and resource URLs are not retained.

Version 0.3.0 adds a five-second auto-hide overlay and direct remote shortcuts,
defaults to the documented P.E.K.K.A high-bitrate 1080p source, exposes the
documented provider and numbered subtitle commands with clearer native dialogs,
and adds removal/start-over actions to Continue Watching. FramexTV does not
document a direct quality-selection command; provider selection influences the
available stream, while adaptive playback and actual resolution remain under
FramexTV's control.

Version 0.4.0 removes the false-positive top-screen progress warning, accepts
documented progress telemetry in multiple safe payload shapes, adds an exact
seek timeline when duration is known, and provides Player UI mode for passing
remote events into FramexTV's own visible controls. FramexTV still controls which
native settings its embedded UI exposes.

Version 0.5.0 replaces keyboard-only Player UI forwarding with a visible D-pad
cursor that sends browser hover and click input to the embedded player. This is
the closest standards-based equivalent to using FramexTV in a TV browser; the
provider still determines its control layout and whether each control responds.
Invalid TLS resources remain blocked, but a nonfatal resource failure is recorded
in Diagnostics rather than replacing the title/status for the whole session.

Physical acceptance checklist: [PHYSICAL_TV_CHECKLIST.md](PHYSICAL_TV_CHECKLIST.md).
Research and exact documented contract: [RESEARCH.md](RESEARCH.md).

## APK identity

- Filename: `Lumen-TV-0.5.0-debug.apk`
- Package: `dev.lumentv`
- Version: `0.5.0` / versionCode `5`
- Size: 7,642,432 bytes
- SHA-256: `af8efaa98dc889c6bf8c424d613727397d6026640888a4f765aab1838cabe9da`

The APK is debug-signed for sideload testing. No release signing key, TMDB key,
Android SDK, emulator disk or user account credentials are in the handoff source.
The 0.5.0 APK has the same signing certificate as 0.2.0–0.4.0 and can update them in
place. The 0.1.0 key was unavailable, so upgrading directly from 0.1.0 still
requires uninstalling it first.

## Evidence

`evidence/final/` contains build output, unit-test XML, lint XML/HTML, signature
verification, compiled manifest badging, installation output, final Android
instrumentation output and screenshots. The emulator installation/instrumentation
evidence is from 0.1.0; 0.5.0 was locally compiled, unit-tested, linted, packaged,
and signature-verified. See GitHub Actions for the published commit's CI result.
