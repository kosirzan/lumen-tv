package dev.lumentv

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*

class DetailsActivity : Activity() {
    private lateinit var title: Title
    private lateinit var watch: Button
    override fun onCreate(state: Bundle?) {
        super.onCreate(state); Ui.setup(this)
        @Suppress("DEPRECATION") val item = intent.getSerializableExtra("title") as? Title
        if(item == null) { finish(); return }; title = item
        val root = Ui.column(this); val body = Ui.row(this)
        val poster = ImageView(this).apply { scaleType = ImageView.ScaleType.FIT_CENTER; setBackgroundColor(Ui.PANEL) }
        body.addView(poster,LinearLayout.LayoutParams(Ui.dp(this,170),Ui.dp(this,255)).apply { rightMargin = Ui.dp(this@DetailsActivity,28) }); Posters.load(poster,title.poster)
        val info = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        info.addView(Ui.text(this,title.name,30f)); info.addView(Ui.text(this,"${title.year}  ·  ${title.type.uppercase()}  ·  TMDB %.1f / 10".format(title.rating),16f))
        val description = ScrollView(this).apply { isFocusable = true; addView(Ui.text(this@DetailsActivity,title.overview.ifBlank { "No description available." },18f)) }
        info.addView(description,LinearLayout.LayoutParams(-1,0,1f)); body.addView(info,LinearLayout.LayoutParams(0,Ui.dp(this,255),1f)); root.addView(body)
        val buttons = Ui.row(this)
        val play = Ui.button(this,if(title.type == "tv") "Choose episode" else if(Store(this).resume(title).resumable) "Resume" else "Play") { if(title.type == "tv") seasons() else play(Store(this).resume(title)) }
        buttons.addView(play)
        watch = Ui.button(this,"") { Store(this).toggle(title); watchLabel() }; watchLabel(); buttons.addView(watch)
        buttons.addView(Ui.button(this,"Back") { finish() }); root.addView(buttons)
        root.addView(Ui.text(this,"Playback availability is controlled by the provider. Only play content you have permission to access.",14f)); setContentView(root); play.requestFocus()
    }
    private fun watchLabel() { watch.text = if(Store(this).contains(title)) "Remove from watchlist" else "+ Watchlist" }
    private fun play(p: Playback) { startActivity(Intent(this,PlayerActivity::class.java).putExtra("playback",p)) }
    private fun seasons() {
        Ui.task(this,{ Tmdb(this).request("tv/${title.id}").getJSONArray("seasons") }, { array ->
            val values = (0 until array.length()).map { array.getJSONObject(it) }.filter { it.optInt("season_number") > 0 && it.optInt("episode_count") > 0 }
            if(values.isEmpty()) Ui.message(this,"Episodes","No seasons available.") else AlertDialog.Builder(this).setTitle("Choose season").setItems(values.map { it.optString("name") }.toTypedArray()) { _, i -> episodes(values[i].getInt("season_number")) }.setNegativeButton("Back",null).show()
        }, { Ui.message(this,"Unable to load seasons",it) })
    }
    private fun episodes(season: Int) {
        Ui.task(this,{ Tmdb(this).request("tv/${title.id}/season/$season").getJSONArray("episodes") }, { array ->
            val values = (0 until array.length()).map { array.getJSONObject(it) }
            if(values.isEmpty()) Ui.message(this,"Episodes","No episodes available.") else AlertDialog.Builder(this).setTitle("Season $season · Choose episode").setItems(values.map { "${it.getInt("episode_number")}. ${it.optString("name")}" }.toTypedArray()) { _, i -> play(Store(this).resume(title,season,values[i].getInt("episode_number"))) }.setNegativeButton("Back",null).show()
        }, { Ui.message(this,"Unable to load episodes",it) })
    }
}
