package jp.co.geniee.samples.nativead

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
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
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import jp.co.geniee.samples.R
import jp.co.geniee.samples.SharedPreferenceManager
import jp.co.geniee.samples.common.MemCache
import jp.co.geniee.gnadsdk.common.GNAdLogger
import jp.co.geniee.gnadsdk.common.GNSException
import jp.co.geniee.sdk.ads.nativead.GNNativeAd
import jp.co.geniee.sdk.ads.nativead.GNNativeAdRequest
import jp.co.geniee.sdk.ads.nativead.GNNativeAdRequestListener
import jp.co.geniee.sdk.ads.nativead.GNSNativeVideoPlayerListener
import jp.co.geniee.sdk.ads.nativead.GNSNativeVideoPlayerView

class NativeSimpleVideoDemoActivity : AppCompatActivity() {
    private val TAG = "[GNS]NativeDemoActivity"
    private var nativeAdRequest: GNNativeAdRequest? = null
    private var timeStart: Long = 0
    private var timeEnd: Long = 0
    private var log: GNAdLogger? = null
    private val videoViews = ArrayList<GNSNativeVideoPlayerView>()
    private val mLogArrayList = ArrayList<String>()
    private var mLogAdapter: ArrayAdapter<String>? = null
    private var edtZoneId: EditText? = null
    private var btLoadAd: Button? = null

    private var loopCount = 0
    private val previousHeights = ArrayList<Int>()
    private val handler = Handler()
    private val runnable = object : Runnable {
        @Synchronized
        override fun run() {
            for ((i, videoView) in videoViews.withIndex()) {
                val aspect = videoView.mediaFileAspect
                val height = (videoView.width / aspect).toInt()
                if (previousHeights.size <= i) {
                    previousHeights.add(0)
                }
                if (height != previousHeights[i]) {
                    if (aspect > 1) {
                        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
                        videoView.layoutParams = params
                        previousHeights[i] = height
                    } else {
                        val params = LinearLayout.LayoutParams(videoView.width, height)
                        videoView.layoutParams = params
                        previousHeights[i] = height
                    }
                }
            }
            if (loopCount <= 8) {
                loopCount += 1
                handler.postDelayed(this, 250)
            }
        }
    }

