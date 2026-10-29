package dev.lumentv

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.content.Intent
import android.widget.*

class SettingsActivity : Activity() {
    private fun about(text: String) {
        val content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(32,16,32,16) }
        content.addView(ImageView(this).apply { setImageResource(R.drawable.tmdb_logo); contentDescription = "TMDB"; scaleType = ImageView.ScaleType.FIT_START },LinearLayout.LayoutParams(Ui.dp(this,100),Ui.dp(this,48)))
        content.addView(Ui.text(this,text,16f))
        AlertDialog.Builder(this).setTitle("Lumen TV · About").setView(ScrollView(this).apply { addView(content) }).setPositiveButton("Close",null).show()
    }
    override fun onCreate(state: Bundle?) {
        super.onCreate(state); Ui.setup(this)
        val store = Store(this); val root = Ui.column(this)
        root.addView(Ui.text(this,"Make yourself at home.",28f))
        root.addView(Ui.text(this,"Lumen TV · 0.8.0 · Android ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})",15f))
        val key = Ui.button(this,if(store.token.isBlank()) "Add TMDB key" else "Replace TMDB key") {
            val field = EditText(this).apply { hint = "TMDB API Read Access Token or v3 API key"; setSingleLine(); inputType = 129 }
            AlertDialog.Builder(this).setTitle("TMDB key").setMessage("Create a free personal key at themoviedb.org/settings/api. Stored privately on this TV. Never included in diagnostics.").setView(field)
                .setPositiveButton("Save") { _, _ -> if(field.text.toString().isNotBlank()) { store.token = field.text.toString(); Toast.makeText(this,"Key saved",Toast.LENGTH_SHORT).show() } }
                .setNeutralButton("Remove key") { _, _ -> store.token = "" }.setNegativeButton("Cancel",null).show()
        }
        root.addView(key)
        val allowed = CheckBox(this).apply { text = "Enable FramexTV · I have permission to access the content I play"; isChecked = store.providerAllowed; textSize = 17f; setTextColor(android.graphics.Color.WHITE); setOnCheckedChangeListener { _, checked -> store.providerAllowed = checked } }; root.addView(allowed)
        root.addView(Ui.button(this,"Preferred source: ${Framex.providerLabels[store.provider] ?: store.provider}") {
            AlertDialog.Builder(this).setTitle("Preferred FramexTV source").setMessage("FramexTV does not provide Lumen with a direct resolution control. The source influences available quality; adaptive playback may still change it.").setItems(Framex.providers.map { Framex.providerLabels[it] ?: it }.toTypedArray()) { _, i -> store.provider = Framex.providers[i]; recreate() }.setNegativeButton("Cancel",null).show()
        })
        root.addView(Ui.button(this,"Test player · offline diagnostic clip") { startActivity(Intent(this,PlayerActivity::class.java).putExtra("demo",true)) })
        root.addView(Ui.button(this,"Clear Continue Watching") { AlertDialog.Builder(this).setTitle("Clear history?").setMessage("Remove saved playback positions on this TV?").setPositiveButton("Clear") { _, _ -> store.clearHistory() }.setNegativeButton("Cancel",null).show() })
        root.addView(Ui.button(this,"About, licences & privacy") { about("Open-source app: MIT licence. AndroidX / Media3: Apache 2.0; Kotlin: Apache 2.0. This product uses the TMDB API but is not endorsed or certified by TMDB. TMDB data and images remain subject to TMDB terms. FramexTV is an independent external service, not included in this software's licence. Its embedding documentation does not establish video distribution rights. No paid hosting or analytics. TMDB requests include your token and search terms; FramexTV receives the selected title ID, IP and browser information. Watchlist and progress stay on this TV. Android WebView may store provider site data. Uninstall or clear app data to remove all settings and local data. Keep Android System WebView updated. Playback on old WebView versions may fail. The bundled diagnostic clip is generated test patterns, not a film.") })
        val attribution = Ui.row(this)
        val logo = ImageView(this).apply { setImageResource(R.drawable.tmdb_logo); contentDescription = "TMDB"; scaleType = ImageView.ScaleType.FIT_CENTER }
        attribution.addView(logo, LinearLayout.LayoutParams(Ui.dp(this,80),Ui.dp(this,40)))
        attribution.addView(Ui.text(this,"This product uses the TMDB API but is not endorsed or certified by TMDB.",12f))
        root.addView(Ui.button(this,"Open-source licence texts") { about(resources.openRawResource(R.raw.licences).bufferedReader().use { it.readText() }) })
        root.addView(attribution)
        root.addView(Ui.button(this,"Back") { finish() })
        setContentView(ScrollView(this).apply { addView(root) }); key.requestFocus()
    }
}
