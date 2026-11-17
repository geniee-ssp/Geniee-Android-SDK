package jp.co.geniee.samples.maxmediation

import android.content.Intent
import android.os.Bundle
import jp.co.geniee.samples.common.BaseMenuActivity
import jp.co.geniee.samples.common.MenuItem

class MenuActivity : BaseMenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize early so the first load request does not wait for SDK init.
        MaxMediationHelper.initialize(this) {}
    }

    override fun getListViewContents(): Array<MenuItem> = arrayOf(
        MenuItem("Banner", "", Intent(this, BannerActivity::class.java)),
        MenuItem("Interstitial", "", Intent(this, InterstitialActivity::class.java)),
        MenuItem("Rewarded", "", Intent(this, RewardedActivity::class.java)),
        MenuItem("Mediation Debugger", "Check that the Geniee adapter is detected", null),
    )

    override fun onMenuItemClick(position: Int) {
        if (position == MENU_POSITION_MEDIATION_DEBUGGER) {
            MaxMediationHelper.initialize(this) { MaxMediationHelper.showMediationDebugger(this) }
        }
    }

    companion object {
        private const val MENU_POSITION_MEDIATION_DEBUGGER = 3
    }
}
