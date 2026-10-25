package dev.lumentv

import android.content.Context
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.io.File
import java.security.MessageDigest

class Tmdb(private val context: Context) {
    class ApiError(message: String) : Exception(message)
    fun request(path: String, query: String = ""): JSONObject {
        val token = Store(context).token
        if (token.isBlank()) throw ApiError("Add your free TMDB API read token or v3 key in Settings.")
        val keyParam = if (token.length == 32) "&api_key=${URLEncoder.encode(token, "UTF-8")}" else ""
        val url = "https://api.themoviedb.org/3/$path?language=en-US&include_adult=false$query$keyParam"
        val hash = MessageDigest.getInstance("SHA-256").digest((url + token).toByteArray()).joinToString("") { "%02x".format(it) }
        val dir = File(context.cacheDir, "tmdb").apply { mkdirs() }; val cache = File(dir, hash)
        if (cache.exists() && System.currentTimeMillis() - cache.lastModified() < 15 * 60 * 1000) return JSONObject(cache.readText())
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connectTimeout = 12000; connection.readTimeout = 12000
        if (keyParam.isEmpty()) connection.setRequestProperty("Authorization", "Bearer $token")
        connection.setRequestProperty("Accept", "application/json")
        try {
            val status = connection.responseCode
            if (status != 200) throw ApiError(when(status) { 401 -> "TMDB rejected the key. Check Settings."; 429 -> "TMDB rate limit. Please try again later."; else -> "TMDB request failed (HTTP $status)." })
            val text = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(text)
            cache.writeText(text)
            dir.listFiles()?.sortedByDescending { it.lastModified() }?.drop(40)?.forEach { it.delete() }
            return json
        } catch (e: ApiError) { throw e } catch (_: Exception) { throw ApiError("Cannot reach TMDB. Check the TV internet connection and date/time.") }
        finally { connection.disconnect() }
    }
    fun titles(path: String, query: String = "", fallback: String = "movie"): List<Title> {
        val array = request(path, query).optJSONArray("results") ?: return emptyList()
        return (0 until array.length()).mapNotNull { index ->
            val o = array.getJSONObject(index); val type = o.optString("media_type", fallback)
            if (type !in listOf("movie", "tv") || o.optBoolean("adult")) null else runCatching {
                Title(o.getInt("id"), type, o.optString("title", o.optString("name")), o.optString("overview"), o.optString("poster_path").takeUnless { it == "null" } ?: "", o.optString("release_date", o.optString("first_air_date")).take(4), o.optDouble("vote_average", 0.0))
            }.getOrNull()
        }
    }
    fun search(query: String, page: Int) = titles("search/multi", "&query=${URLEncoder.encode(query, "UTF-8")}&page=$page")
}
