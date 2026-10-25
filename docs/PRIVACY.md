# Privacy and security

No app analytics, advertising SDK or backend. Network destinations:
TMDB metadata API, TMDB poster CDN, and the external FramexTV iframe and its own
subresources. Those services see your IP and request metadata. The app cannot
make promises about the provider's own analytics or advertising.

The TMDB token is stored in private Android preferences, never committed or
included in diagnostic output. It is not encrypted against a rooted device or
an already-compromised Android installation. Backups are disabled. Metadata
cache keys are SHA-256 hashes; cached JSON does not contain the credential.
Watchlist/progress are private local bounded preferences.

The player host uses HTTPS origin `appassets.androidplatform.net` with locally
supplied HTML. The cross-origin iframe is confined by CSP and sandbox flags.
Only events from that iframe's window AND the exact documented HTTPS provider
origins are accepted. The app polls a small sanitized host state object instead
of exposing an Android JavaScript interface to third-party pages. Non-finite,
negative and out-of-duration progress is rejected again natively.

No file/content access, cleartext traffic, mixed content, popup windows, camera
or microphone grants, TLS bypass or stream scraping. Fullscreen is a native
Activity, not dependent on provider fullscreen UI. SSL errors cancel the request.
Keep the operating system and WebView current; the app does not make an obsolete
Android 6 security stack safe for arbitrary websites.

Provider embedding may rely on features intentionally restricted here. Such
failures must be documented, not repaired by weakening security or bypassing
access controls. No stream download, DRM interception or provider secrets exist
in this project.
