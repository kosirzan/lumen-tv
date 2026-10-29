# Changelog

## 0.7.0 — 2026-10-29

- Map every visible FramexTV bottom action: Play, Volume, Captions, Settings,
  Cast, Picture-in-Picture and Fullscreen.
- Expand popup navigation from four rows to eleven proportional rows so longer
  subtitle and settings lists are reachable.
- Replace the persistent yellow circle with a translucent rounded focus highlight
  sized to the selected button, timeline point or popup row.
- Hide the focus highlight after five seconds of inactivity and send a safe hover
  refresh at two seconds so FramexTV's own control bar remains visible for the
  requested five-second interaction window.

## 0.6.0 — 2026-10-29

- Make FramexTV controls the automatic default when playback opens; Lumen's
  overlay is now a fallback reached with Menu.
- Replace free cursor movement with proportional snap targets measured from the
  live 1280×720 FramexTV layout: Play, Captions, Settings, Fullscreen and timeline.
- Map Left/Right across the bottom actions, Up to the timeline, timeline
  Left/Right to 30-second seeking, and vertical popup navigation for captions
  and settings menus.
- Use normalized coordinates so the focus ring scales with the WebView size.

## 0.5.0 — 2026-10-29

- Replace Player UI keyboard forwarding with a visible remote-controlled cursor:
  D-pad moves a real browser pointer and OK sends a real click to FramexTV.
- Send hover movement while the cursor moves so FramexTV can reveal its normally
  pointer-triggered control bar. Back or Menu returns to Lumen controls.
- Continue cancelling every invalid TLS request, but record provider-resource
  certificate failures in Diagnostics instead of permanently covering otherwise
  working playback with a fatal-looking banner.
- Preserve the package and signing identity for an in-place update from 0.2.0+.

## 0.4.0 — 2026-10-29

- Stop replacing the title with an alarming warning when optional FramexTV
  progress telemetry is absent; Diagnostics now explains the limited impact.
- Accept documented progress events in object, JSON-string, and nested payload
  forms while continuing to require a known FramexTV HTTPS origin.
- Add a remote-focusable timeline. When FramexTV supplies duration telemetry,
  Left/Right moves by 30 seconds and sends its documented exact `seek` command.
- Add Player UI mode, which deliberately transfers focus and remote keys into
  FramexTV's own visible control layer. Back or Menu returns to Lumen controls.
- Preserve the package and 0.2.0+ signing identity for in-place installation.

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
