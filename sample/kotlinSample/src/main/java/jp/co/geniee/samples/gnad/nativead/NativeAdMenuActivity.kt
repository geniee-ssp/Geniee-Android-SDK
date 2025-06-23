package jp.co.geniee.samples.gnad.nativead

import android.content.Intent

import jp.co.geniee.samples.BaseMenuActivity
import jp.co.geniee.samples.MenuItem

class NativeAdMenuActivity : BaseMenuActivity() {
    override fun getListViewContents(): Array<MenuItem> {
        return arrayOf(
                MenuItem("Native Ad", "Native advertising is the use of paid ads that match the look, feel and function of the media format in which they appear", Intent(this, NativeDemoActivity::class.java)),
                MenuItem("Simple Native Video Ad", "", Intent(this, NativeSimpleVideoDemoActivity::class.java)),
                MenuItem("Native Video Ad", "", Intent(this, NativeVideoDemoActivity::class.java))
        )
    }
}
