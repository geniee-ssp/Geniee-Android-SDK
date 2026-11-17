package jp.co.geniee.samples.googlemediation

import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardItem
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardedAd
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardedAdEventCallback
import jp.co.geniee.samples.common.AdState
import jp.co.geniee.samples.common.SharedPreferenceManager

class RewardActivity : BaseGoogleAdActivity() {

    @Volatile
    private var rewardedAd: RewardedAd? = null

    @Volatile
    private var rewardItem: RewardItem? = null

    override fun getFormatName(): String = "Reward"

    // AdMob rewarded ad unit whose mediation group has Geniee as a custom event.
    override fun getDefaultAdUnitId(): String = "ca-app-pub-2963406282877816/4959490587"

    override fun getPreferenceKeySuffix(): String = SharedPreferenceManager.REWARDED_VIDEO_AD_ZONE_ID

    override fun getInfoLabels(): Array<String> = super.getInfoLabels() + INFO_REWARD

    override fun loadAd(adUnitId: String) {
        rewardedAd = null
        RewardedAd.load(AdRequest.Builder(adUnitId).build(), object : AdLoadCallback<RewardedAd> {
            override fun onAdLoaded(ad: RewardedAd) {
                ad.adEventCallback = adEventCallback
                rewardedAd = ad
                renderLoaded(ad.getResponseInfo())
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                renderFailed(adError)
            }
        })
    }

    override fun showAd() {
        val ad = rewardedAd ?: return
        rewardItem = null
        ad.show(this) { item ->
            rewardItem = item
            runOnUiThread { setInfo(INFO_REWARD, "${item.amount} ${item.type}") }
        }
    }

    override fun destroyAd() {
        rewardedAd = null
    }

    private val adEventCallback = object : RewardedAdEventCallback {
        override fun onAdShowedFullScreenContent() {
            renderStateOnUiThread(AdState.SHOWING, "Showing ad.")
        }

        override fun onAdFailedToShowFullScreenContent(fullScreenContentError: FullScreenContentError) {
            rewardedAd = null
            renderShowFailed(fullScreenContentError)
        }

        override fun onAdDismissedFullScreenContent() {
            rewardedAd = null
            val item = rewardItem
            val rewardMessage = if (item != null) "Rewarded: ${item.amount} ${item.type}." else "No reward."
            renderStateOnUiThread(AdState.CLOSED, rewardMessage)
        }
    }
}
