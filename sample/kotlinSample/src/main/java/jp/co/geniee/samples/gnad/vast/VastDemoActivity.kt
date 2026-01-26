package jp.co.geniee.samples.gnad.vast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import jp.co.geniee.samples.R
import jp.co.geniee.samples.SharedPreferenceManager
import jp.co.geniee.gnadsdk.video.GNAdVideo
import kotlinx.android.synthetic.main.activity_vast_demo.*

class VastDemoActivity : AppCompatActivity(), GNAdVideo.GNAdVideoListener, View.OnClickListener {

    private lateinit var videoAd: GNAdVideo

    private val YOUR_APP_SSP_ID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vast_demo)
        Log.d(TAG, "onCreate")
        edtZoneId.setText(SharedPreferenceManager.getInstance(this).getString(SharedPreferenceManager.VAST_AD_ZONE_ID))

        // Sample button to load the Ad
        loadAdButton.setOnClickListener(this)
        showAdButton.setOnClickListener(this)
        showAdButton.isEnabled = false

        // Initializes a GNAdVideo
        videoAd = GNAdVideo(this, YOUR_APP_SSP_ID)
        // The alternative interstitial ad will be shown when no video ad.
        // videoAd.setAlternativeInterstitialAppID(YOUR_SSP_APP_ID_FOR_INTERSTITIAL);
        videoAd.listener = this
    }

    override fun onClick(view: View) {
        if (view === loadAdButton) {
            try {
                val zoneId = edtZoneId!!.text.toString()
                SharedPreferenceManager.getInstance(this).putString(SharedPreferenceManager.VAST_AD_ZONE_ID, zoneId)

                // Initializes a GNAdVideo
                videoAd = GNAdVideo(this, Integer.parseInt(zoneId))
                // The alternative interstitial ad will be shown when no video ad.
                // videoAd.setAlternativeInterstitialAppID(YOUR_SSP_APP_ID_FOR_INTERSTITIAL);
                videoAd.listener = this
                //videoAd.setAutoCloseMode(false);            // Optional mode to automatically close after playing a video ad. Default: true
                //videoAd.setShowRate(100);                   // (Optional) Ad display frequency. (percentage)：Set the number between 0-100 (%). Default: 100
                //videoAd.setShowLimit(0);                    // (Optional) The maximum ad display numbers per app use. : Set the number 0 or higher. Default: 0 (No limited)
                //videoAd.setShowReset(0);                    // (Optional) The maximum reset fraction number of ad display per app
                //videoAd.setLogPriority(GNAdLogger.INFO);    // (Optional) log level
                //videoAd.setGeoLocationEnable(true);         // (Optional) location optimization. Default: false

                // Load GNAdVideo Ad
                videoAd.load(this)
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Err " + e.message, Toast.LENGTH_SHORT).show()
            }
        } else if (view === showAdButton) {
            showAdButton.isEnabled = false
            // Show GNAdVideo Ad
            if (videoAd.isReady) {
                videoAd.show(this)
            }
        }
    }

    override fun onGNAdVideoReceiveSetting() {
        showAdButton.isEnabled = true
        Toast.makeText(this, "onGNAdVideoReceiveSetting", Toast.LENGTH_SHORT).show()
    }

    override fun onGNAdVideoFailedToReceiveSetting() {
        showAdButton.isEnabled = false
        Toast.makeText(this, "onGNAdVideoFailedToReceiveSetting", Toast.LENGTH_SHORT).show()
    }


    override fun onGNAdVideoClose() {
        Toast.makeText(this, "onGNAdVideoClose", Toast.LENGTH_SHORT).show()
    }

    override fun onGNAdVideoButtonClick(nButtonIndex: Int) {
        Toast.makeText(this, "onGNAdVideoButtonClick:" + nButtonIndex, Toast.LENGTH_SHORT).show()
    }

    companion object {

        private val TAG = "[GNS]VastDemoActivity"
    }
}
