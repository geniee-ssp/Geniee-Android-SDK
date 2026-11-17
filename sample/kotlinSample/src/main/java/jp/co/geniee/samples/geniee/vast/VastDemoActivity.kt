package jp.co.geniee.samples.geniee.vast

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import jp.co.geniee.gnadsdk.video.GNAdVideo
import jp.co.geniee.samples.R
import jp.co.geniee.samples.common.SharedPreferenceManager

class VastDemoActivity : AppCompatActivity(), GNAdVideo.GNAdVideoListener, View.OnClickListener {

    private var videoAd: GNAdVideo? = null
    private lateinit var edtZoneId: EditText
    private lateinit var loadAdButton: Button
    private lateinit var showAdButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gnad_vast)
        Log.d(TAG, "onCreate")
        edtZoneId = findViewById(R.id.edtZoneId)
        edtZoneId.setText(SharedPreferenceManager.getInstance(this).getString(SharedPreferenceManager.VAST_AD_ZONE_ID))

        // Sample button to load the Ad
        loadAdButton = findViewById(R.id.load_ad_button)
        loadAdButton.setOnClickListener(this)
        showAdButton = findViewById(R.id.show_ad_button)
        showAdButton.setOnClickListener(this)
        showAdButton.isEnabled = false
    }

    override fun onClick(view: View) {
        if (view === loadAdButton) {
            try {
                val zoneId = edtZoneId.text.toString()
                SharedPreferenceManager.getInstance(this).putString(SharedPreferenceManager.VAST_AD_ZONE_ID, zoneId)

                // Initializes a GNAdVideo
                val ad = GNAdVideo(this, zoneId.toInt())
                videoAd = ad
                // The alternative interstitial ad will be shown when no video ad.
                // ad.setAlternativeInterstitialAppID(YOUR_SSP_APP_ID_FOR_INTERSTITIAL)
                ad.setListener(this)
                //ad.setAutoCloseMode(false)            // Optional mode to automatically close after playing a video ad. Default: true
                //ad.setShowRate(100)                   // (Optional) Ad display frequency. (percentage)：Set the number between 0-100 (%). Default: 100
                //ad.setShowLimit(0)                    // (Optional) The maximum ad display numbers per app use. : Set the number 0 or higher. Default: 0 (No limited)
                //ad.setShowReset(0)                    // (Optional) The maximum reset fraction number of ad display per app
                //ad.setLogPriority(GNAdLogger.INFO)    // (Optional) log level
                //ad.setGeoLocationEnable(true)         // (Optional) location optimization. Default: false

                // Load GNAdVideo Ad
                ad.load(this)
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Err " + e.message, Toast.LENGTH_SHORT).show()
            }
        } else if (view === showAdButton) {
            showAdButton.isEnabled = false
            // Show GNAdVideo Ad
            videoAd?.let { if (it.isReady) it.show(this) }
        }
    }

    // GNAdVideoListener: sent when an video ad request succeeded.
    override fun onGNAdVideoReceiveSetting() {
        showAdButton.isEnabled = true
        Toast.makeText(this, "onGNAdVideoReceiveSetting", Toast.LENGTH_SHORT).show()
    }

    // GNAdVideoListener: sent when an video ad request failed.
    // (Network Connection Unavailable, Frequency capping, Out of ad stock)
    override fun onGNAdVideoFailedToReceiveSetting() {
        showAdButton.isEnabled = false
        Toast.makeText(this, "onGNAdVideoFailedToReceiveSetting", Toast.LENGTH_SHORT).show()
    }

    // GNAdVideoListener: sent just after closing ad because the user clicked skip button in video ad or
    // close button in alternative interstitial ad.
    override fun onGNAdVideoClose() {
        Toast.makeText(this, "onGNAdVideoClose", Toast.LENGTH_SHORT).show()
    }

    // GNAdVideoListener: sent just after closing alternative interstitial ad because the
    // user clicked the button configured through server-side.
    override fun onGNAdVideoButtonClick(nButtonIndex: Int) {
        Toast.makeText(this, "onGNAdVideoButtonClick:$nButtonIndex", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "[GNS]VastDemoActivity"
    }
}
