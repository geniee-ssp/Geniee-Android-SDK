package jp.co.geniee.samples.googlemediation

import android.content.Intent
import jp.co.geniee.samples.common.BaseMenuActivity
import jp.co.geniee.samples.common.MenuItem

class MenuActivity : BaseMenuActivity() {
    override fun getListViewContents(): Array<MenuItem> = arrayOf(
        MenuItem("Banner", "", Intent(this, BannerActivity::class.java)),
        MenuItem("Interstitial", "", Intent(this, FullscreenInterstitialActivity::class.java)),
        MenuItem("Reward", "", Intent(this, RewardActivity::class.java)),
    )
}
