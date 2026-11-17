package jp.co.geniee.samples.geniee.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.URL

@Suppress("DEPRECATION")
class ImageGetTask(imageView: ImageView, private val url: String) : AsyncTask<Void, Void, Bitmap?>() {
    private val imageView = WeakReference(imageView)
    private val tag: String = imageView.tag?.toString() ?: ""

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: Void): Bitmap? = try {
        URL(url).openStream().use { BitmapFactory.decodeStream(it) }
    } catch (e: IOException) {
        null
    }

    @Deprecated("Deprecated in Java")
    override fun onPostExecute(bitmap: Bitmap?) {
        val view = imageView.get() ?: return
        if (tag == view.tag) {
            view.setImageBitmap(bitmap)
        }
    }
}
