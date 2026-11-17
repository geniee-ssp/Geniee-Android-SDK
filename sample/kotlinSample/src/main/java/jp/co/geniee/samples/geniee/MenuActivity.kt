package jp.co.geniee.samples.geniee

import android.content.Intent
import jp.co.geniee.samples.common.BaseMenuActivity
import jp.co.geniee.samples.common.MenuItem
import jp.co.geniee.samples.geniee.banner.SingleBannerDemoActivity
import jp.co.geniee.samples.geniee.interstitial.FullscreenInterstitialActivity
import jp.co.geniee.samples.geniee.reward.RewardVideoActivity

class MenuActivity : BaseMenuActivity() {
    // Native, Multiple/XML banners, Interstitial (deprecated) and Video Ad (deprecated) are
    // kept in the project but no longer listed here.
    override fun getListViewContents(): Array<MenuItem> = arrayOf(
        MenuItem("Banner", "", Intent(this, SingleBannerDemoActivity::class.java)),
        MenuItem("Interstitial", "", Intent(this, FullscreenInterstitialActivity::class.java)),
        MenuItem("Reward", "", Intent(this, RewardVideoActivity::class.java)),
    )
}
