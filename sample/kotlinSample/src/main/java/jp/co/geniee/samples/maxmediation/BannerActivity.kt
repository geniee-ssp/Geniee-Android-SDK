package jp.co.geniee.samples.maxmediation

import android.widget.FrameLayout
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView

class BannerActivity : BaseMaxAdActivity(), MaxAdViewAdListener {
    private var adView: MaxAdView? = null

    override fun getFormatName(): String = "Banner"

    override fun getDefaultAdUnitId(): String = MaxMediationHelper.BANNER_AD_UNIT_ID

    override fun isFullscreen(): Boolean = false

    override fun loadAd(adUnitId: String) {
        destroyAd()
        val view = MaxAdView(adUnitId, this)
        view.setListener(this)
        getAdSlot().addView(view, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        adView = view
        view.loadAd()
    }

    override fun destroyAd() {
        adView?.let {
            getAdSlot().removeView(it)
            it.destroy()
        }
        adView = null
    }

    // region MaxAdViewAdListener

    override fun onAdLoaded(ad: MaxAd) {
        // Also called on every auto-refresh, so the status always shows the current winner.
        renderLoaded(ad)
    }

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
        renderFailed(error)
    }

    override fun onAdDisplayed(ad: MaxAd) {}

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
        renderFailed(error)
    }

    override fun onAdHidden(ad: MaxAd) {}

    override fun onAdClicked(ad: MaxAd) {}

    override fun onAdExpanded(ad: MaxAd) {}

    override fun onAdCollapsed(ad: MaxAd) {}

    // endregion
}
