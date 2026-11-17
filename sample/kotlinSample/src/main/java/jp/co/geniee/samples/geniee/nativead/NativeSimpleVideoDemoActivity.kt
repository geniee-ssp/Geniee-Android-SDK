package jp.co.geniee.samples.geniee.nativead

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import jp.co.geniee.gnadsdk.common.GNAdLogger
import jp.co.geniee.gnadsdk.common.GNSException
import jp.co.geniee.samples.R
import jp.co.geniee.samples.common.SharedPreferenceManager
import jp.co.geniee.samples.geniee.common.MemCache
import jp.co.geniee.sdk.ads.nativead.GNNativeAd
import jp.co.geniee.sdk.ads.nativead.GNNativeAdRequest
import jp.co.geniee.sdk.ads.nativead.GNNativeAdRequestListener
import jp.co.geniee.sdk.ads.nativead.GNSNativeVideoPlayerListener
import jp.co.geniee.sdk.ads.nativead.GNSNativeVideoPlayerView
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar

class NativeSimpleVideoDemoActivity : AppCompatActivity() {
    private var nativeAdRequest: GNNativeAdRequest? = null
    private var timeStart = 0L
    private var timeEnd = 0L
    private lateinit var log: GNAdLogger
    private val videoViews = ArrayList<GNSNativeVideoPlayerView>()
    private val logArrayList = ArrayList<String>()
    private var logAdapter: ArrayAdapter<String>? = null
    private lateinit var edtZoneId: EditText
    private lateinit var btLoadAd: Button

    private var loopCount = 0
    private val previousHeights = ArrayList<Int>()
    private val handler = Handler(Looper.getMainLooper())
    private val runnable: Runnable = object : Runnable {
        @Synchronized
        override fun run() {
            for ((i, videoView) in videoViews.withIndex()) {
                val aspect = videoView.getMediaFileAspect()
                val height = (videoView.width / aspect).toInt()
                if (previousHeights.size <= i) {
                    previousHeights.add(0)
                }
                if (height != previousHeights[i]) {
                    val params = if (aspect > 1) {
                        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
                    } else {
                        LinearLayout.LayoutParams(videoView.width, height)
                    }
                    videoView.layoutParams = params
                    previousHeights[i] = height
                }
            }
            if (loopCount <= 8) {
                loopCount += 1
                handler.postDelayed(this, 250)
            }
        }
    }

