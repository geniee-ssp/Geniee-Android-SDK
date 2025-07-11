package jp.co.geniee.samples

import android.content.Intent

class MainActivity : BaseMenuActivity() {

    override fun getListViewContents(): Array<MenuItem> {

        return arrayOf(
            MenuItem("GNAd", "Banner,Native,Interstitial (deprecated),Video Ad (deprecated)", Intent(this, jp.co.geniee.samples.gnad.MenuActivity::class.java)),
            MenuItem("Firebase", "Banner,Interstitial,Reward", Intent(this, jp.co.geniee.samples.firebasegooglead.MainActivity::class.java))
        )
    }
}
