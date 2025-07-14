package jp.co.geniee.samples.firebasegooglead

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import jp.co.geniee.samples.R
import jp.co.geniee.samples.firebasegooglead.FirebaseSettings.FIREBASE_KEY_REWARD
import jp.co.geniee.samples.firebasegooglead.FirebaseSettings.FIREBASE_MINIMUM_FETCH_INTERVAL_IN_SECONDS
import kotlinx.android.synthetic.main.activity_firebasegooglead_reward.*


class RewardNewAPIActivity : AppCompatActivity() {

    private val TAG = "RewardNewAPIActivity"
    private lateinit var mRewardedAd: RewardedAd
    private lateinit var mAdUnitId: String
    private var isInitRewardedAd = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebasegooglead_reward)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }
                ?: IllegalAccessException("Toolbar cannot be null")

        // Use an activity context to get the rewarded video instance.

        button_get_id.setOnClickListener { getUnitId() }
        button_load.setOnClickListener { loadRewardedVideoAd() }
        button_show.setOnClickListener { showRewardedVideoAd() }

        ableGetIdButton()
        disableLoadButton()
        disableShowButton()
    }

    private fun loadRewardedVideoAd() {
        if (isInitRewardedAd) {
            Log.d(TAG, "RewardedVideoAd is Loaded")
            Toast.makeText(this, "RewardedVideoAd is Loaded", Toast.LENGTH_SHORT).show()
            return
        }

        if (mAdUnitId.isEmpty()) {
            Log.d(TAG, "adUnitId is Empty")
            Toast.makeText(this, "adUnitId is Empty", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "loadRewardedVideoAd")
        Toast.makeText(this, "loadRewardedVideoAd", Toast.LENGTH_SHORT).show()

        getUnitId()

        disableGetIdButton()
        disableLoadButton()
        disableShowButton()
    }

    private fun showRewardedVideoAd() {
        if (isInitRewardedAd) {
            Log.d(TAG, "showRewardedVideoAd")

            val adCallback =
                    object : OnUserEarnedRewardListener {
                        /*override fun onRewardedAdOpened() {
                            Toast.makeText(
                                            this@RewardNewAPIActivity,
                                            "onRewardedAdOpened",
                                            Toast.LENGTH_SHORT
                                    )
                                    .show()
                            Log.d(TAG, "onRewardedAdOpened")
                        }

                        override fun onRewardedAdClosed() {
                            Toast.makeText(
                                            this@RewardNewAPIActivity,
                                            "onRewardedAdClosed",
                                            Toast.LENGTH_SHORT
                                    )
                                    .show()
                            Log.d(TAG, "onRewardedAdClosed")
                            disableGetIdButton()
                            ableLoadButton()
                            disableShowButton()

                            // Since "Rewarded" is a disposable object, create a new instance again
                            mRewardedAd = RewardedAd(this@RewardNewAPIActivity, mAdUnitId)
                        }*/

                        override fun onUserEarnedReward(reward: RewardItem) {
                            Toast.makeText(
                                            this@RewardNewAPIActivity,
                                            "onUserEarnedReward " + reward.amount + reward.type,
                                            Toast.LENGTH_SHORT
                                    )
                                    .show()
                            Log.d(TAG, "onUserEarnedReward " + reward.amount + reward.type)
                        }

                        /*override fun onRewardedAdFailedToShow(errorCode: Int) {
                            Toast.makeText(
                                            this@RewardNewAPIActivity,
                                            "onRewardedAdFailedToShow: $errorCode",
                                            Toast.LENGTH_SHORT
                                    )
                                    .show()
                            Log.d(TAG, "onRewardedAdFailedToShow: $errorCode")
                            disableGetIdButton()
                            ableLoadButton()
                            disableShowButton()

                            // Since "Rewarded" is a disposable object, create a new instance again
                            mRewardedAd = RewardedAd(this@RewardNewAPIActivity, mAdUnitId)
                        }*/
                    }

            mRewardedAd.show(this, adCallback)
            disableShowButton()
        } else {
            Log.d(TAG, "The RewardVideo wasn't loaded yet.")
            Toast.makeText(this, "he RewardVideo wasn't loaded yet.", Toast.LENGTH_SHORT).show()
        }
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

            mAdUnitId = firebaseRemoteConfig.getString(FIREBASE_KEY_REWARD)
            if (mAdUnitId.isEmpty()) {
                Log.d(TAG, "$FIREBASE_KEY_REWARD is Empty")
                Toast.makeText(
                                this,
                                "fetch_result: $result\n$FIREBASE_KEY_REWARD is Empty",
                                Toast.LENGTH_SHORT
                        )
                        .show()
                ableGetIdButton()
                return@addOnCompleteListener
            }
            Log.d(TAG, "adUnitId: $mAdUnitId")
            Toast.makeText(this, "fetch_result: $result\nadUnitId: $mAdUnitId", Toast.LENGTH_SHORT)
                    .show()
            ableLoadButton()
            RewardedAd.load(
                this,
                mAdUnitId,
                AdRequest.Builder().build(),
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        mRewardedAd = ad
                        isInitRewardedAd = true
                        //mRewardedAd.setFullScreenContentCallback(fullScreenContentCallback)

                        Toast.makeText(
                            this@RewardNewAPIActivity,
                            "onRewardedAdLoaded",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        Log.d(TAG, "onRewardedAdLoaded")
                        disableGetIdButton()
                        disableLoadButton()
                        ableShowButton()
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        Toast.makeText(
                            this@RewardNewAPIActivity,
                            "onRewardedAdFailedToLoad: ${p0.code}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        Log.d(TAG, "onRewardedAdFailedToLoad: ${p0.code}")
                        disableGetIdButton()
                        ableLoadButton()
                        disableShowButton()
                    }
                })
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
