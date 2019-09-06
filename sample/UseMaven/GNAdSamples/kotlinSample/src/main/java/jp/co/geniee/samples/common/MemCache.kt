package jp.co.geniee.samples.common

import android.graphics.Bitmap
import androidx.collection.LruCache

object MemCache {
    private const val cacheSize = 6 * 1024

    private val mMemoryCache = object : androidx.collection.LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.rowBytes * bitmap.height / 1024
        }

        override fun entryRemoved(evicted: Boolean, key: String, oldValue: Bitmap, newValue: Bitmap?) {
            var oldValue = oldValue
            if (!oldValue.isRecycled) {
            }
        }
    }

    fun getImage(key: String): Bitmap? {
        val bitmap = mMemoryCache.get(key)
        return if (bitmap != null && bitmap.isRecycled) {
            null
        } else {
            bitmap
        }
    }

    fun setImage(key: String, bitmap: Bitmap?) {
        if (!hasImage(key) && bitmap != null) {
            mMemoryCache.put(key, bitmap)
        }
    }

    fun hasImage(key: String): Boolean {
        return getImage(key) != null
    }

    fun clear() {
        mMemoryCache.evictAll()
    }
}
