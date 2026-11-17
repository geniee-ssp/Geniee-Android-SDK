package jp.co.geniee.samples.geniee.reward

import android.util.Log
import android.widget.Switch
import jp.co.geniee.gnadsdk.inspector.GNSAdInspectorListener
import jp.co.geniee.gnadsdk.rewardvideo.GNSRewardVideoAd
import jp.co.geniee.gnadsdk.rewardvideo.GNSRewardVideoAdListener
import jp.co.geniee.gnadsdk.rewardvideo.GNSVideoRewardData
import jp.co.geniee.gnadsdk.rewardvideo.GNSVideoRewardException
import jp.co.geniee.samples.common.AdState
import jp.co.geniee.samples.common.BaseAdActivity
import jp.co.geniee.samples.common.SharedPreferenceManager

class RewardVideoActivity : BaseAdActivity() {

    private var reward: GNSRewardVideoAd? = null
    private var rewardData: GNSVideoRewardData? = null
    private lateinit var switchRTB: Switch
    private lateinit var switchCustomAdNetwork: Switch

    override fun getFormatName(): String = "Reward"

    override fun getProviderName(): String = "Geniee SDK"

    override fun getAdUnitLabel(): String = "Zone ID"

    override fun getDefaultAdUnitId(): String = ""

    override fun getAdUnitPreferenceKey(): String = SharedPreferenceManager.REWARDED_VIDEO_AD_ZONE_ID

    override fun getInfoLabels(): Array<String> = arrayOf(INFO_NETWORK, INFO_AD_UNIT, INFO_LATENCY, INFO_REWARD)

    override fun onCreateOptions() {
        switchRTB = addSwitch("Use RTB", SharedPreferenceManager.SWITCH_REWARD_RTB)
        switchCustomAdNetwork = addSwitch("Custom ad network", SharedPreferenceManager.SWITCH_REWARD_CUSTOM)
    }

    override fun getExtraActionLabel(): String = "Open Ad Inspector"

    override fun onExtraActionClicked() {
        val ad = getOrCreateAd(getAdUnitId())
        ad.setCustomAdNetworkEnabled(switchCustomAdNetwork.isChecked)
        ad.openAdInspector(this, GNSAdInspectorListener { error ->
            if (error != null) {
                Log.d(TAG, "Ad Inspector error: " + error.message)
            }
        })
    }

    override fun loadAd(adUnitId: String) {
        val ad = getOrCreateAd(adUnitId)
        ad.setZoneId(adUnitId)
        ad.setCustomAdNetworkEnabled(switchCustomAdNetwork.isChecked)
        ad.loadRequest(switchRTB.isChecked)
    }

    override fun showAd() {
        val ad = reward
        if (ad != null && ad.canShow()) {
            rewardData = null
            ad.show()
        } else {
            renderState(AdState.FAILED, "Ad expired. Load again.")
        }
    }

    override fun destroyAd() {
        // The SDK releases the ad itself when the host activity is destroyed.
        reward = null
    }

    private fun getOrCreateAd(zoneId: String): GNSRewardVideoAd =
        reward ?: GNSRewardVideoAd(zoneId, this).also {
            it.setRewardVideoAdListener(listener)
            reward = it
        }

    private val listener = object : GNSRewardVideoAdListener {
        override fun rewardVideoAdDidReceiveAd(adNetworkName: String?) {
            setInfo(INFO_NETWORK, adNetworkName)
            setInfo(INFO_AD_UNIT, getAdUnitId())
            setInfo(INFO_LATENCY, getElapsedSinceLoad())
            renderState(AdState.LOADED, "Ready to show.")
        }

        override fun rewardVideoAdDidStartPlaying(data: GNSVideoRewardData) {
            renderState(AdState.SHOWING, "Showing ad from ${data.adName}.")
        }

        override fun didRewardUserWithReward(data: GNSVideoRewardData) {
            rewardData = data
            setInfo(INFO_REWARD, "${data.amount} ${data.type}")
        }

        override fun rewardVideoAdDidClose(data: GNSVideoRewardData?) {
            val rewarded = rewardData
            val rewardMessage = if (rewarded != null) {
                "Rewarded: ${rewarded.amount} ${rewarded.type}."
            } else {
                "No reward."
            }
            renderState(AdState.CLOSED, rewardMessage)
        }

        override fun didFailToLoadWithError(e: GNSVideoRewardException) {
            setInfo(INFO_NETWORK, e.adnetworkName)
            setInfo(INFO_LATENCY, getElapsedSinceLoad())
            renderState(AdState.FAILED, "${e.message} (code ${e.code})")
        }
    }

    companion object {
        private const val TAG = "[GNS]RewardVideo"
    }
}
