package jp.co.geniee.samples.banner

import android.content.Intent
import jp.co.geniee.samples.BaseMenuActivity
import jp.co.geniee.samples.MenuItem

class BannerMenuActivity : BaseMenuActivity() {
    override fun getListViewContents(): Array<MenuItem> {
        return arrayOf(
                MenuItem("Single Banner", "Classic banner ads", Intent(this, SingleBannerDemoActivity::class.java)),
                MenuItem("Multiple Banners", "Multiple banner ads", Intent(this, MultipleBannerDemoActivity::class.java))
        )
    }

}
