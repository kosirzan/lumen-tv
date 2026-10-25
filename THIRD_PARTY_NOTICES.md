# Third-party notices

Lumen TV source and generated diagnostic clip: MIT (see LICENSE).

Direct dependencies:

| Component | Version | Licence / upstream |
|---|---|---|
| AndroidX Leanback | 1.0.0 | Apache 2.0, https://android.googlesource.com/platform/frameworks/support/ |
| AndroidX Fragment KTX | 1.6.2 | Apache 2.0, same AndroidX upstream |
| AndroidX Media3 ExoPlayer / UI | 1.3.1 | Apache 2.0, https://github.com/androidx/media |
| Kotlin standard library | 1.9.24 | Apache 2.0, https://github.com/JetBrains/kotlin |
| JUnit (tests) | 4.13.2 | EPL 1.0, https://github.com/junit-team/junit4 |
| AndroidX Test / UIAutomator (tests) | pinned in build | Apache 2.0 |
| Gradle wrapper | 8.7 | Apache 2.0, https://github.com/gradle/gradle |

Transitive dependencies retain their own licences and packaged notices. Gradle
packaged metadata and upstream POMs are authoritative. Review the resolved
dependency report before distributing a commercial fork.

TMDB data, images and branding are not MIT-licensed app assets. This product uses
the TMDB API but is not endorsed or certified by TMDB. See
https://developer.themoviedb.org/docs/faq and the TMDB API terms.

FramexTV is not bundled, sublicensed, owned or endorsed by this project. Its
catalogue and service are outside the app licence. The app communicates with
its published embed interface only, when enabled by the user.

Android SDK/emulator are external developer tools under their own terms, not
redistributed in this source package. FFmpeg was used to generate test patterns;
FFmpeg executable and source are not linked or bundled with this app.

The APK also includes the Apache 2.0 licence text and the Checker Framework
qualifier MIT licence in `res/raw/licences.txt`, viewable in Settings. The
approved TMDB short logo is included unchanged in colour/aspect ratio; its
source and branding terms are recorded in `docs/TMDB_LOGO_SOURCE.txt`.
