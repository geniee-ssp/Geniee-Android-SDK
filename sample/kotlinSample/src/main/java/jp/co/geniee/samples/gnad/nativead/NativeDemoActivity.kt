package jp.co.geniee.samples.gnad.nativead

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import jp.co.geniee.samples.R
import jp.co.geniee.samples.SharedPreferenceManager
import jp.co.geniee.samples.gnad.common.CellData
import jp.co.geniee.samples.gnad.common.GNQueue
import jp.co.geniee.samples.gnad.common.MemCache
import jp.co.geniee.gnadsdk.common.GNAdLogger
import jp.co.geniee.gnadsdk.common.GNSException
import jp.co.geniee.sdk.ads.nativead.GNNativeAd
import jp.co.geniee.sdk.ads.nativead.GNNativeAdRequest
import jp.co.geniee.sdk.ads.nativead.GNNativeAdRequestListener
import kotlinx.android.synthetic.main.activity_native_demo.*
import java.util.*

class NativeDemoActivity : AppCompatActivity() {

    private lateinit var mContext: Context

    private var loading = false
    private val queueAds = GNQueue(100)

    private var mNativeAdRequest: GNNativeAdRequest? = null

    private lateinit var mAdapter: NativeAdListViewAdapter
    private val cellDataList = ArrayList<Any>()

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
        setContentView(R.layout.activity_native_demo)

        mContext = this

        edtZoneId.setText(SharedPreferenceManager.getInstance(mContext).getString(SharedPreferenceManager.NATIVE_AD_ZONE_ID))

        mAdapter = NativeAdListViewAdapter(mContext, cellDataList)
        val listView = findViewById<ListView>(android.R.id.list)
        listView.adapter = mAdapter
        listView.addFooterView(layoutInflater.inflate(R.layout.listview_footer, null))
        listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            val cell = adapterView.getItemAtPosition(position)
            (cell as? GNNativeAd)?.onTrackingClick()
        }

        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScroll(absListView: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (totalItemCount - 1 > 0 && totalItemCount - 1 == cellDataList.size && !loading && totalItemCount == firstVisibleItem + visibleItemCount) {
                    mNativeAdRequest?.loadAds(this@NativeDemoActivity)
                    requestCellDataListAsync()
                }
            }
            override fun onScrollStateChanged(absListView: AbsListView, scrollState: Int) {
            }
        })

        btLoadNativeAd!!.setOnClickListener {
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
            mNativeAdRequest = GNNativeAdRequest(mContext, zoneId)
            mNativeAdRequest?.adListener = nativeListener
            //nativeAdRequest.setGeoLocationEnable(true);
            mNativeAdRequest?.setLogPriority(GNAdLogger.INFO)
            mNativeAdRequest?.loadAds(this@NativeDemoActivity)
            if (zoneId.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size > 1) {
                mNativeAdRequest!!.multiLoadAds(this)
            } else {
                mNativeAdRequest!!.loadAds(this)
            }
            SharedPreferenceManager.getInstance(mContext).putString(SharedPreferenceManager.NATIVE_AD_ZONE_ID, zoneId)
        } catch (e: Exception) {
            edtZoneId.error = e.localizedMessage
        }
    }

    private fun requestCellDataListAsync() {
        loading = true
        val mHandler = Handler()
        mHandler.postDelayed({
            createCellDataList()
            loading = false
        }, 1000)
    }

    private fun createCellDataList() {
        for (i in 0..4) {
            if (queueAds.size() > 0) {
                val ad = queueAds.dequeue()
                cellDataList.add(ad!!)
            } else {
                cellDataList.add(CellData())
            }
        }
        mAdapter.notifyDataSetChanged()
    }

    companion object {
        private val TAG = "[GNS]NativeDemoActivity"
    }

}
