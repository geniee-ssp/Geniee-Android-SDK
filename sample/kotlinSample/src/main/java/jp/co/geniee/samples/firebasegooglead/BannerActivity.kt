package jp.co.geniee.samples.firebasegooglead

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import jp.co.geniee.samples.R
import jp.co.geniee.samples.firebasegooglead.FirebaseSettings.FIREBASE_KEY_BANNER
import jp.co.geniee.samples.firebasegooglead.FirebaseSettings.FIREBASE_MINIMUM_FETCH_INTERVAL_IN_SECONDS
import jp.co.geniee.samples.firebasegooglead.enums.BannerAdSize
import kotlinx.android.synthetic.main.activity_firebasegooglead_banner.*

class BannerActivity : AppCompatActivity() {
    private val TAG = "BannerActivity"
    private var mAdView: AdView? = null

    private val spinnerAdSizeItems =
            arrayOf(
                    BannerAdSize.BANNER.toString(),
                    BannerAdSize.LARGE_BANNER.toString(),
                    BannerAdSize.MEDIUM_RECTANGLE.toString()
            )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebasegooglead_banner)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }
                ?: IllegalAccessException("Toolbar cannot be null")

        mAdView = AdView(this)
        mAdView!!.adListener =
                object : AdListener() {
                    override fun onAdLoaded() {
                        Log.d(TAG, "onAdLoaded")
                        Toast.makeText(this@BannerActivity, "onAdLoaded", Toast.LENGTH_SHORT).show()
                        ableLoadButton()
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        Log.d(TAG, "onAdFailedToLoad:${p0.code}")
                        Toast.makeText(
                            this@BannerActivity,
                            "onAdFailedToLoad: ${p0.code}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        ableLoadButton()
                    }
                }
        ad_layout.addView(mAdView)

        val adapter =
                ArrayAdapter(
                        applicationContext,
                        android.R.layout.simple_spinner_item,
                        spinnerAdSizeItems
                )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_ad_size.adapter = adapter
        spinner_ad_size.setSelection(BannerAdSize.BANNER.ordinal)

        button_get_id.setOnClickListener { getUnitId() }
        button_load.setOnClickListener { loadBannerAd() }

        ableGetIdButton()
        disableLoadButton()
    }

    private fun getUnitId() {
        val firebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        val configSettings =
                FirebaseRemoteConfigSettings.Builder()
                        .setMinimumFetchIntervalInSeconds(
                                FIREBASE_MINIMUM_FETCH_INTERVAL_IN_SECONDS
                        )
                        .build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            val result: String =
                    if (task.isSuccessful) {
                        "Success"
                    } else {
                        "Failure"
                    }
            Log.d(TAG, "fetch result: $result")

            val adUnitId = firebaseRemoteConfig.getString(FIREBASE_KEY_BANNER)
            if (adUnitId.isNullOrEmpty()) {
                Log.d(TAG, "$FIREBASE_KEY_BANNER is Empty")
                Toast.makeText(
                                this,
                                "fetch_result: $result\n$FIREBASE_KEY_BANNER is Empty",
                                Toast.LENGTH_SHORT
                        )
                        .show()
                ableGetIdButton()
                return@addOnCompleteListener
            }
            Log.d(TAG, "adUnit_id: $adUnitId")
            mAdView!!.adUnitId = adUnitId
            Toast.makeText(
                            this@BannerActivity,
                            "fetch result: $result\nadUnit_id: $adUnitId",
                            Toast.LENGTH_SHORT
                    )
                    .show()

            disableGetIdButton()
            ableLoadButton()
        }
    }

    private fun loadBannerAd() {
        if (mAdView?.adUnitId == null) {
            Log.d(TAG, "adUnitId is Null")
            return
        }

        if (mAdView?.adSize == null) {
            mAdView!!.setAdSize(when (spinner_ad_size.selectedItemPosition) {
                BannerAdSize.BANNER.ordinal -> AdSize.BANNER
                BannerAdSize.LARGE_BANNER.ordinal -> AdSize.LARGE_BANNER
                else -> AdSize.MEDIUM_RECTANGLE
            })
            disableSpinnerAdSize()
        }

        val adRequest =
                AdRequest.Builder()
                        // .addTestDevice("YOUR_DEVICE_ID")
                        .build()
        mAdView!!.loadAd(adRequest)
        disableLoadButton()
    }

    private fun ableLoadButton() {
        button_load.isEnabled = true
    }

    private fun disableLoadButton() {
        button_load.isEnabled = false
    }

    private fun ableGetIdButton() {
        button_get_id.isEnabled = true
    }

    private fun disableGetIdButton() {
        button_get_id.isEnabled = false
    }

    private fun disableSpinnerAdSize() {
        spinner_ad_size.isEnabled = false
    }
}
