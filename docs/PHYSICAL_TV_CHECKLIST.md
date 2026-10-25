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
