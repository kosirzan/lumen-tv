package dev.lumentv

import java.io.Serializable

/** TMDB identifiers are meaningful only together with their media type. */
data class Title(val id: Int, val type: String, val name: String, val overview: String = "", val poster: String = "", val year: String = "", val rating: Double = 0.0) : Serializable {
    init { require(id > 0 && type in listOf("movie", "tv")) }
    val key: String get() = "$type:$id"
}
data class Playback(val title: Title, val season: Int = 0, val episode: Int = 0, val seconds: Double = 0.0, val duration: Double = 0.0) : Serializable {
    init { require(if (title.type == "tv") season > 0 && episode > 0 else season == 0 && episode == 0) }
    val key: String get() = "${title.key}:$season:$episode"
    val label: String get() = title.name + if (title.type == "tv") " · S$season E$episode" else ""
    val resumable: Boolean get() = ProgressPolicy.resumable(seconds, duration)
}
object ProgressPolicy {
    fun valid(seconds: Double, duration: Double) = seconds.isFinite() && duration.isFinite() && seconds >= 0 && duration > 0 && seconds <= duration + 2
    fun resumable(seconds: Double, duration: Double) = valid(seconds, duration) && seconds >= 5 && seconds / duration < 0.95
}
object Framex {
    // framextv.xyz began redirecting embeds to framextv.cc in October 2026.
    // Keep the documented aliases for redirect/error/origin validation, but start
    // new sessions on the current host so legacy WebViews do not need a redirect.
    const val ORIGIN = "https://framextv.cc"
    val allowedHosts = setOf("framextv.cc", "www.framextv.cc", "framextv.xyz", "www.framextv.xyz")
    val providers = listOf("auto", "barbarian", "archer", "goblin")
    fun embed(playback: Playback, provider: String = "auto", autoplay: Boolean = true): String {
        require(provider in providers)
        val suffix = if (playback.title.type == "tv") "/${playback.season}/${playback.episode}" else ""
        val start = if (playback.resumable) playback.seconds.toLong() else 0
        return "$ORIGIN/embed/${playback.title.id}$suffix?autoplay=${if(autoplay) 1 else 0}&muted=0&controls=1&start=$start" + if (provider == "auto") "" else "&provider=$provider"
    }
}
