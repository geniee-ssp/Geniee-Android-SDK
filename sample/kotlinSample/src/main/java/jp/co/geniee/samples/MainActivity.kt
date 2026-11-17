package jp.co.geniee.samples

import android.content.Intent
import jp.co.geniee.samples.common.BaseMenuActivity
import jp.co.geniee.samples.common.MenuItem

class MainActivity : BaseMenuActivity() {
    // Geniee SDK screens not listed in its menu (Native, Multiple/XML banner, Video Player,
    // VAST, legacy Interstitial) are kept under the geniee package.
    override fun getListViewContents(): Array<MenuItem> = arrayOf(
        MenuItem("Geniee SDK", "Banner,Interstitial,Reward",
            Intent(this, jp.co.geniee.samples.geniee.MenuActivity::class.java)),
        MenuItem("AppLovin MAX Mediation", "Banner,Interstitial,Rewarded",
            Intent(this, jp.co.geniee.samples.maxmediation.MenuActivity::class.java)),
        MenuItem("Google Mediation", "Banner,Interstitial,Reward",
            Intent(this, jp.co.geniee.samples.googlemediation.MenuActivity::class.java)),
    )
}
