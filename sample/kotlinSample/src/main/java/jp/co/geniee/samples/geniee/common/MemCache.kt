package jp.co.geniee.samples.geniee.common

import android.graphics.Bitmap
import androidx.collection.LruCache

object MemCache {
    private const val CACHE_SIZE = 6 * 1024

    private val memoryCache = object : LruCache<String, Bitmap>(CACHE_SIZE) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.rowBytes * value.height / 1024
    }

    @JvmStatic
    fun getImage(key: String): Bitmap? = memoryCache.get(key)?.takeUnless { it.isRecycled }

    @JvmStatic
    fun setImage(key: String, bitmap: Bitmap?) {
        if (!hasImage(key) && bitmap != null) {
            memoryCache.put(key, bitmap)
        }
    }

    @JvmStatic
    fun hasImage(key: String): Boolean = getImage(key) != null

    @JvmStatic
    fun clear() {
        memoryCache.evictAll()
    }
}
