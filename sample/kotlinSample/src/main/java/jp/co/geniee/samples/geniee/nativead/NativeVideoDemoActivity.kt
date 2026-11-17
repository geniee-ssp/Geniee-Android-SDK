package jp.co.geniee.samples.geniee.nativead

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import jp.co.geniee.gnadsdk.common.GNSException
import jp.co.geniee.samples.R
import jp.co.geniee.samples.common.SharedPreferenceManager
import jp.co.geniee.samples.geniee.common.CellData
import jp.co.geniee.samples.geniee.common.GNQueue
import jp.co.geniee.samples.geniee.common.MemCache
import jp.co.geniee.sdk.ads.nativead.GNNativeAd
import jp.co.geniee.sdk.ads.nativead.GNNativeAdRequest
import jp.co.geniee.sdk.ads.nativead.GNNativeAdRequestListener
import jp.co.geniee.sdk.ads.nativead.GNSNativeVideoPlayerListener
import jp.co.geniee.sdk.ads.nativead.GNSNativeVideoPlayerView
import java.io.IOException
import java.net.URL

class NativeVideoDemoActivity : AppCompatActivity() {
    private var nativeAdRequest: GNNativeAdRequest? = null
    private val loading = false
    private val queueAds = GNQueue(100)
    private lateinit var edtZoneId: EditText
    private lateinit var btLoadAd: Button
    private val cellDataList = ArrayList<Any>()
    private var timeStart = 0L
    private var timeEnd = 0L
    private val adapter: ListViewAdapter by lazy { ListViewAdapter(this, cellDataList) }
    private val videoViews = ArrayList<GNSNativeVideoPlayerView>()
    private var zoneId: String = ""
    private var scale = 0f
    private var videoViewMaxWidth = 0
    private var videoViewMaxHeight = 0

