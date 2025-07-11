package jp.co.geniee.samples.gnad.banner

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import jp.co.geniee.samples.R
import jp.co.geniee.samples.SharedPreferenceManager
import jp.co.geniee.gnadsdk.banner.GNAdEventListener
import jp.co.geniee.gnadsdk.banner.GNAdSize
import jp.co.geniee.gnadsdk.banner.GNAdView
import jp.co.geniee.gnadsdk.banner.GNTouchType
import kotlinx.android.synthetic.main.activity_single_banner_demo.*

class SingleBannerDemoActivity : AppCompatActivity() {

    private val TAG = "[GNS]SingleBannerDemo"

    private lateinit var mContext: Context

    private var adView: GNAdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_banner_demo)

        mContext = this

        setUpSpinnerBannerSizes()

        edtZoneId.setText(SharedPreferenceManager.getInstance(mContext).getString(SharedPreferenceManager.SINGLE_BANNER_ZONE_ID))

        btLoadGNAd.setOnClickListener {
            prepareBannerView()

                try {
                    adView?.appId = edtZoneId.text.toString()
                    adView?.startAdLoop()

                    SharedPreferenceManager.getInstance(mContext).putString(SharedPreferenceManager.SINGLE_BANNER_ZONE_ID, edtZoneId.text.toString())
                } catch (e: Exception) {
                    edtZoneId.error = e.localizedMessage
                }
        }
    }

    override fun onPause() {
        super.onPause()
        adView?.stopAdLoop()
    }

    override fun onDestroy() {
        adView?.clearAdView()
        super.onDestroy()
    }

    private fun prepareBannerView() {

        adView?.clearAdView()
        AdviewLayout.removeView(adView)

        val adSize = when (spinnerBannerSizes.selectedItem.toString()) {
            "W320H50" -> GNAdSize.W320H50
            "W320H48" -> GNAdSize.W320H48
            "W300H250" -> GNAdSize.W300H250
            "W728H90" -> GNAdSize.W728H90
            "W468H60" -> GNAdSize.W468H60
            "W120H600" -> GNAdSize.W120H600
            "W320H100" -> GNAdSize.W320H100
            "W57H57" -> GNAdSize.W57H57
            "W76H76" -> GNAdSize.W76H76
            "W480H32" -> GNAdSize.W480H32
            "W768H66" -> GNAdSize.W768H66
            "W1024H66" -> GNAdSize.W1024H66
            else -> GNAdSize.W320H50
        }

        adView = GNAdView(mContext, adSize)
        adView?.useMediation(true)
        // alternatively, initialize with a GNTouchType
        //adView = new GNAdView(mContext,adSize,GNTouchType.TAP_AND_FLICK);
        val layout = findViewById<View>(R.id.AdviewLayout) as LinearLayout
        layout.addView(adView)

        adView?.listener = object :GNAdEventListener {
            override fun onReceiveAd(gnAdView: GNAdView) {
                Log.d(TAG, "onReceiveAd")
                Toast.makeText(mContext, "Ad received", Toast.LENGTH_LONG).show()
            }

            override fun onFaildToReceiveAd(gnAdView: GNAdView) {
                Log.d(TAG, "onFaildToReceiveAd")
                Toast.makeText(mContext, "Failed to receive ad", Toast.LENGTH_LONG).show()
            }

            override fun onStartExternalBrowser(gnAdView: GNAdView) {
                Log.d(TAG, "onStartExternalBrowser")
            }

            override fun onStartInternalBrowser(gnAdView: GNAdView) {
                Log.d(TAG, "onStartInternalBrowser")
            }

            override fun onTerminateInternalBrowser(gnAdView: GNAdView) {
                Log.d(TAG, "onTerminateInternalBrowser")
            }

            override fun onShouldStartInternalBrowserWithClick(s: String): Boolean {
                return false
            }

            override fun onAdHidden(adView: GNAdView?) {
                Log.d(TAG, "onAdHidden")
            }
        }


    }

    private fun setUpSpinnerBannerSizes() {
        val dataAdapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, this.resources.getStringArray(R.array.banner_size_arrays))
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBannerSizes.adapter = dataAdapter
        dataAdapter.notifyDataSetChanged()
    }
}
