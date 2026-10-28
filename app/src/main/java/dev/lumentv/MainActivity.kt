package dev.lumentv

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import android.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.*

class MainActivity : FragmentActivity() {
    private lateinit var rows: ArrayObjectAdapter
    private lateinit var fragment: RowsSupportFragment
    private lateinit var headline: TextView
    private lateinit var summary: TextView
    private var generation = 0
    private var section = "Home"
    private var query = ""
    private var page = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); Ui.setup(this)
        val root = Ui.column(this)
        val nav = Ui.row(this)
        nav.addView(Ui.text(this,"L U M E N",24f), LinearLayout.LayoutParams(0,-2,1f))
        listOf("Home","Movies","Series","Watchlist","Search","Settings").forEach { label -> nav.addView(Ui.button(this,label) {
            when(label) {
                "Settings" -> startActivity(Intent(this,SettingsActivity::class.java))
                "Search" -> searchDialog()
                else -> { section = label; page = 1; load() }
            }
        }) }
        root.addView(nav)
        headline = Ui.text(this,"Your next great watch.",28f); root.addView(headline)
        summary = Ui.text(this,"Browse with your remote. Select a title for details.",15f).apply { maxLines = 2 }; root.addView(summary)
        val container = FrameLayout(this).apply { id = View.generateViewId() }
        root.addView(container, LinearLayout.LayoutParams(-1,0,1f)); setContentView(root)
        fragment = RowsSupportFragment(); supportFragmentManager.beginTransaction().replace(container.id,fragment).commitNow()
        rows = ArrayObjectAdapter(ListRowPresenter().apply { shadowEnabled = false; selectEffectEnabled = false })
        fragment.adapter = rows
        fragment.setOnItemViewClickedListener { _, item, _, _ ->
            when(item) {
                is Title -> details(item)
                is Playback -> historyActions(item)
                is Command -> item.action()
            }
        }
        fragment.setOnItemViewSelectedListener { _, item, _, _ ->
            val t = when(item) { is Title -> item; is Playback -> item.title; else -> null }
            if(t != null) { headline.text = t.name; summary.text = t.overview.ifBlank { "${t.year} · ${t.type.uppercase()}" } }
        }
        if(savedInstanceState != null) { section = savedInstanceState.getString("section", "Home"); query = savedInstanceState.getString("query", ""); page = savedInstanceState.getInt("page",1) }
        nav.getChildAt(1).requestFocus()
    }
    override fun onResume() { super.onResume(); load() }
    override fun onSaveInstanceState(outState: Bundle) { outState.putString("section",section); outState.putString("query",query); outState.putInt("page",page); super.onSaveInstanceState(outState) }
    private fun details(t: Title) { startActivity(Intent(this,DetailsActivity::class.java).putExtra("title",t)) }
    private fun historyActions(p: Playback) {
        val episode = if(p.title.type == "tv") " · S${p.season} E${p.episode}" else ""
        AlertDialog.Builder(this).setTitle(p.title.name + episode).setItems(arrayOf("Resume ${format(p.seconds)}", "Start from beginning", "Remove from Continue Watching", "Open details")) { _, choice ->
            when(choice) {
                0 -> startActivity(Intent(this, PlayerActivity::class.java).putExtra("playback",p))
                1 -> { Store(this).removeHistory(p); startActivity(Intent(this, PlayerActivity::class.java).putExtra("playback",p.copy(seconds=0.0,duration=0.0))) }
                2 -> { Store(this).removeHistory(p); load() }
                3 -> details(p.title)
            }
        }.setNegativeButton("Cancel",null).show()
    }
    private fun format(value: Double): String { val s = value.toLong().coerceAtLeast(0); return if(s >= 3600) "%d:%02d:%02d".format(s / 3600,s / 60 % 60,s % 60) else "%d:%02d".format(s / 60,s % 60) }
    private fun row(label: String, items: List<Any>) { if(items.isEmpty()) return; val a = ArrayObjectAdapter(Cards()); items.forEach { a.add(it) }; rows.add(ListRow(HeaderItem(label),a)) }
    private fun searchDialog() {
        val field = EditText(this).apply { hint = "Movie or series title"; setSingleLine(); setText(query) }
        AlertDialog.Builder(this).setTitle("Search TMDB").setView(field).setPositiveButton("Search") { _, _ ->
            if(field.text.toString().isNotBlank()) { query = field.text.toString().trim(); section = "Search"; page = 1; load() }
        }.setNegativeButton("Cancel",null).show()
    }
    private fun load() {
        val current = ++generation; rows.clear(); headline.text = section; summary.text = "Loading…"
        val store = Store(this)
        if(section == "Home") row("Continue watching",store.history())
        if(section == "Watchlist") {
            row("My watchlist",store.watchlist()); summary.text = if(store.watchlist().isEmpty()) "Your watchlist is empty. Add a title from its details screen." else "Saved on this television."
            if(store.watchlist().isEmpty()) row("Discover", listOf(Command("Browse movies") { section = "Movies"; load() }))
            return
        }
        if(store.token.isBlank()) {
            summary.text = "Add your free TMDB key in Settings to discover movies and series. You can test playback now."
            row("Get started",listOf(Command("Settings") { startActivity(Intent(this,SettingsActivity::class.java)) },Command("Test player") { startActivity(Intent(this,PlayerActivity::class.java).putExtra("demo",true)) }))
            return
        }
        val selectedSection = section; val selectedPage = page; val selectedQuery = query
        Ui.task(this, {
            val api = Tmdb(this)
            when(selectedSection) {
                "Home" -> listOf("Trending movies" to api.titles("trending/movie/week"), "Trending series" to api.titles("trending/tv/week",fallback="tv"))
                "Movies" -> listOf("Popular movies · page $selectedPage" to api.titles("movie/popular","&page=$selectedPage"))
                "Series" -> listOf("Popular series · page $selectedPage" to api.titles("tv/popular","&page=$selectedPage", "tv"))
                else -> listOf("Results for $selectedQuery · page $selectedPage" to api.search(selectedQuery,selectedPage))
            }
        }, { result ->
            if(current != generation) return@task
            result.forEach { (label,titles) -> row(label,titles) }
            summary.text = if(result.all { it.second.isEmpty() }) "No titles found." else "Select a title · Back returns to TV home · Metadata by TMDB"
            if(selectedSection != "Home") row("Pages", buildList {
                if(page > 1) add(Command("Previous page") { page--; load() })
                if(result.any { it.second.isNotEmpty() } && page < 500) add(Command("Next page") { page++; load() })
            })
        }, { error -> if(current == generation) { summary.text = error; row("Try again",listOf(Command("Retry") { load() },Command("Settings") { startActivity(Intent(this,SettingsActivity::class.java)) })) } })
    }
    override fun onTrimMemory(level: Int) { super.onTrimMemory(level); if(level >= 10) Posters.trim() }
}
