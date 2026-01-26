package jp.co.geniee.samples.gnad.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import java.io.IOException
import java.io.InputStream
import java.net.URL

class ImageGetTask(imageView: ImageView, private val url: String) : AsyncTask<Void, Void, Bitmap>() {
    private val imageView = ThreadLocal<ImageView>()
    private var tag = ""

    init {
        this.imageView.set(imageView)
        if (imageView.tag != null) {
            tag = imageView.tag.toString()
        }
    }

    override fun doInBackground(vararg params: Void): Bitmap? {
        val bitmap: Bitmap
        return try {
            val imageUrl = URL(url)
            val input: InputStream
            input = imageUrl.openStream()
            bitmap = BitmapFactory.decodeStream(input)
            input.close()
            bitmap
        } catch (e: IOException) {
            null
        }

    }

    override fun onPostExecute(bitmap: Bitmap?) {
        if (tag == imageView.get()!!.tag) {
            imageView.get()!!.setImageBitmap(bitmap)
        }
    }
}
