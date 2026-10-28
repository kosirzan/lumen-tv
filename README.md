# Lumen TV

A small, open-source Kotlin Android TV client targeting the Philips 43PUS6412/12
and Android 6.0+ (API 23). Native Leanback content rows, remote-friendly controls,
TMDB discovery, and an optional FramexTV iframe integration.

**Status: testing build, not a verified end-to-end streaming release.** Read
[testing status](docs/TESTING.md) before installing. Source, provider metadata
and video content have separate licences. This app does not grant video rights.

## Download

- [Download Lumen TV 0.3.0 APK](https://github.com/kosirzan/lumen-tv/raw/refs/heads/main/downloads/Lumen-TV-0.3.0-debug.apk)
- [Installation instructions](docs/INSTALL.md)
- [Testing results and remaining checks](docs/TESTING.md)

The APK is a debug-signed testing build. Version 0.3.0 adds auto-hiding,
remote-first player controls, a recommended high-quality source, and Continue
Watching actions. Live playback on
the physical Philips TV still requires device verification. See `downloads/SHA256SUMS.txt` to
verify the file. If the TV browser cannot follow GitHub's redirect, use the raw
URL linked in `downloads/README.md`.

## Features

- Trending movies / series, paged popular catalogues, paged multi-search.
- Title details, season and episode selection.
- Native D-pad focus, TV launcher entry, overscan margins.
- Local watchlist (200 titles) and Continue Watching (100 episode/movie positions).
- Resume after 5 seconds; completion at 95% removes a position from Continue Watching.
- Documented FramexTV movie/TV embeds and postMessage controls: play, pause,
  relative seek, volume, provider and subtitle index.
- Five-second auto-hiding player overlay; D-pad Left/Right seek and OK toggles
  playback when hidden. Any navigation key reveals the native controls.
- Continue Watching menu with Resume, Start from beginning, Remove and Details.
- Origin/source-validated progress polling; no native JavaScript bridge.
- Android/WebView/memory/progress/resource/HTTP/renderer diagnostics and retry.
- Bundled offline H.264/AAC test pattern using Media3, independent of FramexTV.
- No account, server, analytics, subscription, scraped streams or paid API.

## Quick start

1. Install the debug APK: [installation guide](docs/INSTALL.md).
2. Settings → Test player verifies basic local video decoding and remote operation.
3. Obtain your own free **non-commercial** TMDB API read token or v3 API key at
   https://www.themoviedb.org/settings/api and enter it in Settings.
4. Only enable FramexTV if you have permission to access the selected content.
5. Browse → title → Play, or choose a series season/episode.

FramexTV is an external service, not part of this project's MIT licence. The
published embed documentation is a technical contract, not evidence of rights
to any particular movie. Provider availability, access requirements, geographic
restrictions and WebView compatibility may change. No restrictions are bypassed.

## Build

Requirements: JDK 17, Android SDK platform 35, build tools (Gradle installs required
components if SDK licences have already been accepted), network access to Google
Maven/Maven Central/Gradle. Set `ANDROID_HOME` or `sdk.dir` in local.properties.

```sh
./gradlew --no-daemon testDebugUnitTest lintDebug assembleDebug assembleDebugAndroidTest
# With a running Android 6 emulator or an authorized connected TV:
./gradlew connectedDebugAndroidTest
```

APK: `app/build/outputs/apk/debug/app-debug.apk`.
Debug APKs are for sideloading; they use the local Android debug signing key.
No private release key or TMDB credential is in this source. To keep updates
installable without deleting local data, retain your own signing keystore.
The supplied workflow builds APKs and reports and runs API 23 emulator checks;
see the repository's Actions tab for current GitHub CI results.
Public GitHub Actions normally have a free tier; private-repository quota limits
and terms are controlled by GitHub. This project provisions no paid resource.

## Architecture

`MainActivity` / Leanback rows → `Tmdb` metadata client → `DetailsActivity` →
`PlayerActivity` (FramexTV WebView or bundled native diagnostic).
`Store` keeps bounded private preferences; `Posters` uses a bounded image executor
and 8 MiB RGB565 bitmap cache. JSON metadata cache: 40 files, 15-minute freshness.
Only one player WebView exists and is destroyed on exit. No video prefetch or
background streaming. Native player pauses when backgrounded.

Leanback is intentionally used for this legacy TV target despite its upstream
deprecation in favor of Compose for TV. Versions are pinned and should be reviewed
before broader distribution. App install compatibility does not imply the
provider's modern JavaScript player supports an old Android System WebView.

See [research](docs/RESEARCH.md), [privacy/security](docs/PRIVACY.md),
[third-party notices](THIRD_PARTY_NOTICES.md), [test status](docs/TESTING.md), and
[continuation instructions](docs/CONTINUATION.md).
