package jp.co.geniee.samples.nativead

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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList

import jp.co.geniee.samples.R
import jp.co.geniee.samples.SharedPreferenceManager
import jp.co.geniee.samples.common.CellData
import jp.co.geniee.samples.common.GNQueue
import jp.co.geniee.samples.common.MemCache
import jp.co.geniee.gnadsdk.common.GNSException
import jp.co.geniee.sdk.ads.nativead.GNNativeAd
import jp.co.geniee.sdk.ads.nativead.GNNativeAdRequest
import jp.co.geniee.sdk.ads.nativead.GNNativeAdRequestListener
import jp.co.geniee.sdk.ads.nativead.GNSNativeVideoPlayerListener
import jp.co.geniee.sdk.ads.nativead.GNSNativeVideoPlayerView

class NativeVideoDemoActivity : AppCompatActivity() {
    private val TAG = "[GNS]NativeDemoActivity"
    private var nativeAdRequest: GNNativeAdRequest? = null
    private val loading = false
    private val queueAds = GNQueue(100)
    private var edtZoneId: EditText? = null
    private var btLoadAd: Button? = null
    private val cellDataList = ArrayList<Any>()
    private var timeStart: Long = 0
    private var timeEnd: Long = 0
    private var mAdapter: ListViewAdapter? = null
    private var mScrollListener: AbsListView.OnScrollListener? = null
    private var mClickListener: AdapterView.OnItemClickListener? = null
    private var mFooter: View? = null
    private var videoViews: MutableList<GNSNativeVideoPlayerView>? = null
    private var zoneId: String? = null
    private var scale: Float = 0.toFloat()
    private var videoViewMaxWidth: Int = 0
    private var videoViewMaxHeight: Int = 0