    private val nativeListener = object : GNNativeAdRequestListener() {
        override fun onNativeAdsLoaded(nativeAds: Array<GNNativeAd>) {
            timeEnd = System.currentTimeMillis()
            Log.i(TAG, "NativeAds loaded in seconds:" + (timeEnd - timeStart) / 1000.0)
            mLogArrayList.add(statusMessage("Number of zoneid : " + nativeAds.size))
            mLogAdapter!!.notifyDataSetChanged()

            val inflater = LayoutInflater.from(this@NativeSimpleVideoDemoActivity)
            for (ad in nativeAds) {
                val itemLayout = inflater.inflate(R.layout.simple_video_item, null) as LinearLayout
                val simpleVideoBody = findViewById<LinearLayout>(R.id.simpleVideoBody)
                simpleVideoBody.addView(itemLayout)
                val videoPlayerLayout = itemLayout.findViewById<LinearLayout>(R.id.videoPlayerLayout)
                if (ad.hasVideoContent()) {
                    val videoView = GNSNativeVideoPlayerView(applicationContext)
                    videoView.setActivity(this@NativeSimpleVideoDemoActivity)
                    videoView.listener = object : GNSNativeVideoPlayerListener {
                        // Sent when an video ad request succeeded.
                        override fun onVideoReceiveSetting(videoView: GNSNativeVideoPlayerView) {
                            // Decide the video size to display based on the aspect ratio
                            val videoViewMaxWidth = videoPlayerLayout.width
                            val videoViewMaxHeight = videoPlayerLayout.height
                            Log.i(TAG, "Aspect ratio =" + videoView.mediaFileAspect)
                            when {
                                videoView.mediaFileAspect > 1 -> {
                                    // 16:9
                                    Log.i(TAG, "videoView width =$videoViewMaxWidth")
                                    Log.i(TAG, "videoView height =" + (videoViewMaxWidth / videoView.mediaFileAspect).toInt())
                                    videoView.layoutParams = LinearLayout.LayoutParams(videoViewMaxWidth, (videoViewMaxWidth / videoView.mediaFileAspect).toInt())
                                }
                                videoView.mediaFileAspect < 1 -> {
                                    // 9:16
                                    Log.i(TAG, "videoView width =" + (videoViewMaxHeight * videoView.mediaFileAspect).toInt())
                                    Log.i(TAG, "videoView height =$videoViewMaxHeight")
                                    videoView.layoutParams = LinearLayout.LayoutParams((videoViewMaxHeight * videoView.mediaFileAspect).toInt(), videoViewMaxHeight)
                                }
                                else -> {
                                    // 1:1
                                    Log.i(TAG, "videoView width =$videoViewMaxHeight")
                                    Log.i(TAG, "videoView height =$videoViewMaxHeight")
                                    videoView.layoutParams = LinearLayout.LayoutParams((videoViewMaxHeight * 0.8).toInt(), (videoViewMaxHeight * 0.8).toInt())
                                }
                            }
                            Log.i(TAG, "onVideoReceiveSetting videoView.isReady()=" + videoView.isReady)
                            if (videoView.isReady) {
                                videoView.show()
                                Log.i(TAG, "onVideoReceiveSetting videoView.show")
                            }
                        }

                        // Sent when an video ad request failed.
                        // (Network Connection Unavailable, Frequency capping, Out of ad stock)
                        override fun onVideoFailWithError(videoView: GNSNativeVideoPlayerView, e: GNSException) {
                            Log.e(TAG, "Err:" + e.code + " " + e.message)
                            mLogArrayList.add(statusMessage("Err:" + e.code + " " + e.message))
                            mLogAdapter!!.notifyDataSetChanged()
                        }

                        // When playback of video ad is started
                        override fun onVideoStartPlaying(videoView: GNSNativeVideoPlayerView) {
                            Log.i(TAG, "Ad started playing.")
                        }

                        // When playback of video ad is completed
                        override fun onVideoPlayComplete(videoView: GNSNativeVideoPlayerView) {
                            Log.i(TAG, "Ad finished playing.")
                        }
                    }
                    videoPlayerLayout.addView(videoView, RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

                    videoView.load(ad)
                    videoViews.add(videoView)
                    val seekRunnable = object : Runnable {
                        var previousCurrentPosition = 0f
                        @Synchronized
                        override fun run() {
                            if (previousCurrentPosition != videoView.currentPosition) {
                                mLogArrayList.add(statusMessage("zoneID=" + ad.zoneID + " position=" + videoView.currentPosition
                                        + "/" + videoView.duration))
                                mLogAdapter!!.notifyDataSetChanged()
                                previousCurrentPosition = videoView.currentPosition
                            }
                            if (videoView.currentPosition < videoView.duration) {
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
                val titleText = itemLayout.findViewById<TextView>(R.id.titleText)
                titleText.text = ad.title
                val descriptionText = itemLayout.findViewById<TextView>(R.id.descriptionText)
                descriptionText.text = ad.description
                val advertiserText = itemLayout.findViewById<TextView>(R.id.advertiserText)
                advertiserText.text = ad.advertiser
                val videoPlayerClickButton = itemLayout.findViewById<Button>(R.id.videoPlayerClickButton)
                videoPlayerClickButton.setOnClickListener { ad.onTrackingClick() }
                val videoPlayerOutputButton = itemLayout.findViewById<Button>(R.id.videoPlayerOutputButton)
                if (ad.optout_text !== "") {
                    videoPlayerOutputButton.text = ad.optout_text
                } else {
                    videoPlayerOutputButton.text = "広告提供元"
                }
                videoPlayerOutputButton.setOnClickListener {
                    if (ad.optout_url.isNotEmpty()) {
                        val uri = Uri.parse(ad.optout_url)
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@NativeSimpleVideoDemoActivity, "ad.optout_url is empty", Toast.LENGTH_LONG).show()
                    }
                }
                mLogArrayList.add(statusMessage("nativeAd.zoneID=" + ad.zoneID))
                mLogArrayList.add(statusMessage("nativeAd.advertiser=" + ad.advertiser))
                mLogArrayList.add(statusMessage("nativeAd.title=" + ad.title))
                mLogArrayList.add(statusMessage("nativeAd.description=" + ad.description))
                mLogArrayList.add(statusMessage("nativeAd.cta=" + ad.cta))
                mLogArrayList.add(statusMessage("nativeAd.icon_aspectRatio=" + ad.icon_aspectRatio))
                mLogArrayList.add(statusMessage("nativeAd.icon_url=" + ad.icon_url))
                mLogArrayList.add(statusMessage("nativeAd.icon_height=" + ad.icon_height))
                mLogArrayList.add(statusMessage("nativeAd.icon_width=" + ad.icon_width))
                mLogArrayList.add(statusMessage("nativeAd.screenshots_aspectRatio=" + ad.screenshots_aspectRatio))
                mLogArrayList.add(statusMessage("nativeAd.screenshots_url=" + ad.screenshots_url))
                mLogArrayList.add(statusMessage("nativeAd.screenshots_height=" + ad.screenshots_height))
                mLogArrayList.add(statusMessage("nativeAd.screenshots_width=" + ad.screenshots_width))
                mLogArrayList.add(statusMessage("nativeAd.app_appName=" + ad.app_appName))
                mLogArrayList.add(statusMessage("nativeAd.app_appid=" + ad.app_appid))
                mLogArrayList.add(statusMessage("nativeAd.app_rating=" + ad.app_rating))
                mLogArrayList.add(statusMessage("nativeAd.app_storeURL=" + ad.app_storeURL))
                mLogArrayList.add(statusMessage("nativeAd.app_targetAge=" + ad.app_targetAge))
                mLogArrayList.add(statusMessage("nativeAd.optout_text=" + ad.optout_text))
                mLogArrayList.add(statusMessage("nativeAd.optout_image_url=" + ad.optout_image_url))
                mLogArrayList.add(statusMessage("nativeAd.optout_url=" + ad.optout_url))
                mLogArrayList.add(statusMessage("nativeAd.hasVideoContent()=" + ad.hasVideoContent()))
                // Report SDK impression
                ad.onTrackingImpression()
                mLogArrayList.add(statusMessage("ZoneId=" + ad.zoneID + " Issued TrackingImpression."))
                mLogAdapter!!.notifyDataSetChanged()
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

    fun statusMessage(message: String): String {
        Log.d(TAG, message)
        return String.format("%s %s", SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().time), message)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_simple_video_demo)
        log = GNAdLogger.getInstance()
        log!!.priority = GNAdLogger.DEBUG
        edtZoneId = findViewById(R.id.edtZoneId)
        edtZoneId!!.setText(SharedPreferenceManager.getInstance(this).getString(SharedPreferenceManager.NATIVE_AD_ZONE_ID))
        btLoadAd = findViewById(R.id.btLoadNativeAd)
        btLoadAd!!.setOnClickListener {
            try {
                val zoneId = edtZoneId!!.text.toString()
                SharedPreferenceManager.getInstance(this@NativeSimpleVideoDemoActivity).putString(SharedPreferenceManager.NATIVE_AD_ZONE_ID, zoneId)
                // Initialize SDK GNNativeAdRequest
                nativeAdRequest = GNNativeAdRequest(applicationContext, zoneId)
                nativeAdRequest!!.adListener = nativeListener
                // nativeAdRequest.setGeoLocationEnable(true);
                timeStart = System.currentTimeMillis()
                if (zoneId.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size > 1) {
                    nativeAdRequest!!.multiLoadAds(this@NativeSimpleVideoDemoActivity)
                } else {
                    nativeAdRequest!!.loadAds(this@NativeSimpleVideoDemoActivity)
                }
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Err " + e.message, Toast.LENGTH_SHORT).show()
            }
        }
        if (mLogAdapter == null) {
            mLogAdapter = object : ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, mLogArrayList) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent)
                    val tv = view.findViewById<View>(android.R.id.text1) as TextView
                    tv.setTextColor(Color.BLACK)
                    tv.setSingleLine(false)
                    return view
                }
            }
        }
        (findViewById<ListView>(R.id.gnsSampleListView)).adapter = mLogAdapter
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        loopCount = 0
        handler.post(runnable)
    }

    override fun onResume() {
        super.onResume()
        for (videoView in videoViews) {
            videoView.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        for (videoView in videoViews) {
            videoView.pause()
        }
    }

    override fun onDestroy() {
        for (videoView in videoViews) {
            videoView.remove()
        }
        super.onDestroy()
    }

    internal inner class ImageGetTask(protected var imageView: ImageView, protected var url: String) : AsyncTask<Void, Void, Bitmap>() {
        private val tag: String = imageView.tag.toString()

        public override fun onPreExecute() {
            val bitmap = MemCache.getImage(url)
            if (bitmap != null) {
                if (tag == imageView.tag) {
                    imageView.setImageBitmap(bitmap)
                }
                cancel(true)
                return
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
                MemCache.setImage(url, bitmap)
                bitmap
            } catch (e: MalformedURLException) {
                null
            } catch (e: IOException) {
                null
            }
        }

        override fun onPostExecute(bitmap: Bitmap) {
            if (tag == imageView.tag) {
                imageView.setImageBitmap(bitmap)
            }
        }
    }

}
