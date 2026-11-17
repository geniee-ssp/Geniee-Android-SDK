package jp.co.geniee.samples.geniee.banner

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.AbsListView
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import jp.co.geniee.gnadsdk.banner.GNAdView
import jp.co.geniee.gnadsdk.banner.GNAdViewRequest
import jp.co.geniee.gnadsdk.banner.GNAdViewRequestListener
import jp.co.geniee.samples.R
import jp.co.geniee.samples.common.SharedPreferenceManager
import jp.co.geniee.samples.geniee.common.CellData
import jp.co.geniee.samples.geniee.common.GNQueue
import jp.co.geniee.samples.geniee.common.MemCache

class MultipleBannerDemoActivity : AppCompatActivity(), GNAdViewRequestListener {

    private var loading = false
    private val queueAds = GNQueue(100)

    private var multiAdViewRequest: GNAdViewRequest? = null

    private lateinit var edtZoneId: EditText

    private lateinit var adapter: MultipleBannersListViewAdapter
    private val dataList = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gnad_multiple_banner)

        edtZoneId = findViewById(R.id.edtZoneId)
        edtZoneId.setText(SharedPreferenceManager.getInstance(this).getString(SharedPreferenceManager.MULTIPLE_BANNERS_ZONE_ID))

        findViewById<Button>(R.id.btLoadMultiBannerAd).setOnClickListener {
            initializeAd()
            requestCellDataListAsync()
        }

        adapter = MultipleBannersListViewAdapter(this, dataList)
        val listView = findViewById<ListView>(android.R.id.list)
        listView.adapter = adapter
        listView.addFooterView(layoutInflater.inflate(R.layout.listview_footer, null))

        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScroll(absListView: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (totalItemCount - 1 > 0 && totalItemCount - 1 == dataList.size && !loading
                    && totalItemCount == firstVisibleItem + visibleItemCount) {
                    multiAdViewRequest?.loadAds(this@MultipleBannerDemoActivity)
                    requestCellDataListAsync()
                }
            }

            override fun onScrollStateChanged(absListView: AbsListView, scrollState: Int) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()

        MemCache.clear()
        System.gc()
    }

    private fun initializeAd() {
        try {
            // Initialize SDK GNAdViewRequest
            // multiple zone id can be given, separated by commas
            //multiAdViewRequest = GNAdViewRequest(this, "ZONE_ID_1,ZONE_ID_2,ZONE_ID_3,...,ZONE_ID_10")
            val request = GNAdViewRequest(this, edtZoneId.text.toString())
            multiAdViewRequest = request
            request.setAdListener(this)
            //request.setGeoLocationEnable(true)
            //request.setLogPriority(GNAdLogger.INFO)
            request.loadAds(this)

            SharedPreferenceManager.getInstance(this)
                .putString(SharedPreferenceManager.MULTIPLE_BANNERS_ZONE_ID, edtZoneId.text.toString())
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
            val ad = if (queueAds.size() > 0) queueAds.dequeue() else null
            dataList.add(ad ?: CellData())
        }
        adapter.notifyDataSetChanged()
    }

    override fun onGNAdViewsLoaded(gnAdViews: Array<GNAdView>?) {
        if (!gnAdViews.isNullOrEmpty()) {
            for (adView in gnAdViews) {
                queueAds.enqueue(adView)
            }

            Toast.makeText(this, "Multiple banner ads received, num of ads = ${gnAdViews.size}", Toast.LENGTH_LONG).show()
            Log.d(TAG, "Multiple banner ads received, num of ads = ${gnAdViews.size}")
        }
    }

    override fun onGNAdViewsFailedToLoad() {
        Log.d(TAG, "onGNAdViewsFailedToLoad")
    }

    override fun onShouldStartInternalBrowserWithClick(s: String): Boolean = false

    companion object {
        private const val TAG = "MultipleBannerDemo"
    }
}
