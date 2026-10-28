# Changelog

## 0.3.0 — 2026-10-28

- Replace the permanently visible player strip with a full-screen overlay that
  hides after five seconds and reappears on remote activity.
- Add direct hidden-state remote shortcuts: Left/Right seeks 10 seconds and OK
  toggles play/pause; media keys continue to work.
- Default existing `auto` users and new installs to FramexTV's documented
  P.E.K.K.A high-bitrate 1080p source. Auto and other documented providers remain
  selectable from a clearer Source menu.
- Explain in the UI that FramexTV has no documented direct quality command, so
  source choice influences but cannot guarantee the adaptive resolution.
- Replace the subtitle disclaimer with remote-friendly numbered track selection
  through FramexTV's documented `setSubtitle` command.
- Add Resume, Start from beginning, Remove from Continue Watching, and Details
  actions when selecting a Continue Watching item.
- Keep the 0.2.0 signing identity so this APK installs as an in-place update.

## 0.2.0 — 2026-10-28

- Start FramexTV embeds on the current `framextv.cc` host after the provider's
  `framextv.xyz` migration.
- Allowlist the provider's old and current HTTPS aliases for redirects,
  postMessage origin validation, content policy, and resource error reporting.
- Match FramexTV's documented iframe environment while continuing to block
  external top-level navigation, popups, permission requests, mixed content,
  file access, and invalid TLS certificates.
- Restore standard embedded-cookie behavior needed by some provider resources.
- Add privacy-preserving counts for provider resources and console errors plus
  HTTP and WebView renderer status to Diagnostics. Messages, URLs, and tokens
  are not retained.
- Add regression coverage for the current embed origin and redirect allowlist.

Physical-device FramexTV playback remains to be confirmed on the Philips
43PUS6412/12. The app does not extract streams, bypass DRM, or bypass access
controls.

## 0.1.0 — 2026-10-25

- Initial Android TV testing build.
