package jp.co.geniee.samples.geniee.interstitial

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import jp.co.geniee.gnadsdk.common.GNAdLogger
import jp.co.geniee.gnadsdk.interstitial.GNInterstitial
import jp.co.geniee.samples.R
import jp.co.geniee.samples.common.SharedPreferenceManager

class InterstitialDemoActivity : AppCompatActivity(),
    GNInterstitial.GNInterstitialListener,
    GNInterstitial.GNInterstitialDialogListener,
    View.OnClickListener {

    private lateinit var edtZoneId: EditText
    private lateinit var btLoadGNAd: Button
    private lateinit var btShowGNAd: Button

    private var gnInterstitial: GNInterstitial? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gnad_interstitial)

        edtZoneId = findViewById(R.id.edtZoneId)
        edtZoneId.setText(SharedPreferenceManager.getInstance(this).getString(SharedPreferenceManager.INTERSTITIAL_AD_ZONE_ID))
        btLoadGNAd = findViewById(R.id.btLoadGNAd)
        btShowGNAd = findViewById(R.id.btShowGNAd)

        btLoadGNAd.setOnClickListener(this)
        btShowGNAd.setOnClickListener(this)
    }

    override fun onReceiveSetting() {
        btShowGNAd.isEnabled = true
        Log.d(TAG, "onReceiveSetting")
        Toast.makeText(this, "Interstitial ad received", Toast.LENGTH_SHORT).show()
    }

    override fun onFailedToReceiveSetting() {
        btShowGNAd.isEnabled = false
        Log.d(TAG, "onFailedToReceiveSetting")
        Toast.makeText(this, "Interstitial ad failed to receive", Toast.LENGTH_SHORT).show()
    }

    override fun onButtonClick(nButtonIndex: Int) {
        Log.d(TAG, "onButtonClick")
    }

    override fun onClose() {
        Log.d(TAG, "onClose interstitial")
        btShowGNAd.isEnabled = false
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btLoadGNAd -> prepareInterstitialAd()
            R.id.btShowGNAd -> gnInterstitial?.let { if (it.isReady) it.show(this) }
        }
    }

    private fun prepareInterstitialAd() {
        try {
            val interstitial = GNInterstitial(this, edtZoneId.text.toString().toInt())
            gnInterstitial = interstitial
            interstitial.setListener(this)
            interstitial.setDialoglistener(this)
            interstitial.load(this)
            interstitial.setGeoLocationEnable(true)
            interstitial.setLogPriority(GNAdLogger.INFO)

            SharedPreferenceManager.getInstance(this)
                .putString(SharedPreferenceManager.INTERSTITIAL_AD_ZONE_ID, edtZoneId.text.toString())
        } catch (e: Exception) {
            edtZoneId.error = e.localizedMessage
        }
    }

    companion object {
        private const val TAG = "[GNS]InterstitialDemo"
    }
}