    fun statusMessage(message: String): String {
        Log.d(TAG, message)
        return String.format("%s %s", SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().time), message)
    }

    private fun addLog(message: String) {
        logArrayList.add(statusMessage(message))
    }

    private val nativeListener = object : GNNativeAdRequestListener {
        override fun onNativeAdsLoaded(nativeAds: Array<GNNativeAd>) {
            timeEnd = System.currentTimeMillis()
            Log.i(TAG, "NativeAds loaded in seconds:" + (timeEnd - timeStart) / 1000.0)
            addLog("Number of zoneid : " + nativeAds.size)
            logAdapter?.notifyDataSetChanged()

            val inflater = LayoutInflater.from(this@NativeSimpleVideoDemoActivity)
            for (ad in nativeAds) {
                val simpleVideoBody = findViewById<LinearLayout>(R.id.simple_video_body)
                val itemLayout = inflater.inflate(R.layout.simple_video_item, null) as LinearLayout
                simpleVideoBody.addView(itemLayout)
                val videoPlayerLayout = itemLayout.findViewById<LinearLayout>(R.id.videoPlayerLayout)
                if (ad.hasVideoContent()) {
                    val videoView = GNSNativeVideoPlayerView(applicationContext)
                    videoView.setActivity(this@NativeSimpleVideoDemoActivity)

                    videoView.setListener(object : GNSNativeVideoPlayerListener {
                        // Sent when an video ad request succeeded.
                        override fun onVideoReceiveSetting(videoView: GNSNativeVideoPlayerView) {
                            // Decide the video size to display based on the aspect ratio
                            val videoViewMaxWidth = videoPlayerLayout.width
                            val videoViewMaxHeight = videoPlayerLayout.height
                            val aspect = videoView.getMediaFileAspect()
                            Log.i(TAG, "Aspect ratio =$aspect")
                            if (aspect > 1) {
                                // 16:9
                                Log.i(TAG, "videoView width =$videoViewMaxWidth")
                                Log.i(TAG, "videoView height =" + (videoViewMaxWidth / aspect).toInt())
                                videoView.layoutParams = LinearLayout.LayoutParams(videoViewMaxWidth, (videoViewMaxWidth / aspect).toInt())
                            } else if (aspect < 1) {
                                // 9:16
                                Log.i(TAG, "videoView width =" + (videoViewMaxHeight * aspect).toInt())
                                Log.i(TAG, "videoView height =$videoViewMaxHeight")
                                videoView.layoutParams = LinearLayout.LayoutParams((videoViewMaxHeight * aspect).toInt(), videoViewMaxHeight)
                            } else {
                                // 1:1
                                Log.i(TAG, "videoView width =$videoViewMaxHeight")
                                Log.i(TAG, "videoView height =$videoViewMaxHeight")
                                videoView.layoutParams = LinearLayout.LayoutParams((videoViewMaxHeight * 0.8).toInt(), (videoViewMaxHeight * 0.8).toInt())
                            }
                            Log.i(TAG, "onVideoReceiveSetting videoView.isReady()=" + videoView.isReady())
                            if (videoView.isReady()) {
                                videoView.show()
                                Log.i(TAG, "onVideoReceiveSetting videoView.show")
                            }
                        }

                        // Sent when an video ad request failed.
                        // (Network Connection Unavailable, Frequency capping, Out of ad stock)
                        override fun onVideoFailWithError(videoView: GNSNativeVideoPlayerView, e: GNSException) {
                            Log.e(TAG, "Err:" + e.code + " " + e.message)
                            addLog("Err:" + e.code + " " + e.message)
                            logAdapter?.notifyDataSetChanged()
                        }

                        // When playback of video ad is started
                        override fun onVideoStartPlaying(videoView: GNSNativeVideoPlayerView) {
                            Log.i(TAG, "Ad started playing.")
                        }

                        // When playback of video ad is completed
                        override fun onVideoPlayComplete(videoView: GNSNativeVideoPlayerView) {
                            Log.i(TAG, "Ad finished playing.")
                        }

                        // When playback of video ad is closed
                        override fun onVideoClose(videoView: GNSNativeVideoPlayerView) {
                            Log.i(TAG, "Ad closed.")
                        }
                    })
                    videoPlayerLayout.addView(videoView, RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

                    videoView.load(ad)
                    videoViews.add(videoView)
                    val seekRunnable = object : Runnable {
                        var previousCurrentPosition = 0f

                        @Synchronized
                        override fun run() {
                            if (previousCurrentPosition != videoView.getCurrentPosition()) {
                                addLog("zoneID=" + ad.zoneID + " position=" + videoView.getCurrentPosition() +
                                        "/" + videoView.getDuration())
                                logAdapter?.notifyDataSetChanged()
                                previousCurrentPosition = videoView.getCurrentPosition()
                            }
                            if (videoView.getCurrentPosition() < videoView.getDuration()) {
                                handler.postDelayed(this, 1000)
                            }
                        }
                    }
                    handler.postDelayed(seekRunnable, 1000)
                } else {
                    val imageView = ImageView(applicationContext)
                    imageView.tag = ad.screenshots_url
                    imageView.scaleType = ImageView.ScaleType.FIT_START
                    imageView.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    ImageGetTask(imageView, ad.screenshots_url).execute()
                }
                val iconImage = itemLayout.findViewById<ImageView>(R.id.iconImage)
                iconImage.tag = ad.icon_url
                iconImage.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                ImageGetTask(iconImage, ad.icon_url).execute()
                itemLayout.findViewById<TextView>(R.id.titleText).text = ad.title
                itemLayout.findViewById<TextView>(R.id.descriptionText).text = ad.description
                itemLayout.findViewById<TextView>(R.id.advertiserText).text = ad.advertiser
                itemLayout.findViewById<Button>(R.id.videoPlayerClickButton).setOnClickListener {
                    ad.onTrackingClick()
                }
                val outputButton = itemLayout.findViewById<Button>(R.id.videoPlayerOutputButton)
                outputButton.text = if (ad.optout_text != "") ad.optout_text else "広告提供元"
                outputButton.setOnClickListener {
                    if (!ad.optout_url.isNullOrEmpty()) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(ad.optout_url)))
                    } else {
                        Toast.makeText(this@NativeSimpleVideoDemoActivity, "ad.optout_url is empty", Toast.LENGTH_LONG).show()
                    }
                }
                addLog("nativeAd.zoneID=" + ad.zoneID)
                addLog("nativeAd.advertiser=" + ad.advertiser)
                addLog("nativeAd.title=" + ad.title)
                addLog("nativeAd.description=" + ad.description)
                addLog("nativeAd.cta=" + ad.cta)
                addLog("nativeAd.icon_aspectRatio=" + ad.icon_aspectRatio)
                addLog("nativeAd.icon_url=" + ad.icon_url)
                addLog("nativeAd.icon_height=" + ad.icon_height)
                addLog("nativeAd.icon_width=" + ad.icon_width)
                addLog("nativeAd.screenshots_aspectRatio=" + ad.screenshots_aspectRatio)
                addLog("nativeAd.screenshots_url=" + ad.screenshots_url)
                addLog("nativeAd.screenshots_height=" + ad.screenshots_height)
                addLog("nativeAd.screenshots_width=" + ad.screenshots_width)
                addLog("nativeAd.app_appName=" + ad.app_appName)
                addLog("nativeAd.app_appid=" + ad.app_appid)
                addLog("nativeAd.app_rating=" + ad.app_rating)
                addLog("nativeAd.app_storeURL=" + ad.app_storeURL)
                addLog("nativeAd.app_targetAge=" + ad.app_targetAge)
                addLog("nativeAd.optout_text=" + ad.optout_text)
                addLog("nativeAd.optout_image_url=" + ad.optout_image_url)
                addLog("nativeAd.optout_url=" + ad.optout_url)
                addLog("nativeAd.hasVideoContent()=" + ad.hasVideoContent())
                // Report SDK impression
                ad.onTrackingImpression()
                addLog("ZoneId=" + ad.zoneID + " Issued TrackingImpression.")
                logAdapter?.notifyDataSetChanged()
            }
        }

        override fun onNativeAdsFailedToLoad(e: GNSException) {
            Log.w(TAG, "onNativeAdsFailedToLoad Err " + e.code + ":" + e.message)
            Toast.makeText(applicationContext, "Err " + e.code + ":" + e.message, Toast.LENGTH_SHORT).show()
        }

        override fun onShouldStartInternalBrowserWithClick(landingURL: String): Boolean {
            Log.i(TAG, "onShouldStartInternalBrowserWithClick : $landingURL")
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gnad_native_simple_video)
        log = GNAdLogger.getInstance()
        log.setPriority(GNAdLogger.DEBUG)
        timeStart = System.currentTimeMillis()
        edtZoneId = findViewById(R.id.edtZoneId)
        edtZoneId.setText(SharedPreferenceManager.getInstance(this).getString(SharedPreferenceManager.NATIVE_AD_ZONE_ID))
        btLoadAd = findViewById(R.id.btLoadNativeAd)
        btLoadAd.setOnClickListener {
            try {
                val zoneId = edtZoneId.text.toString()
                SharedPreferenceManager.getInstance(this).putString(SharedPreferenceManager.NATIVE_AD_ZONE_ID, zoneId)
                // Initialize SDK GNNativeAdRequest
                val request = GNNativeAdRequest(applicationContext, zoneId)
                nativeAdRequest = request
                request.setAdListener(nativeListener)
                // request.setGeoLocationEnable(true);
                if (zoneId.split(",").size > 1) {
                    request.multiLoadAds(this)
                } else {
                    request.loadAds(this)
                }
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Err " + e.message, Toast.LENGTH_SHORT).show()
            }
        }
        if (logAdapter == null) {
            logAdapter = object : ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, logArrayList) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent)
                    val tv = view.findViewById<TextView>(android.R.id.text1)
                    tv.setTextColor(Color.BLACK)
                    tv.isSingleLine = false
                    return view
                }
            }
        }
        findViewById<ListView>(R.id.gns_sample_list_view).adapter = logAdapter
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        loopCount = 0
        handler.post(runnable)
    }

    override fun onResume() {
        super.onResume()
        videoViews.forEach { it.resume() }
    }

    override fun onPause() {
        super.onPause()
        videoViews.forEach { it.pause() }
    }

    override fun onDestroy() {
        videoViews.forEach { it.remove() }
        super.onDestroy()
    }

    @Suppress("DEPRECATION")
    inner class ImageGetTask(private val imageView: ImageView, private val url: String) :
        AsyncTask<Void, Void, Bitmap?>() {
        private val tag: String = imageView.tag.toString()

        @Deprecated("Deprecated in Java")
        override fun onPreExecute() {
            val bitmap = MemCache.getImage(url)
            if (bitmap != null) {
                if (tag == imageView.tag) {
                    imageView.setImageBitmap(bitmap)
                }
                cancel(true)
            }
        }

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: Void): Bitmap? {
            return try {
                val bitmap = URL(url).openStream().use { BitmapFactory.decodeStream(it) }
                MemCache.setImage(url, bitmap)
                bitmap
            } catch (e: IOException) {
                null
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(bitmap: Bitmap?) {
            if (tag == imageView.tag) {
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    companion object {
        private const val TAG = "[GNS]NativeSimpleVideoDemoActivity"
    }
}
