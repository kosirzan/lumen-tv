package dev.lumentv

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.os.Build
import android.view.KeyEvent
import android.view.Gravity
import android.view.View
import android.webkit.*
import android.widget.*
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import org.json.JSONObject

/** Native remote controls operate only the provider's documented postMessage API. */
class PlayerActivity : Activity() {
    private var playback: Playback? = null
    private var demo = false
    private var web: WebView? = null
    private var native: ExoPlayer? = null
    private lateinit var surface: FrameLayout
    private lateinit var status: TextView
    private lateinit var playButton: Button
    private lateinit var controlsPanel: LinearLayout
    private val handler = Handler(Looper.getMainLooper())
    private var paused = false
    private var active = false
    private var seconds = 0.0
    private var duration = 0.0
    private var lastTelemetry = 0L
    private var telemetrySequence = 0L
    private var started = 0L
    private var savedAt = 0L
    private var error = "None"
    private var webVersion = "Unknown"
    private var provider = "auto"
    private var muted = false
    private var pendingPoll = false
    private var initialFocus = false
    private var frameRequests = 0
    private var frameHttpError = "None"
    private var consoleErrors = 0
    private var renderProcessLost = false
    private var controlsVisible = true
    private val hideControls = Runnable { setControlsVisible(false) }
    private val tick = object : Runnable {
        override fun run() {
            if(!active) return
            native?.let { seconds = it.currentPosition / 1000.0; duration = it.duration.coerceAtLeast(0) / 1000.0; lastTelemetry = SystemClock.elapsedRealtime(); renderStatus() }
            if(web != null && !pendingPoll) {
                pendingPoll = true
                web?.evaluateJavascript("JSON.stringify(window.framexState || {})") { raw ->
                    pendingPoll = false
                    if(active) runCatching {
                        val decoded = org.json.JSONTokener(raw).nextValue() as? String ?: "{}"
                        val state = JSONObject(decoded)
                        val time = state.optDouble("currentTime",Double.NaN); val length = state.optDouble("duration",Double.NaN)
                        if(ProgressPolicy.valid(time,length) && state.optLong("sequence") > telemetrySequence) { telemetrySequence = state.optLong("sequence"); seconds = time; duration = length; lastTelemetry = SystemClock.elapsedRealtime() }
                    }
                    if(active) renderStatus()
                }
            }
            val now = SystemClock.elapsedRealtime()
            if(now - savedAt >= 5000) { save(); savedAt = now }
            if(!paused && web != null && now - (if(lastTelemetry == 0L) started else lastTelemetry) > 30000) status.text = "No playback progress received. Open Diagnostics; update WebView or try another provider."
            handler.postDelayed(this,2000)
        }
    }
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(state: Bundle?) {
        super.onCreate(state); Ui.setup(this)
        demo = intent.getBooleanExtra("demo",false)
        @Suppress("DEPRECATION") val item = intent.getSerializableExtra("playback") as? Playback
        playback = item
        if(!demo && item == null) { finish(); return }
        if(!demo && !Store(this).providerAllowed) {
            Ui.message(this,"Playback permission required","Enable FramexTV in Settings only for content you have permission to access.")
            val root = Ui.column(this); root.addView(Ui.text(this,"FramexTV is disabled")); root.addView(Ui.button(this,"Back") { finish() }); setContentView(root); return
        }
        seconds = state?.getDouble("seconds") ?: item?.seconds ?: 0.0
        duration = state?.getDouble("duration") ?: item?.duration ?: 0.0
        provider = Store(this).provider
        val root = FrameLayout(this).apply { setBackgroundColor(Color.BLACK) }
        surface = FrameLayout(this).apply { descendantFocusability = android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS }; root.addView(surface,FrameLayout.LayoutParams(-1,-1))
        status = Ui.text(this,if(demo) "Offline playback diagnostic" else item!!.label,15f).apply { setBackgroundColor(0xCC000000.toInt()); setPadding(Ui.dp(this@PlayerActivity,24),Ui.dp(this@PlayerActivity,10),Ui.dp(this@PlayerActivity,24),Ui.dp(this@PlayerActivity,10)) }
        root.addView(status,FrameLayout.LayoutParams(-1,-2,Gravity.TOP))
        val controls = Ui.row(this).apply { gravity = Gravity.CENTER }
        controls.addView(playerButton("−10s") { seek(-10.0) })
        playButton = playerButton("Pause") { togglePause() }; controls.addView(playButton)
        controls.addView(playerButton("+10s") { seek(10.0) })
        controls.addView(playerButton("Audio") { muted = !muted; native?.volume = if(muted) 0f else 1f; command(JSONObject().put("action","setVolume").put("volume",if(muted) 0 else 1)) })
        if(!demo) {
            controls.addView(playerButton("Source") { chooseProvider() })
            controls.addView(playerButton("Subtitles") { chooseSubtitle() })
        }
        controls.addView(playerButton("Retry") { retry() })
        controls.addView(playerButton("Diagnostics") { diagnostics() })
        controls.addView(playerButton("Back") { finish() })
        controlsPanel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; setBackgroundColor(0xD9000000.toInt())
            setPadding(Ui.dp(this@PlayerActivity,16),Ui.dp(this@PlayerActivity,8),Ui.dp(this@PlayerActivity,16),Ui.dp(this@PlayerActivity,8))
            addView(Ui.text(this@PlayerActivity,"Remote: ◀/▶ seek 10s · OK play/pause · any navigation key shows controls",13f).apply { gravity = Gravity.CENTER })
            addView(HorizontalScrollView(this@PlayerActivity).apply { isFocusable = false; addView(controls) })
        }
        root.addView(controlsPanel,FrameLayout.LayoutParams(-1,-2,Gravity.BOTTOM))
        setContentView(root)
        started = SystemClock.elapsedRealtime()
        if(demo) setupNative() else setupWeb()
        playButton.requestFocus()
        scheduleControlsHide()
    }
    private fun playerButton(label: String, action: () -> Unit): Button = Ui.button(this,label) { showControls(); action(); scheduleControlsHide() }
    private fun setControlsVisible(visible: Boolean) {
        if(!::controlsPanel.isInitialized || !::status.isInitialized) return
        controlsVisible = visible
        controlsPanel.visibility = if(visible) View.VISIBLE else View.GONE
        status.visibility = if(visible || error != "None") View.VISIBLE else View.GONE
        if(visible) playButton.post { if(currentFocus?.isShown != true) playButton.requestFocus() }
    }
    private fun showControls() { handler.removeCallbacks(hideControls); setControlsVisible(true) }
    private fun scheduleControlsHide() { handler.removeCallbacks(hideControls); handler.postDelayed(hideControls,5000) }
    private fun setupNative() {
        native = ExoPlayer.Builder(this).build().apply player@ {
            val view = PlayerView(this@PlayerActivity).apply { player = this@player; useController = false; isFocusable = false }
            surface.addView(view,FrameLayout.LayoutParams(-1,-1))
            addListener(object : Player.Listener {
                override fun onPlayerError(e: PlaybackException) { error = "${e.errorCodeName}: ${e.message}"; status.text = "Playback failed. Open Diagnostics or Retry." }
            })
            setMediaItem(MediaItem.fromUri(Uri.parse("android.resource://$packageName/${R.raw.diagnostic}")))
            seekTo((seconds * 1000).toLong()); prepare(); playWhenReady = !paused
        }
    }
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWeb() {
        muted = false
        try {
            val w = WebView(this); web = w
            w.settings.apply {
                javaScriptEnabled = true; domStorageEnabled = true
                mediaPlaybackRequiresUserGesture = false
                allowFileAccess = false; allowContentAccess = false
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                setSupportMultipleWindows(false); javaScriptCanOpenWindowsAutomatically = false
                builtInZoomControls = false; displayZoomControls = false
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) safeBrowsingEnabled = true
            }
            // FramexTV documents an iframe integration. Match normal browser embed
            // cookie behavior; the WebView remains isolated to this app's data.
            CookieManager.getInstance().setAcceptCookie(true)
            CookieManager.getInstance().setAcceptThirdPartyCookies(w,true)
            w.isFocusable = false; w.isFocusableInTouchMode = false
            webVersion = w.settings.userAgentString.substringAfter("Chrome/","unknown").substringBefore(" ")
            w.webChromeClient = object : WebChromeClient() {
                override fun onPermissionRequest(request: PermissionRequest) { request.deny() }
                override fun onConsoleMessage(message: ConsoleMessage): Boolean {
                    // Count errors without retaining provider messages, URLs or tokens.
                    if(message.messageLevel() == ConsoleMessage.MessageLevel.ERROR) consoleErrors++
                    return true
                }
            }
            w.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean = request.isForMainFrame
                @Deprecated("Legacy WebView compatibility") override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    // API 23 does not expose isForMainFrame. Permit only the provider's
                    // HTTPS redirect aliases; external navigation stays blocked.
                    val uri = runCatching { Uri.parse(url) }.getOrNull()
                    return uri?.scheme != "https" || uri.host !in Framex.allowedHosts
                }
                override fun onLoadResource(view: WebView, url: String) {
                    if(runCatching { Uri.parse(url).host in Framex.allowedHosts }.getOrDefault(false)) frameRequests++
                }
                override fun onReceivedError(view: WebView, request: WebResourceRequest, e: WebResourceError) {
                    if(request.url.host in Framex.allowedHosts) { error = "Provider resource error ${e.errorCode}"; status.text = "Provider connection failed. Open Diagnostics or Retry."; showControls() }
                }
                override fun onReceivedHttpError(view: WebView, request: WebResourceRequest, response: WebResourceResponse) {
                    if(request.url.host in Framex.allowedHosts) { frameHttpError = "HTTP ${response.statusCode}" }
                }
                override fun onReceivedSslError(view: WebView, ssl: android.webkit.SslErrorHandler, e: android.net.http.SslError) { ssl.cancel(); error = "TLS certificate validation failed"; status.text = "Secure connection failed. Check the TV date/time."; showControls() }
                @android.annotation.TargetApi(Build.VERSION_CODES.O)
                override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
                    renderProcessLost = true
                    error = if(detail.didCrash()) "WebView renderer crashed" else "WebView renderer was reclaimed"
                    web = null
                    surface.removeView(view)
                    status.text = "Player stopped. Open Diagnostics, then Retry."
                    showControls()
                    return true
                }
            }
            val p = playback!!.copy(seconds=seconds,duration=duration)
            val url = Framex.embed(p,provider,autoplay=!paused)
            // The host is ours, the iframe is cross-origin; no addJavascriptInterface is exposed.
            val html = """<!doctype html><html><head><meta name="viewport" content="width=device-width,initial-scale=1"><meta http-equiv="Content-Security-Policy" content="default-src 'none'; frame-src https://framextv.cc https://www.framextv.cc https://framextv.xyz https://www.framextv.xyz; script-src 'unsafe-inline'; style-src 'unsafe-inline'"><style>html,body,iframe{margin:0;width:100%;height:100%;border:0;background:#000;overflow:hidden}</style></head><body><iframe id="player" src="$url" allow="autoplay; fullscreen; encrypted-media; picture-in-picture" allowfullscreen></iframe><script>
            (function(){var f=document.getElementById('player'),origins=['https://framextv.cc','https://www.framextv.cc','https://framextv.xyz','https://www.framextv.xyz'];window.framexState={};window.sendFramex=function(c){for(var i=0;i<origins.length;i++)f.contentWindow.postMessage(c,origins[i]);};window.addEventListener('message',function(e){if(e.source!==f.contentWindow||origins.indexOf(e.origin)<0)return;var d=e.data;if(d&&d.event==='frameXTV:timeupdate'&&typeof d.currentTime==='number'&&typeof d.duration==='number'&&isFinite(d.currentTime)&&isFinite(d.duration)){window.framexState={currentTime:d.currentTime,duration:d.duration,sequence:(window.framexState.sequence||0)+1};}});})();
            </script></body></html>"""
            surface.addView(w,FrameLayout.LayoutParams(-1,-1))
            w.loadDataWithBaseURL("https://appassets.androidplatform.net/player/",html,"text/html","UTF-8",null)
        } catch (_: Exception) { error = "WebView unavailable"; status.text = "Android System WebView could not start. Update/enable it in TV settings."; showControls() }
    }
    private fun command(json: JSONObject) { web?.evaluateJavascript("window.sendFramex && window.sendFramex($json)",null) }
    private fun togglePause() { paused = !paused; native?.playWhenReady = !paused; command(JSONObject().put("action",if(paused) "pause" else "play")); playButton.text = if(paused) "Play" else "Pause"; showControls(); scheduleControlsHide() }
    private fun seek(offset: Double) { native?.let { it.seekTo((it.currentPosition + (offset * 1000).toLong()).coerceIn(0,it.duration.coerceAtLeast(0))) }; command(JSONObject().put("action","seekBy").put("seconds",offset)) }
    private fun chooseProvider() {
        handler.removeCallbacks(hideControls)
        val labels = Framex.providers.map { Framex.providerLabels[it] ?: it }
        AlertDialog.Builder(this).setTitle("Stream source").setMessage("FramexTV does not expose a direct quality command. Source selection influences available quality; the provider may still adapt it to bandwidth and device support.").setSingleChoiceItems(labels.toTypedArray(),Framex.providers.indexOf(provider)) { dialog, i ->
            provider = Framex.providers[i]; Store(this).provider = provider; dialog.dismiss(); retry(); showControls(); scheduleControlsHide()
        }.setNegativeButton("Cancel") { _, _ -> scheduleControlsHide() }.setOnCancelListener { scheduleControlsHide() }.show()
    }
    private fun chooseSubtitle() {
        handler.removeCallbacks(hideControls)
        val tracks = (0..7).map { "Subtitle track ${it + 1}" }.toTypedArray()
        AlertDialog.Builder(this).setTitle("Subtitles").setMessage("FramexTV exposes track numbers but not their language names. Choose a track; if it is unavailable, try the next one.").setItems(tracks) { _, index ->
            command(JSONObject().put("action","setSubtitle").put("index",index)); showControls(); scheduleControlsHide()
        }.setNegativeButton("Cancel") { _, _ -> scheduleControlsHide() }.setOnCancelListener { scheduleControlsHide() }.show()
    }
    private fun renderStatus() { if(error != "None") return; val label = if(demo) "Offline diagnostic" else playback!!.label; status.text = "$label  ·  ${format(seconds)} / ${format(duration)}" }
    private fun format(value: Double): String { val s = value.toLong().coerceAtLeast(0); return "%d:%02d:%02d".format(s / 3600, s / 60 % 60, s % 60) }
    private fun save() { if(!demo) playback?.let { Store(this).save(it.copy(seconds=seconds,duration=duration)) } }
    private fun retry() { save(); release(); surface.removeAllViews(); error = "None"; frameHttpError = "None"; frameRequests = 0; consoleErrors = 0; renderProcessLost = false; pendingPoll = false; lastTelemetry = 0; telemetrySequence = 0; started = SystemClock.elapsedRealtime(); paused = false; muted = false; playButton.text = "Pause"; if(demo) setupNative() else setupWeb(); if(active) { handler.removeCallbacks(tick); handler.post(tick) } }
    private fun diagnostics() {
        val memory = android.app.ActivityManager.MemoryInfo(); (getSystemService(ACTIVITY_SERVICE) as android.app.ActivityManager).getMemoryInfo(memory)
        val age = if(lastTelemetry == 0L) "None" else "${(SystemClock.elapsedRealtime() - lastTelemetry) / 1000}s ago"
        val text = "Mode: ${if(demo) "Bundled H.264/AAC clip · native Media3" else "FramexTV documented iframe"}\nAndroid: ${android.os.Build.VERSION.RELEASE} / API ${android.os.Build.VERSION.SDK_INT}\nWebView Chrome: $webVersion\nEmbed origin: ${Framex.ORIGIN}\nProvider: $provider\nProgress: ${format(seconds)} / ${format(duration)}\nLast telemetry: $age\nProvider resources seen: $frameRequests\nProvider HTTP error: $frameHttpError\nConsole errors (contents not retained): $consoleErrors\nRenderer lost: $renderProcessLost\nAvailable memory: ${memory.availMem / 1048576} MiB\nLow memory: ${memory.lowMemory}\nError: $error\n\nProvider commands have no documented acknowledgement. The Play/Pause label shows requested state. No video extraction or DRM bypass is performed. If native test succeeds but FramexTV fails, check WebView compatibility, provider availability and permissions."
        AlertDialog.Builder(this).setTitle("Player diagnostics").setMessage(text).setPositiveButton("Close",null).show()
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus && !initialFocus && ::playButton.isInitialized) {
            initialFocus = true
            playButton.post { playButton.requestFocus() }
        }
    }
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if(::playButton.isInitialized && event.action == KeyEvent.ACTION_DOWN) {
            when(event.keyCode) {
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> { togglePause(); return true }
                KeyEvent.KEYCODE_MEDIA_PLAY -> { if(paused) togglePause(); showControls(); scheduleControlsHide(); return true }
                KeyEvent.KEYCODE_MEDIA_PAUSE -> { if(!paused) togglePause(); showControls(); scheduleControlsHide(); return true }
                KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> { showControls(); seek(10.0); scheduleControlsHide(); return true }
                KeyEvent.KEYCODE_MEDIA_REWIND -> { showControls(); seek(-10.0); scheduleControlsHide(); return true }
                KeyEvent.KEYCODE_MENU -> { showControls(); diagnostics(); return true }
            }
            if(!controlsVisible) when(event.keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> { showControls(); seek(-10.0); scheduleControlsHide(); return true }
                KeyEvent.KEYCODE_DPAD_RIGHT -> { showControls(); seek(10.0); scheduleControlsHide(); return true }
                KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_NUMPAD_ENTER -> { showControls(); togglePause(); return true }
                KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN -> { showControls(); scheduleControlsHide(); return true }
            }
            if(event.keyCode != KeyEvent.KEYCODE_BACK) { showControls(); scheduleControlsHide() }
        }
        return super.dispatchKeyEvent(event)
    }
    override fun onResume() { super.onResume(); active = true; if(::playButton.isInitialized) { if(!demo && web == null && error == "None") { telemetrySequence = 0; started = SystemClock.elapsedRealtime(); setupWeb() }; web?.onResume(); native?.playWhenReady = !paused; if(!paused) command(JSONObject().put("action","play")); handler.post(tick); showControls(); scheduleControlsHide() } }
    override fun onPause() { active = false; handler.removeCallbacks(tick); handler.removeCallbacks(hideControls); native?.pause(); command(JSONObject().put("action","pause")); save(); web?.let { surface.removeView(it); it.stopLoading(); it.destroy() }; web = null; pendingPoll = false; super.onPause() }
    override fun onSaveInstanceState(outState: Bundle) { outState.putDouble("seconds",seconds); outState.putDouble("duration",duration); super.onSaveInstanceState(outState) }
    private fun release() { native?.release(); native = null; web?.let { surface.removeView(it); it.stopLoading(); it.loadUrl("about:blank"); it.destroy() }; web = null }
    override fun onDestroy() { handler.removeCallbacksAndMessages(null); if(::surface.isInitialized) release(); super.onDestroy() }
}
