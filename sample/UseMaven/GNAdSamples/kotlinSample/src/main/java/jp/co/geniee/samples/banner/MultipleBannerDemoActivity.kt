package jp.co.geniee.samples.banner

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.AbsListView
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import jp.co.geniee.samples.R
import jp.co.geniee.samples.SharedPreferenceManager
import jp.co.geniee.samples.common.CellData
import jp.co.geniee.samples.common.GNQueue
import jp.co.geniee.samples.common.MemCache
import jp.co.geniee.gnadsdk.banner.GNAdView
import jp.co.geniee.gnadsdk.banner.GNAdViewRequest
import jp.co.geniee.gnadsdk.banner.GNAdViewRequestListener
import java.util.*

class MultipleBannerDemoActivity : AppCompatActivity(), GNAdViewRequestListener {

    private val TAG = "MultipleBannerDemo"

    private lateinit var mContext: Context

    private var loading = false
    private val queueAds = GNQueue(100)

    private var multiAdViewRequest: GNAdViewRequest? = null

    private var mAdapter: MultipleBannersListViewAdapter? = null
    private val mDataList = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_banner_demo)

        mContext = this

        val edtZoneId = findViewById<TextView>(R.id.edtZoneId)
        edtZoneId.setText(SharedPreferenceManager.getInstance(mContext).getString(SharedPreferenceManager.MULTIPLE_BANNERS_ZONE_ID))

        val btLoadMultiBannerAd = findViewById<Button>(R.id.btLoadMultiBannerAd)
        btLoadMultiBannerAd.setOnClickListener {
            initializeAd()
            requestCellDataListAsync()
        }

        mAdapter = MultipleBannersListViewAdapter(this, mDataList)
        val listView = findViewById<ListView>(android.R.id.list)
        listView.adapter = mAdapter
        listView.addFooterView(layoutInflater.inflate(R.layout.listview_footer, null))

        listView.setOnScrollListener(object : AbsListView.OnScrollListener {

            override fun onScroll(absListView: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (totalItemCount - 1 > 0 && totalItemCount - 1 == mDataList.size && !loading && totalItemCount == firstVisibleItem + visibleItemCount) {
                    multiAdViewRequest?.loadAds(this@MultipleBannerDemoActivity)
                    requestCellDataListAsync()
                }
            }

            override fun onScrollStateChanged(absListView: AbsListView, scrollState: Int) {

            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        MemCache.clear()
        System.gc()
    }

    private fun initializeAd() {
        val edtZoneId = findViewById<TextView>(R.id.edtZoneId)
        try {
            // Initialize SDK GNAdViewRequest
            multiAdViewRequest = GNAdViewRequest(this, edtZoneId!!.text.toString())
            multiAdViewRequest!!.adListener = this
            //multiAdViewRequest.setGeoLocationEnable(true);
            //multiAdViewRequest.setLogPriority(GNAdLogger.INFO);
            multiAdViewRequest!!.loadAds(this)

            SharedPreferenceManager.getInstance(mContext).putString(SharedPreferenceManager.MULTIPLE_BANNERS_ZONE_ID, edtZoneId.text.toString())

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
                mDataList.add(ad!!)
            } else {
                mDataList.add(CellData())
            }
        }
        mAdapter?.notifyDataSetChanged()
    }

    override fun onGNAdViewsLoaded(gnAdViews: Array<GNAdView>) {

        if (gnAdViews.isNotEmpty()) {
            for (adView in gnAdViews) {
                queueAds.enqueue(adView)
            }

            Toast.makeText(this, "Multiple banner ads received, num of ads = " + gnAdViews.size, Toast.LENGTH_LONG).show()
            Log.d(TAG, "Multiple banner ads received, num of ads = " + gnAdViews.size)
        }
    }

    override fun onGNAdViewsFailedToLoad() {
        Log.d(TAG, "onGNAdViewsFailedToLoad")
    }

    override fun onShouldStartInternalBrowserWithClick(s: String): Boolean {
        return false
    }
}