    private val nativeListener = object : GNNativeAdRequestListener {
        override fun onNativeAdsLoaded(nativeAds: Array<GNNativeAd?>) {
            timeEnd = System.currentTimeMillis()
            Log.i(TAG, "NativeAds loaded in seconds:" + (timeEnd - timeStart) / 1000.0)
            Log.i(TAG, "nativeAds.length=" + nativeAds.size)
            for (ad in nativeAds) {
                if (ad != null) {
                    Log.i(TAG, "zoneID=" + ad.zoneID)
                    Log.i(TAG, "advertiser=" + ad.advertiser)
                    Log.i(TAG, "title=" + ad.title)
                    Log.i(TAG, "description=" + ad.description)
                    Log.i(TAG, "cta=" + ad.cta)
                    Log.i(TAG, "icon_aspectRatio=" + ad.icon_aspectRatio)
                    Log.i(TAG, "icon_url=" + ad.icon_url)
                    Log.i(TAG, "icon_height=" + ad.icon_height)
                    Log.i(TAG, "icon_width=" + ad.icon_width)
                    Log.i(TAG, "screenshots_aspectRatio=" + ad.screenshots_aspectRatio)
                    Log.i(TAG, "screenshots_url=" + ad.screenshots_url)
                    Log.i(TAG, "screenshots_height=" + ad.screenshots_height)
                    Log.i(TAG, "screenshots_width=" + ad.screenshots_width)
                    Log.i(TAG, "app_appName=" + ad.app_appName)
                    Log.i(TAG, "app_appid=" + ad.app_appid)
                    Log.i(TAG, "app_rating=" + ad.app_rating)
                    Log.i(TAG, "app_storeURL=" + ad.app_storeURL)
                    Log.i(TAG, "app_targetAge=" + ad.app_targetAge)
                    Log.i(TAG, "optout_text=" + ad.optout_text)
                    Log.i(TAG, "optout_image_url=" + ad.optout_image_url)
                    Log.i(TAG, "optout_url=" + ad.optout_url)
                    Log.i(TAG, "vast_xml=" + ad.vast_xml)

                    queueAds.enqueue(ad)
                }
            }
            if (queueAds.size() == 0) {
                Toast.makeText(this@NativeVideoDemoActivity, "There is no zone.", Toast.LENGTH_SHORT).show()
            }
            createCellDataList()
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
        setContentView(R.layout.activity_gnad_native_video)
        val listView = findViewById<ListView>(android.R.id.list)
        listView.addFooterView(layoutInflater.inflate(R.layout.listview_footer, null))
        listView.adapter = adapter
        listView.setOnScrollListener(scrollListener)
        listView.setOnItemClickListener { parent, _, position, _ ->
            val cell = (parent as ListView).getItemAtPosition(position)
            if (cell is GNNativeAd) {
                // Report SDK click
                cell.onTrackingClick()
            }
        }
        timeStart = System.currentTimeMillis()
        scale = resources.displayMetrics.density
        edtZoneId = findViewById(R.id.edtZoneId)
        edtZoneId.setText(SharedPreferenceManager.getInstance(this).getString(SharedPreferenceManager.NATIVE_AD_ZONE_ID))
        btLoadAd = findViewById(R.id.btLoadNativeAd)
        btLoadAd.setOnClickListener {
            try {
                zoneId = edtZoneId.text.toString()
                SharedPreferenceManager.getInstance(this).putString(SharedPreferenceManager.NATIVE_AD_ZONE_ID, zoneId)
                Log.i(TAG, "zoneId=$zoneId")
                // Initialize SDK GNNativeAdRequest
                val request = GNNativeAdRequest(this, zoneId)
                nativeAdRequest = request
                request.setAdListener(nativeListener)
                //request.setGeoLocationEnable(true);
                loadAds(request)
            } catch (e: Exception) {
                Toast.makeText(this, "Err " + e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadAds(request: GNNativeAdRequest) {
        if (zoneId.split(",").size > 1) {
            request.multiLoadAds(this)
        } else {
            request.loadAds(this)
        }
    }

    private fun createCellDataList() {
        val totalCnt = 20
        val adCnt = queueAds.size()
        for (i in 0 until totalCnt) {
            if (queueAds.size() > 0 && adCnt > 0 && totalCnt / adCnt > 0 && i % (totalCnt / adCnt) == 0) {
                cellDataList.add(queueAds.dequeue() ?: CellData())
            } else {
                cellDataList.add(CellData())
            }
        }
        adapter.notifyDataSetChanged()
    }

    private val scrollListener = object : AbsListView.OnScrollListener {
        override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
            if (totalItemCount - 1 > 0 && totalItemCount - 1 == cellDataList.size && !loading &&
                totalItemCount == firstVisibleItem + visibleItemCount
            ) {
                Log.d(TAG, "load additional list cells")
                val request = nativeAdRequest ?: return
                try {
                    loadAds(request)
                } catch (e: Exception) {
                    Toast.makeText(this@NativeVideoDemoActivity, "Err " + e.message, Toast.LENGTH_SHORT).show()
                }
                request.setAdListener(nativeListener)
            }
        }

        override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        videoViews.forEach { it.resume() }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
        videoViews.forEach { it.pause() }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        Log.d(TAG, "videoViews.size()=" + videoViews.size)
        videoViews.forEach { it.remove() }
        MemCache.clear()
        System.gc()
        super.onDestroy()
    }

    inner class ListViewAdapter(context: Context, list: List<Any>) : ArrayAdapter<Any>(context, 0, list) {
        private val inflater = LayoutInflater.from(context)

        init {
            Log.d(TAG, "ListViewAdapter")
        }

        inner class ViewHolder(view: View) {
            val viewGroup: LinearLayout = view.findViewById(R.id.viewGroup)
            val textView: TextView = view.findViewById(R.id.textView)
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView
                ?: inflater.inflate(R.layout.list_video_item, null).also { it.tag = ViewHolder(it) }
            val holder = view.tag as ViewHolder
            removeAllView(holder.viewGroup)
            when (val cell = getItem(position)) {
                // Rendering SDK NativeAd content
                is GNNativeAd -> {
                    if (cell.hasVideoContent()) {
                        Log.d(TAG, "getView: GNNativeAd(Video)$position")
                        val videoView = GNSNativeVideoPlayerView(applicationContext)
                        videoViews.add(videoView)
                        videoView.load(cell)
                        videoView.setActivity(this@NativeVideoDemoActivity)
                        videoView.setListener(object : GNSNativeVideoPlayerListener {
                            // Sent when an video ad request succeeded.
                            override fun onVideoReceiveSetting(videoView: GNSNativeVideoPlayerView) {
                                if (videoView.isReady()) {
                                    videoView.show()
                                }

                                // Decide the video size to display based on the aspect ratio
                                videoViewMaxWidth = holder.viewGroup.width
                                videoViewMaxHeight = holder.viewGroup.height
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
                                    videoView.layoutParams = LinearLayout.LayoutParams(videoViewMaxHeight, videoViewMaxHeight)
                                }
                            }

                            // Sent when an video ad request failed.
                            // (Network Connection Unavailable, Frequency capping, Out of ad stock)
                            override fun onVideoFailWithError(videoView: GNSNativeVideoPlayerView, e: GNSException) {
                                Log.e(TAG, "Err:" + e.code + " " + e.message)
                                removeAllView(holder.viewGroup)
                                createImageView(holder, cell)
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
                        holder.viewGroup.addView(videoView)
                    } else {
                        Log.d(TAG, "getView: GNNativeAd(Image)$position")
                        createImageView(holder, cell)
                    }

                    // Report SDK impression
                    cell.onTrackingImpression()

                    holder.textView.text = cell.zoneID + cell.title + cell.description + " No." + position
                    holder.textView.isClickable = true
                    holder.textView.setOnClickListener { cell.onTrackingClick() }
                }
                is CellData -> {
                    Log.d(TAG, "getView: MyData$position")
                    val imageView = ImageView(applicationContext)
                    imageView.layoutParams = LinearLayout.LayoutParams((135 * scale + 0.5f).toInt(), (100 * scale + 0.5f).toInt())
                    imageView.tag = cell.imgURL
                    ImageGetTask(imageView, cell.imgURL).execute()
                    holder.viewGroup.addView(imageView)
                    holder.textView.text = cell.title + " No." + position
                }
            }
            return view
        }

        private fun createImageView(holder: ViewHolder, cell: GNNativeAd) {
            val imageView = ImageView(applicationContext)
            imageView.layoutParams = LinearLayout.LayoutParams((135 * scale + 0.5f).toInt(), (100 * scale + 0.5f).toInt())
            imageView.tag = cell.icon_url
            ImageGetTask(imageView, cell.icon_url).execute()
            holder.viewGroup.addView(imageView)
        }

        private fun removeAllView(viewGroup: ViewGroup?) {
            if (viewGroup == null) {
                return
            }
            for (i in 0 until viewGroup.childCount) {
                val view = viewGroup.getChildAt(i)
                when (view) {
                    is ImageView -> view.setImageDrawable(null)
                    is GNSNativeVideoPlayerView -> {
                        view.remove()
                        removeVideoView(view)
                    }
                    is ViewGroup -> removeAllView(view)
                }
                view.background = null
            }
            viewGroup.removeAllViews()
        }

        private fun removeVideoView(view: GNSNativeVideoPlayerView) {
            val videoView = videoViews.firstOrNull { it === view } ?: return
            videoView.remove()
            videoViews.remove(videoView)
        }
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
        private const val TAG = "[GNS]NativeVideoDemoActivity"
    }
}
