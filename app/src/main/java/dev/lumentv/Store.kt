package dev.lumentv

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** Small bounded private local store; no accounts, analytics or cloud synchronization. */
class Store(context: Context) {
    private val prefs = context.getSharedPreferences("library", Context.MODE_PRIVATE)
    var token: String
        get() = prefs.getString("tmdb", "") ?: ""
        set(value) { prefs.edit().putString("tmdb", value.trim()).apply() }
    var providerAllowed: Boolean
        get() = prefs.getBoolean("providerAllowed", false)
        set(value) { prefs.edit().putBoolean("providerAllowed", value).apply() }
    var provider: String
        get() {
            val saved = prefs.getString("provider", null)
            val migrated = prefs.getBoolean("providerDefaultV3", false)
            val value = saved?.takeIf { it in Framex.providers }
            if(!migrated) {
                val preferred = if(value == null || value == "auto") "pekka" else value
                prefs.edit().putString("provider", preferred).putBoolean("providerDefaultV3", true).apply()
                return preferred
            }
            return value ?: "pekka"
        }
        set(value) { require(value in Framex.providers); prefs.edit().putString("provider", value).putBoolean("providerDefaultV3", true).apply() }
    private fun array(key: String) = runCatching { JSONArray(prefs.getString(key, "[]")) }.getOrDefault(JSONArray())
    private fun title(o: JSONObject) = Title(o.getInt("id"), o.getString("type"), o.getString("name"), o.optString("overview"), o.optString("poster"), o.optString("year"), o.optDouble("rating", 0.0))
    private fun json(t: Title) = JSONObject().put("id", t.id).put("type", t.type).put("name", t.name).put("overview", t.overview).put("poster", t.poster).put("year", t.year).put("rating", t.rating)
    fun watchlist(): List<Title> { val a = array("watchlist"); return (0 until a.length()).mapNotNull { runCatching { title(a.getJSONObject(it)) }.getOrNull() } }
    fun contains(t: Title) = watchlist().any { it.key == t.key }
    fun toggle(t: Title): Boolean {
        val list = watchlist().toMutableList(); val removed = list.removeAll { it.key == t.key }
        if (!removed) list.add(0, t)
        prefs.edit().putString("watchlist", JSONArray(list.take(200).map { json(it) }).toString()).apply()
        return !removed
    }
    fun history(): List<Playback> { val a = array("history"); return (0 until a.length()).mapNotNull { runCatching { val o = a.getJSONObject(it); Playback(title(o), o.optInt("season"), o.optInt("episode"), o.getDouble("seconds"), o.getDouble("duration")) }.getOrNull() }.filter { it.resumable } }
    fun resume(t: Title, season: Int = 0, episode: Int = 0): Playback = history().firstOrNull { it.title.key == t.key && it.season == season && it.episode == episode } ?: Playback(t, season, episode)
    fun save(p: Playback) {
        if (!ProgressPolicy.valid(p.seconds, p.duration)) return
        val list = history().filter { it.key != p.key }.toMutableList()
        if (p.resumable) list.add(0, p)
        prefs.edit().putString("history", JSONArray(list.take(100).map { json(it.title).put("season", it.season).put("episode", it.episode).put("seconds", it.seconds).put("duration", it.duration) }).toString()).apply()
    }
    fun removeHistory(p: Playback) {
        val list = history().filter { it.key != p.key }
        prefs.edit().putString("history", JSONArray(list.map { json(it.title).put("season", it.season).put("episode", it.episode).put("seconds", it.seconds).put("duration", it.duration) }).toString()).apply()
    }
    fun clearHistory() { prefs.edit().remove("history").apply() }
}
