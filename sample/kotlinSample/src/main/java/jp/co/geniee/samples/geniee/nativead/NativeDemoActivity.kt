package jp.co.geniee.samples.geniee.nativead

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.AbsListView
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import jp.co.geniee.gnadsdk.common.GNAdLogger
import jp.co.geniee.gnadsdk.common.GNSException
import jp.co.geniee.samples.R
import jp.co.geniee.samples.common.SharedPreferenceManager
import jp.co.geniee.samples.geniee.common.CellData
import jp.co.geniee.samples.geniee.common.GNQueue
import jp.co.geniee.samples.geniee.common.MemCache
import jp.co.geniee.sdk.ads.nativead.GNNativeAd
import jp.co.geniee.sdk.ads.nativead.GNNativeAdRequest
import jp.co.geniee.sdk.ads.nativead.GNNativeAdRequestListener

class NativeDemoActivity : AppCompatActivity() {

    private var loading = false
    private val queueAds = GNQueue(100)
    private lateinit var edtZoneId: EditText
    private lateinit var btLoadNativeAd: Button
    private lateinit var adapter: NativeAdListViewAdapter
    private val cellDataList = ArrayList<Any>()
    private var nativeAdRequest: GNNativeAdRequest? = null

    private val nativeListener = object : GNNativeAdRequestListener {
        override fun onNativeAdsLoaded(gnNativeAds: Array<GNNativeAd>) {
            for (ad in gnNativeAds) {
                queueAds.enqueue(ad)
            }
            Log.i(TAG, "Native ad has been received")
        }

        override fun onNativeAdsFailedToLoad(e: GNSException) {
            Log.w(TAG, "onNativeAdsFailedToLoad Err " + e.code + ":" + e.message)
        }

        override fun onShouldStartInternalBrowserWithClick(landingURL: String): Boolean {
            Log.i(TAG, "onShouldStartInternalBrowserWithClick : $landingURL")
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gnad_native)

        edtZoneId = findViewById(R.id.edtZoneId)
        edtZoneId.setText(SharedPreferenceManager.getInstance(this).getString(SharedPreferenceManager.NATIVE_AD_ZONE_ID))
        btLoadNativeAd = findViewById(R.id.btLoadNativeAd)

        adapter = NativeAdListViewAdapter(this, cellDataList)
        val listView = findViewById<ListView>(android.R.id.list)
        listView.adapter = adapter
        listView.addFooterView(layoutInflater.inflate(R.layout.listview_footer, null))
        listView.setOnItemClickListener { adapterView, _, position, _ ->
            val cell = (adapterView as ListView).getItemAtPosition(position)
            if (cell is GNNativeAd) {
                // Report SDK click
                cell.onTrackingClick()
            }
        }

        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScroll(absListView: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (totalItemCount - 1 > 0 && totalItemCount - 1 == cellDataList.size && !loading &&
                    totalItemCount == firstVisibleItem + visibleItemCount
                ) {
                    nativeAdRequest?.loadAds(this@NativeDemoActivity)
                    requestCellDataListAsync()
                }
            }

            override fun onScrollStateChanged(absListView: AbsListView, scrollState: Int) {}
        })

        btLoadNativeAd.setOnClickListener {
            loadNativeAd()
            requestCellDataListAsync()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MemCache.clear()
        System.gc()
    }

    private fun loadNativeAd() {
        try {
            val zoneId = edtZoneId.text.toString()
            // Initialize SDK GNNativeAdRequest
            val request = GNNativeAdRequest(this, zoneId)
            nativeAdRequest = request
            request.setAdListener(nativeListener)
            //request.setGeoLocationEnable(true);
            request.setLogPriority(GNAdLogger.INFO)
            request.loadAds(this)
            if (zoneId.split(",").size > 1) {
                request.multiLoadAds(this)
            } else {
                request.loadAds(this)
            }
            SharedPreferenceManager.getInstance(this).putString(SharedPreferenceManager.NATIVE_AD_ZONE_ID, zoneId)
        } catch (e: Exception) {
            edtZoneId.error = e.localizedMessage
        }
    }

    private fun requestCellDataListAsync() {
        loading = true
        Handler(Looper.getMainLooper()).postDelayed({
            createCellDataList()
            loading = false
        }, 1000)
    }

    private fun createCellDataList() {
        repeat(5) {
            // dequeue() returns null when no ad is queued.
            cellDataList.add(queueAds.dequeue() ?: CellData())
        }
        adapter.notifyDataSetChanged()
    }

    companion object {
        private const val TAG = "[GNS]NativeDemoActivity"
    }
}
