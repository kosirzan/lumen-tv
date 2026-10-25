# Compatibility and provider research

Research date: 25 October 2026 (Europe/Ljubljana).

## Philips 43PUS6412/12

Philips' official product specifications list Android 6.0 Marshmallow, a quad-core
platform, 3840×2160 display and 16 GB storage. **16 GB is storage, not RAM.** No
reliable manufacturer RAM figure was established. The installed firmware version,
WebView version, available RAM and decoding performance require physical testing.

Official model specifications:
https://www.documents.philips.com/assets/20230308/4d887c3797334d6ca18aafbf012751c1.pdf

App minimum SDK: 23 (Android 6.0). Kotlin/JVM bytecode targets Java 8 and uses
Android-compatible APIs. No native ABI-specific application libraries are required.
The bundled decoder test is H.264 baseline 640×360 + AAC. This test deliberately
avoids pretending to benchmark 1080p/4K provider playback.

Android TV navigation guidance:
https://developer.android.com/training/tv/get-started/navigation
https://developer.android.com/training/tv/get-started/controllers
Media playback device limitations:
https://developer.android.com/media/media3/exoplayer/supported-devices
Leanback status:
https://developer.android.com/jetpack/androidx/releases/leanback

## TMDB

https://developer.themoviedb.org/docs/getting-started
https://developer.themoviedb.org/docs/faq

Personal/non-commercial API use is free with attribution; commercial use requires
a separate arrangement. Users must register their own key. No account is created
or terms accepted on their behalf. Metadata permission does not provide playback.

Implemented endpoints under https://api.themoviedb.org/3/:

- `trending/movie/week`, `trending/tv/week`
- `movie/popular`, `tv/popular` with pagination
- `search/multi` (movie/TV results, people and adult results omitted)
- `tv/{id}`, `tv/{id}/season/{season}`
- Poster images under https://image.tmdb.org/t/p/w185/

Bearer Read Access Token preferred; 32-character v3 API key also accepted.
TMDB can reject a key, rate-limit, or restrict service availability.

## FramexTV public documentation

https://framextv.xyz/api (also linked from its main site).

The page is a client-rendered React document. HTTP fetch of the route returned
the shell; the publicly delivered documentation component referenced by that
shell was read to retrieve the actual published documentation text. No private
endpoint, authentication credential, extractor or decryption implementation was
used. No video stream extraction was implemented.

Documented movie route: `https://framextv.xyz/embed/{tmdbId}`
Documented episode route: `https://framextv.xyz/embed/{tmdbId}/{season}/{episode}`

Parameters implemented: `autoplay`, `muted`, `controls`, `start`, `provider`.
Documented postMessage actions implemented: `play`, `pause`, `seekBy`, `setVolume`,
`setProvider` (provider preference is applied through reloading with `provider`),
`setSubtitle` with a zero-based index. The provider switch reload preserves the
last valid observed position. `auto` omits the provider parameter.
The documented progress event is `frameXTV:timeupdate`, with `currentTime` and
`duration` in seconds. Other undocumented event formats are intentionally ignored.

The public page advertises embedding in web applications. It does not establish
a general open-source licence for the service or video catalogue, an SLA,
a guaranteed free API tier, Android TV certification, or a minimum supported
WebView version. These matters remain unverified. No claim of permitted video
redistribution is made. Playback must remain conditional on applicable rights.

An old WebView may lack the JavaScript module/media features used by the provider.
The app cannot solve that through native UI changes. Update the official Android
System WebView if the television supports an update. Do not sideload an untrusted
browser package or disable TLS checks as a workaround.

## Cost boundaries

App source and bundled test pattern are free/open source. Building locally uses
free tools; no backend is required. TMDB's non-commercial API is free subject to
its terms. FramexTV's future charges, ads, access policies and service continuity
are outside this project and were not guaranteed. Internet/data and optional
store/developer distribution fees are not part of the app.
