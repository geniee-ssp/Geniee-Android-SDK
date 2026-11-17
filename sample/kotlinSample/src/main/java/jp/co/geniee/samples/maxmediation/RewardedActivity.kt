package jp.co.geniee.samples.maxmediation

import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxRewardedAd
import jp.co.geniee.samples.common.AdState

class RewardedActivity : BaseMaxAdActivity(), MaxRewardedAdListener {
    private var rewardedAd: MaxRewardedAd? = null
    private var reward: MaxReward? = null

    override fun getFormatName(): String = "Rewarded"

    override fun getDefaultAdUnitId(): String = MaxMediationHelper.REWARDED_AD_UNIT_ID

    override fun loadAd(adUnitId: String) {
        // MaxRewardedAd is a per-ad-unit singleton; detach from the previous one instead of destroying it.
        destroyAd()
        val ad = MaxRewardedAd.getInstance(adUnitId, this)
        ad.setListener(this)
        rewardedAd = ad
        ad.loadAd()
    }

    override fun showAd() {
        val ad = rewardedAd
        if (ad != null && ad.isReady) {
            reward = null
            ad.showAd(this)
        }
    }

    override fun destroyAd() {
        rewardedAd?.setListener(null)
        rewardedAd = null
    }

    // region MaxRewardedAdListener

    override fun onAdLoaded(ad: MaxAd) {
        renderLoaded(ad)
    }

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
        renderFailed(error)
    }

    override fun onAdDisplayed(ad: MaxAd) {
        renderState(AdState.SHOWING, "Showing ad from " + ad.networkName + ".")
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
        renderFailed(error)
    }

    override fun onUserRewarded(ad: MaxAd, reward: MaxReward) {
        this.reward = reward
    }

    override fun onAdHidden(ad: MaxAd) {
        val reward = reward
        val rewardMessage = if (reward != null) {
            "Rewarded: " + reward.amount + " " + reward.label + "."
        } else {
            "No reward."
        }
        renderState(AdState.CLOSED, rewardMessage)
    }

    override fun onAdClicked(ad: MaxAd) {}

    // endregion
}