    private val nativeListener = object : GNNativeAdRequestListener() {
        override fun onNativeAdsLoaded(nativeAds: Array<GNNativeAd>) {
            timeEnd = System.currentTimeMillis()
            Log.i(TAG, "NativeAds loaded in seconds:" + (timeEnd - timeStart) / 1000.0)
            Log.i(TAG, "nativeAds.length=" + nativeAds.size)
            for (ad in nativeAds) {
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

    private val scrollListener: AbsListView.OnScrollListener
        get() {
            if (mScrollListener == null) {
                mScrollListener = object : AbsListView.OnScrollListener {
                    override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                        if (totalItemCount - 1 > 0 && totalItemCount - 1 == cellDataList.size && !loading &&
                                totalItemCount == firstVisibleItem + visibleItemCount) {
                            Log.d(TAG, "load additional list cells")
                            try {
                                if (zoneId!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size > 1) {
                                    nativeAdRequest!!.multiLoadAds(this@NativeVideoDemoActivity)
                                } else {
                                    nativeAdRequest!!.loadAds(this@NativeVideoDemoActivity)
                                }
                            } catch (e: Exception) {
                                Toast.makeText(this@NativeVideoDemoActivity, "Err " + e.message, Toast.LENGTH_SHORT).show()
                            }

                            nativeAdRequest!!.adListener = nativeListener
                        }
                    }

                    override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
                }
            }
            return mScrollListener!!
        }

    private val clickListener: AdapterView.OnItemClickListener
        get() {
            if (mClickListener == null) {
                mClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                    val listView = parent as ListView
                    val cell = listView.getItemAtPosition(position)
                    if (cell is GNNativeAd) {
                        // Report SDK click
                        cell.onTrackingClick()
                    }
                }
            }
            return mClickListener!!
        }

    private val adapter: ListAdapter
        get() {
            if (mAdapter == null) {
                mAdapter = ListViewAdapter(this, cellDataList)
            }
            return mAdapter!!
        }

    private val footer: View
        get() {
            if (mFooter == null) {
                mFooter = layoutInflater.inflate(R.layout.listview_footer, null)
            }
            return mFooter!!
        }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_video_demo)
        videoViews = ArrayList()
        val listView = findViewById<ListView>(android.R.id.list)
        listView.addFooterView(footer)
        listView.adapter = adapter
        listView.setOnScrollListener(scrollListener)
        listView.onItemClickListener = clickListener
        timeStart = System.currentTimeMillis()
        scale = this.resources.displayMetrics.density
        edtZoneId = findViewById(R.id.edtZoneId)
        edtZoneId!!.setText(SharedPreferenceManager.getInstance(this).getString(SharedPreferenceManager.NATIVE_AD_ZONE_ID))
        btLoadAd = findViewById(R.id.btLoadNativeAd)
        btLoadAd!!.setOnClickListener {
            try {
                zoneId = edtZoneId!!.text.toString()
                SharedPreferenceManager.getInstance(this@NativeVideoDemoActivity).putString(SharedPreferenceManager.NATIVE_AD_ZONE_ID, zoneId!!)
                Log.i(TAG, "zoneId=" + zoneId!!)
                // Initialize SDK GNNativeAdRequest
                nativeAdRequest = GNNativeAdRequest(this@NativeVideoDemoActivity, zoneId!!)
                nativeAdRequest!!.adListener = nativeListener
                //nativeAdRequest.setGeoLocationEnable(true);
                if (zoneId!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size > 1) {
                    nativeAdRequest!!.multiLoadAds(this@NativeVideoDemoActivity)
                } else {
                    nativeAdRequest!!.loadAds(this@NativeVideoDemoActivity)
                }
            } catch (e: Exception) {
                Toast.makeText(this@NativeVideoDemoActivity, "Err " + e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createCellDataList() {
        val totalCnt = 20
        val adCnt = queueAds.size()
        for (i in 0 until totalCnt) {
            if (queueAds.size() > 0 && adCnt > 0 && totalCnt / adCnt > 0 && i % (totalCnt / adCnt) == 0) {
                val ad = queueAds.dequeue()
                if (ad != null) {
                    cellDataList.add(ad)
                }
            } else {
                cellDataList.add(CellData())
            }
        }
        if (mAdapter != null) {
            mAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")

        for (videoView in videoViews!!) {
            videoView.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
        for (videoView in videoViews!!) {
            videoView.pause()
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        Log.d(TAG, "videoViews.size()=" + videoViews!!.size)
        for (videoView in videoViews!!) {
            videoView.remove()
        }
        MemCache.clear()
        System.gc()
        super.onDestroy()
    }

    internal inner class ListViewAdapter(context: Context, list: List<Any>) : ArrayAdapter<Any>(context, 0, list) {
        private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        init {
            Log.d(TAG, "ListViewAdapter")
        }

        internal inner class ViewHolder {
            var viewGroup: LinearLayout? = null
            var textView: TextView? = null
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val holder: ViewHolder
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_video_item, null)
                holder = ViewHolder()
                holder.viewGroup = convertView!!.findViewById(R.id.viewGroup) as LinearLayout
                holder.textView = convertView.findViewById(R.id.textView)
                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }
            removeAllView(holder.viewGroup)
            val cell = getItem(position)
            // Rendering SDK NativeAd content
            if (cell is GNNativeAd) {
                if (cell.hasVideoContent()) {
                    Log.d(TAG, "getView: GNNativeAd(Video)$position")
                    val videoView = GNSNativeVideoPlayerView(applicationContext)
                    videoViews!!.add(videoView)
                    videoView.load(cell)
                    videoView.setActivity(this@NativeVideoDemoActivity)
                    videoView.listener = object : GNSNativeVideoPlayerListener {
                        // Sent when an video ad request succeeded.
                        override fun onVideoReceiveSetting(videoView: GNSNativeVideoPlayerView) {
                            if (videoView.isReady) {
                                videoView.show()
                            }

                            // Decide the video size to display based on the aspect ratio
                            videoViewMaxWidth = holder.viewGroup!!.width
                            videoViewMaxHeight = holder.viewGroup!!.height
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
                                    videoView.layoutParams = LinearLayout.LayoutParams(videoViewMaxHeight, videoViewMaxHeight)
                                }
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
                    }
                    holder.viewGroup!!.addView(videoView)
                } else {
                    Log.d(TAG, "getView: GNNativeAd(Image)$position")
                    createImageView(holder, cell)
                }

                // Report SDK impression
                cell.onTrackingImpression()

                holder.textView!!.text = cell.zoneID + cell.title + cell.description + " No." + position
                holder.textView!!.isClickable = true
                holder.textView!!.setOnClickListener { cell.onTrackingClick() }
            } else {
                Log.d(TAG, "getView: MyData$position")
                val imageView = ImageView(applicationContext)
                imageView.layoutParams = LinearLayout.LayoutParams((135 * scale + 0.5f).toInt(), (100 * scale + 0.5f).toInt())
                imageView.tag = (cell as CellData).imgURL
                ImageGetTask(imageView, cell.imgURL).execute()
                holder.viewGroup!!.addView(imageView)
                holder.textView!!.text = cell.title + " No." + position
            }
            return convertView
        }

        private fun createImageView(holder: ViewHolder, cell: GNNativeAd) {
            val imageView = ImageView(applicationContext)
            imageView.layoutParams = LinearLayout.LayoutParams((135 * scale + 0.5f).toInt(), (100 * scale + 0.5f).toInt())
            imageView.tag = cell.icon_url

            ImageGetTask(imageView, cell.icon_url).execute()
            holder.viewGroup!!.addView(imageView)
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
            for (videoView in videoViews!!) {
                if (videoView === view) {
                    videoView.remove()
                    videoViews!!.remove(videoView)
                    break
                }
            }
        }
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
