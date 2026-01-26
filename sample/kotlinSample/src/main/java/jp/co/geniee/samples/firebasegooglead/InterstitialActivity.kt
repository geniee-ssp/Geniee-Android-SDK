package jp.co.geniee.samples.firebasegooglead

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import jp.co.geniee.samples.R
import jp.co.geniee.samples.firebasegooglead.FirebaseSettings.FIREBASE_KEY_INTERSTITIAL
import jp.co.geniee.samples.firebasegooglead.FirebaseSettings.FIREBASE_MINIMUM_FETCH_INTERVAL_IN_SECONDS
import kotlinx.android.synthetic.main.activity_firebasegooglead_interstitial.*


class InterstitialActivity : AppCompatActivity() {

    private val TAG = "InterstitialActivity"
    private lateinit var mInterstitialAd: InterstitialAd
    private var isInitInterstitialAd = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebasegooglead_interstitial)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }
                ?: IllegalAccessException("Toolbar cannot be null")

        // Use an activity context to get the rewarded video instance.
        /*mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adListener =
                object : AdListener() {
                    override fun onAdClicked() {
                        super.onAdClicked()
                        Toast.makeText(this@InterstitialActivity, "onAdClicked", Toast.LENGTH_SHORT)
                                .show()
                        Log.d(TAG, "onAdClicked")
                    }
                    override fun onAdClosed() {
                        super.onAdClosed()
                        Toast.makeText(this@InterstitialActivity, "onAdClosed", Toast.LENGTH_SHORT)
                                .show()
                        Log.d(TAG, "onAdClosed")
                        disableGetIdButton()
                        ableLoadButton()
                        disableShowButton()
                    }
                    override fun onAdFailedToLoad(errorCode: Int) {
                        super.onAdFailedToLoad(errorCode)
                        Toast.makeText(
                                        this@InterstitialActivity,
                                        "onAdFailedToLoad $errorCode",
                                        Toast.LENGTH_SHORT
                                )
                                .show()
                        Log.d(TAG, "onAdFailedToLoad")
                        disableGetIdButton()
                        ableLoadButton()
                        disableShowButton()
                    }
                    override fun onAdImpression() {
                        super.onAdClosed()
                        Toast.makeText(
                                        this@InterstitialActivity,
                                        "onAdImpression",
                                        Toast.LENGTH_SHORT
                                )
                                .show()
                        Log.d(TAG, "onAdImpression")
                    }
                    override fun onAdLeftApplication() {
                        super.onAdLeftApplication()
                        Toast.makeText(
                                        this@InterstitialActivity,
                                        "onAdLeftApplication",
                                        Toast.LENGTH_SHORT
                                )
                                .show()
                        Log.d(TAG, "onAdLeftApplication")
                    }
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        Toast.makeText(this@InterstitialActivity, "onAdLoaded", Toast.LENGTH_SHORT)
                                .show()
                        Log.d(TAG, "onAdLoaded")
                        disableGetIdButton()
                        disableLoadButton()
                        ableShowButton()
                    }
                    override fun onAdOpened() {
                        super.onAdOpened()
                        Toast.makeText(this@InterstitialActivity, "onAdOpened", Toast.LENGTH_SHORT)
                                .show()
                        Log.d(TAG, "onAdOpened")
                    }
                }*/

        button_get_id.setOnClickListener { getUnitId() }
        button_load.setOnClickListener { loadInterstitialAd() }
        button_show.setOnClickListener { showInterstitialAd() }

        ableGetIdButton()
        disableLoadButton()
        disableShowButton()
    }

    private fun getUnitId() {
        disableGetIdButton()
        disableLoadButton()
        disableShowButton()

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

            val adUnitId = firebaseRemoteConfig.getString(FIREBASE_KEY_INTERSTITIAL)
            if (adUnitId.isNullOrEmpty()) {
                Log.d(TAG, "$FIREBASE_KEY_INTERSTITIAL is Empty")
                Toast.makeText(
                                this,
                                "fetch_result: $result\n$FIREBASE_KEY_INTERSTITIAL is Empty",
                                Toast.LENGTH_SHORT
                        )
                        .show()
                ableGetIdButton()
                return@addOnCompleteListener
            }
            Log.d(TAG, "adUnitId: $adUnitId")
            Toast.makeText(this, "fetch_result: $result\nadUnitId: $adUnitId", Toast.LENGTH_SHORT)
                    .show()

            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        isInitInterstitialAd = false
                    }
                }

            InterstitialAd.load(
                this,
                adUnitId,
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        mInterstitialAd = ad
                        isInitInterstitialAd = true
                        mInterstitialAd.setFullScreenContentCallback(fullScreenContentCallback)
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        // Code to be executed when an ad request fails.
                    }
                })

            disableGetIdButton()
            disableLoadButton()
            disableShowButton()

            ableLoadButton()
        }
    }

    private fun loadInterstitialAd() {
        if (isInitInterstitialAd) {
            Log.d(TAG, "loadInterstitialAd is Loaded")
            Toast.makeText(this, "loadInterstitialAd is Loaded", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "loadInterstitialAd")
        Toast.makeText(this, "loadInterstitialAd", Toast.LENGTH_SHORT).show()

        getUnitId()
    }

    private fun showInterstitialAd() {
        if (isInitInterstitialAd) {
            Log.d(TAG, "showRewardedVideoAd")
            mInterstitialAd.show(this)
            disableShowButton()
        } else {
            Log.d(TAG, "The interstitial wasn't loaded yet.")
            Toast.makeText(this, "he interstitial wasn't loaded yet.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun ableGetIdButton() {
        button_get_id.isEnabled = true
    }

    private fun disableGetIdButton() {
        button_get_id.isEnabled = false
    }

    private fun ableLoadButton() {
        button_load.isEnabled = true
    }

    private fun disableLoadButton() {
        button_load.isEnabled = false
    }

    private fun ableShowButton() {
        button_show.isEnabled = true
    }

    private fun disableShowButton() {
        button_show.isEnabled = false
    }
}
