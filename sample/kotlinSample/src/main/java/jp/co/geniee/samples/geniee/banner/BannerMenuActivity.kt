package jp.co.geniee.samples.geniee.banner

import android.content.Intent
import jp.co.geniee.samples.common.BaseMenuActivity
import jp.co.geniee.samples.common.MenuItem

class BannerMenuActivity : BaseMenuActivity() {
    override fun getListViewContents(): Array<MenuItem> = arrayOf(
        MenuItem("Single Banner", "Classic banner ads", Intent(this, SingleBannerDemoActivity::class.java)),
        MenuItem("Multiple Banners", "Multiple banner ads", Intent(this, MultipleBannerDemoActivity::class.java)),
        MenuItem("XML Banner", "Banner declared in XML layout", Intent(this, XmlBannerDemoActivity::class.java)),
    )
}
