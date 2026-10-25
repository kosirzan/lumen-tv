# Continuation checkpoint

The app must not be presented as a verified end-to-end streaming release until
live metadata and permitted provider playback have passed, including physical TV
checks. Current test evidence is in TESTING.md and the evidence directory.

## GitHub publication

The user created the public repository `kosirzan/lumen-tv`. Source, documentation,
test evidence and the existing debug APK are being uploaded through the connected
GitHub account. The APK lives in `downloads/`; its build-time validation is
recorded in TESTING.md. GitHub Actions results must be checked separately; the
prior cloud build and emulator tests do not prove that a GitHub run succeeded.

The original three local development checkpoints are preserved in
`downloads/lumen-tv.bundle`. Restore them with:

```sh
git clone downloads/lumen-tv.bundle lumen-tv-original-history
```

Never publish a TMDB token, local.properties, private signing key or emulator data.
Do not commit cached provider scripts or stream links.

## Outstanding functional validation

1. Configure the user's own permitted TMDB key; verify discovery, search, pagination,
   seasons/episodes and posters against the live API.
2. Confirm FramexTV permission for the intended content and service use; then test
   documented embeds on a current WebView and the actual Philips TV. Check first
   frame, sound, subtitles, provider switch, commands, telemetry and resume.
3. Verify source/origin event filtering and host controls on the TV's WebView.
4. Run the physical TV checklist in TESTING.md and record firmware/WebView versions.
5. Fix failures, rerun affected checks, preserve a stable signing key for updates.

Do not infer playback success from a successful HTTP response, APK build, emulator
launch, native diagnostic clip, or source code alone. If FramexTV cannot run on the
TV's supported WebView, document that hardware/platform limitation honestly.

## Publication attempt: repository access still required

On the first upload attempt, both Git blob creation and a normal Contents API
file creation returned HTTP 403, `Resource not accessible by integration`.
No files were written remotely. The GitHub app installation is configured for
selected repositories; ensure `kosirzan/lumen-tv` is selected in installation
162468732 before retrying. Account-owner push permission does not prove that the
app's installation token can write. Recheck the remote main head before publishing
because the owner may have changed the initial README meanwhile.
