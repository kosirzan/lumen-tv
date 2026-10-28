# Changelog

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
