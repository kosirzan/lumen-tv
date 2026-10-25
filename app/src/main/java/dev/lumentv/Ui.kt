package dev.lumentv

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.content.res.ColorStateList
import android.view.View
import android.view.WindowManager
import android.widget.*
import java.util.concurrent.Executors
import android.os.Handler
import android.os.Looper

object Ui {
    val BG = Color.rgb(12,16,25); val PANEL = Color.rgb(25,34,48); val ACCENT = Color.rgb(99,230,207)
    val io = Executors.newFixedThreadPool(2)
    val main = Handler(Looper.getMainLooper())
    fun dp(c: Context, n: Int) = (c.resources.displayMetrics.density * n).toInt()
    fun setup(a: Activity) { a.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY; a.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
    fun bg(color: Int = PANEL, stroke: Boolean = false) = GradientDrawable().apply { setColor(color); cornerRadius = 12f; if(stroke) setStroke(3, ACCENT) }
    fun text(c: Context, value: String, size: Float = 18f) = TextView(c).apply { text = value; textSize = size; setTextColor(Color.WHITE); setPadding(0, dp(c,6), 0, dp(c,6)) }
    fun column(c: Context) = LinearLayout(c).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(BG); setPadding(dp(c,40),dp(c,24),dp(c,40),dp(c,24)) }
    fun button(c: Context, label: String, action: () -> Unit) = Button(c).apply {
        text = label; textSize = 15f; isAllCaps = false; isFocusable = true; isFocusableInTouchMode = true; minHeight = dp(c,44)
        setPadding(dp(c,16),0,dp(c,16),0)
        backgroundTintList = ColorStateList(arrayOf(intArrayOf(android.R.attr.state_focused), intArrayOf()), intArrayOf(ACCENT, PANEL))
        setTextColor(ColorStateList(arrayOf(intArrayOf(android.R.attr.state_focused), intArrayOf()), intArrayOf(BG, Color.WHITE)))
        setOnClickListener { action() }
    }
    fun row(c: Context) = LinearLayout(c).apply { orientation = LinearLayout.HORIZONTAL }
    fun <T> task(a: Activity, work: () -> T, success: (T) -> Unit, failure: (String) -> Unit) {
        io.execute { try { val value = work(); main.post { if(!a.isFinishing && !a.isDestroyed) success(value) } } catch(e: Exception) { main.post { if(!a.isFinishing && !a.isDestroyed) failure(e.message ?: "Request failed") } } }
    }
    fun message(c: Context, title: String, message: String) { android.app.AlertDialog.Builder(c).setTitle(title).setMessage(message).setPositiveButton("OK", null).show() }
}
