package jp.co.geniee.samples.gnad

import android.content.Intent

import jp.co.geniee.samples.BaseMenuActivity
import jp.co.geniee.samples.MenuItem
import jp.co.geniee.samples.gnad.banner.BannerMenuActivity
import jp.co.geniee.samples.gnad.interstitial.InterstitialDemoActivity
import jp.co.geniee.samples.gnad.nativead.NativeAdMenuActivity
import jp.co.geniee.samples.gnad.vast.VastDemoActivity

class MenuActivity : BaseMenuActivity() {

    override fun getListViewContents(): Array<MenuItem> {

        return arrayOf(
                MenuItem("Banner", "Banner ads", Intent(this, BannerMenuActivity::class.java)),
                MenuItem("Native Ad", "Native advertising is the use of paid ads that match the look, feel and function of the media format in which they appear", Intent(this, NativeAdMenuActivity::class.java)),
                MenuItem("Interstitial (deprecated)", "Interstitial ads are full-screen ads that cover the interface of their host app", Intent(this, InterstitialDemoActivity::class.java)),
                MenuItem("Video Ad (deprecated)", "", Intent(this, VastDemoActivity::class.java))
        )
    }
}
