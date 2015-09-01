package jp.co.geniee.samples.multiplebanner;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class MemCache {
	private static int cacheSize = 6 * 1024;

	private static LruCache<String, Bitmap> mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
		@Override
		protected int sizeOf(String key, Bitmap bitmap) {
			return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
		}
 
		protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
			if (oldValue != null && !oldValue.isRecycled()) {
			}
			oldValue = null;
		};
	};

	public static Bitmap getImage(String key) {
		Bitmap bitmap = mMemoryCache.get(key);
		if(bitmap != null && bitmap.isRecycled()){
			return null;
		}else {
			return bitmap;
		}
	}

	public static void setImage(String key, Bitmap bitmap) {
		if (!MemCache.hasImage(key) && bitmap != null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	public static boolean hasImage(String key) {
		if (getImage(key) == null) {
			return false;
		} else {
			return true;
		}
	}

	public static void clear() {
		mMemoryCache.evictAll();
	}
}
