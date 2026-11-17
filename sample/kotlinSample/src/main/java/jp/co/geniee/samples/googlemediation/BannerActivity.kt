package jp.co.geniee.samples.googlemediation

import android.view.Gravity
import android.widget.FrameLayout
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize
import com.google.android.libraries.ads.mobile.sdk.banner.AdView
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError

class BannerActivity : BaseGoogleAdActivity() {

    private var adView: AdView? = null

    override fun getFormatName(): String = "Banner"

    // AdMob banner ad unit whose mediation group has Geniee as a custom event.
    override fun getDefaultAdUnitId(): String = "ca-app-pub-2963406282877816/6675896935"

    override fun getPreferenceKeySuffix(): String = "BANNER_AD_UNIT_ID"

    override fun isFullscreen(): Boolean = false

    override fun onCreateOptions() {
        setAdSlotSize(AD_SIZE.width, AD_SIZE.height)
    }

    override fun loadAd(adUnitId: String) {
        destroyAd()
        val view = AdView(this)
        adView = view
        getAdSlot().addView(view, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER))
        view.loadAd(BannerAdRequest.Builder(adUnitId, AD_SIZE).build(), object : AdLoadCallback<BannerAd> {
            override fun onAdLoaded(ad: BannerAd) {
                renderLoaded(ad.getResponseInfo())
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                renderFailed(adError)
            }
        })
    }

    override fun destroyAd() {
        adView?.let {
            getAdSlot().removeView(it)
            it.destroy()
        }
        adView = null
    }

    companion object {
        private val AD_SIZE = AdSize.MEDIUM_RECTANGLE
    }
}
