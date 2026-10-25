package dev.lumentv

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import android.widget.ImageView
import java.net.URL
import java.net.HttpURLConnection

object Posters {
    private val cache = object : LruCache<String, Bitmap>(8 * 1024 * 1024) { override fun sizeOf(key: String, value: Bitmap) = value.byteCount }
    private val executor = java.util.concurrent.ThreadPoolExecutor(2,2,30,java.util.concurrent.TimeUnit.SECONDS,java.util.concurrent.ArrayBlockingQueue(60),java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy())
    fun load(view: ImageView, path: String) {
        view.tag = path; view.setImageDrawable(null)
        if (!path.startsWith("/")) return
        cache.get(path)?.let { view.setImageBitmap(it); return }
        executor.execute {
            val bitmap = runCatching {
                val c = URL("https://image.tmdb.org/t/p/w185$path").openConnection() as HttpURLConnection
                c.connectTimeout = 8000; c.readTimeout = 8000
                try { c.inputStream.use { BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.RGB_565 }) } } finally { c.disconnect() }
            }.getOrNull()
            if(bitmap != null) { cache.put(path, bitmap); Ui.main.post { if(view.tag == path) view.setImageBitmap(bitmap) } }
        }
    }
    fun trim() { cache.evictAll() }
}
