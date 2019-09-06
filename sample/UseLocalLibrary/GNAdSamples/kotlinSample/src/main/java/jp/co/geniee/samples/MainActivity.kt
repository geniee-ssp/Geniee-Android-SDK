package jp.co.geniee.samples

import android.content.Intent
import jp.co.geniee.samples.banner.BannerMenuActivity
import jp.co.geniee.samples.interstitial.InterstitialDemoActivity
import jp.co.geniee.samples.nativead.NativeAdMenuActivity
import jp.co.geniee.samples.vast.VastDemoActivity

class MainActivity : BaseMenuActivity() {

    override fun getListViewContents(): Array<MenuItem> {

        return arrayOf(
                MenuItem("Banner", "Banner ads", Intent(this, BannerMenuActivity::class.java)),
                MenuItem("Native Ad", "Native advertising is the use of paid ads that match the look, feel and function of the media format in which they appear", Intent(this, NativeAdMenuActivity::class.java)),
                MenuItem("Interstitial (deprecated)", "Interstitial ads are full-screen ads that cover the interface of their host app", Intent(this, InterstitialDemoActivity::class.java)),
                MenuItem("Video Ad (deprecated)", "", Intent(this, VastDemoActivity::class.java))
        )
    }
}
