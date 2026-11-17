package jp.co.geniee.samples.geniee.banner

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import jp.co.geniee.gnadsdk.banner.GNAdEventListener
import jp.co.geniee.gnadsdk.banner.GNAdView
import jp.co.geniee.samples.R
import jp.co.geniee.samples.common.SharedPreferenceManager

/**
 * Demonstrates declaring a Geniee banner ([GNAdView]) directly in an XML layout.
 * Size, touch type and options are set from the layout via app: attributes; only the
 * zone id and the load trigger are handled in code.
 */
class XmlBannerDemoActivity : AppCompatActivity() {

    private var adView: GNAdView? = null
    private lateinit var edtZoneId: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gnad_xml_banner)

        // The banner is inflated from XML - just grab the reference.
        val view = findViewById<GNAdView>(R.id.gnAdView)
        adView = view
        view.setListener(object : GNAdEventListener {
            override fun onReceiveAd(gnAdView: GNAdView) {
                val winner = gnAdView.winnerNetworkName
                Log.d(TAG, "onReceiveAd - winnerNetworkName=$winner")
                Toast.makeText(this@XmlBannerDemoActivity, "Ad received - " + (winner ?: "Unknown"), Toast.LENGTH_LONG).show()
            }

            override fun onFaildToReceiveAd(gnAdView: GNAdView) {
                Log.d(TAG, "onFaildToReceiveAd")
                Toast.makeText(this@XmlBannerDemoActivity, "Failed to receive ad", Toast.LENGTH_LONG).show()
            }

            override fun onAdHidden(gnAdView: GNAdView) {
                Log.d(TAG, "onAdHidden")
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

            override fun onShouldStartInternalBrowserWithClick(s: String): Boolean = false
        })

        edtZoneId = findViewById(R.id.edtZoneId)
        edtZoneId.setText(SharedPreferenceManager.getInstance(this).getString(SharedPreferenceManager.SINGLE_BANNER_ZONE_ID))

        findViewById<Button>(R.id.btLoadGNAd).setOnClickListener {
            val zoneId = edtZoneId.text.toString()
            try {
                // Zone id can also be provided in the layout via app:zoneId.
                view.setAppId(zoneId)
                view.startAdLoop()
                SharedPreferenceManager.getInstance(this)
                    .putString(SharedPreferenceManager.SINGLE_BANNER_ZONE_ID, zoneId)
            } catch (e: Exception) {
                edtZoneId.error = e.localizedMessage
            }
        }
    }

    override fun onDestroy() {
        adView?.clearAdView()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "[GNS]XmlBannerDemo"
    }
}
