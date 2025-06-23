package jp.co.geniee.samples.gnad.interstitial

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import jp.co.geniee.samples.R
import jp.co.geniee.samples.SharedPreferenceManager
import jp.co.geniee.gnadsdk.common.GNAdLogger
import jp.co.geniee.gnadsdk.interstitial.GNInterstitial

class InterstitialDemoActivity : AppCompatActivity(), GNInterstitial.GNInterstitialListener, GNInterstitial.GNInterstitialDialogListener, View.OnClickListener {

    private val TAG = "[GNS]InterstitialDemo"

    private lateinit var mContext: Context

    private lateinit var mGnInterstitial: GNInterstitial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interstitial_demo)

        mContext = this

        val edtZoneId = findViewById<TextView>(R.id.edtZoneId)
        edtZoneId.setText(SharedPreferenceManager.getInstance(mContext).getString(SharedPreferenceManager.INTERSTITIAL_AD_ZONE_ID))

        val btLoadGNAd = findViewById<Button>(R.id.btLoadGNAd)
        btLoadGNAd.setOnClickListener(this)
        val btShowGNAd = findViewById<Button>(R.id.btShowGNAd)
        btShowGNAd.setOnClickListener(this)
    }

    override fun onReceiveSetting() {
        val btShowGNAd = findViewById<Button>(R.id.btShowGNAd)
        btShowGNAd.isEnabled = true

        Log.d(TAG, "onReceiveSetting")

        Toast.makeText(mContext, "Interstitial ad received", Toast.LENGTH_SHORT).show()
    }

    override fun onFailedToReceiveSetting() {
        val btShowGNAd = findViewById<Button>(R.id.btShowGNAd)
        btShowGNAd!!.isEnabled = false

        Log.d(TAG, "onFailedToReceiveSetting")

        Toast.makeText(mContext, "Interstitial ad failed to receive", Toast.LENGTH_SHORT).show()
    }

    override fun onButtonClick(i: Int) {

        Log.d(TAG, "onButtonClick")
    }

    override fun onClose() {

        Log.d(TAG, "onClose interstitial")
        val btShowGNAd = findViewById<Button>(R.id.btShowGNAd)
        btShowGNAd!!.isEnabled = false
    }

    override fun onClick(view: View) {

        if (view.id == R.id.btLoadGNAd) {

            prepareInterstitialAd()

        } else if (view.id == R.id.btShowGNAd) {

            if (mGnInterstitial.isReady) {
                mGnInterstitial.show(this)
            }
        }

    }

    private fun prepareInterstitialAd() {
        val edtZoneId = findViewById<TextView>(R.id.edtZoneId)
        try {
            mGnInterstitial = GNInterstitial(this, Integer.parseInt(edtZoneId.text.toString()))
            mGnInterstitial.listener = this
            mGnInterstitial.dialoglistener = this
            mGnInterstitial.load(this)
            mGnInterstitial.setGeoLocationEnable(true)
            mGnInterstitial.setLogPriority(GNAdLogger.INFO)

            SharedPreferenceManager.getInstance(mContext).putString(SharedPreferenceManager.INTERSTITIAL_AD_ZONE_ID, edtZoneId.text.toString())
        } catch (e: Exception) {
            edtZoneId.error = e.localizedMessage
        }

    }
}
