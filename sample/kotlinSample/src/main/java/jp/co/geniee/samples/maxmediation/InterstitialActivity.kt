package jp.co.geniee.samples.maxmediation

import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import jp.co.geniee.samples.common.AdState

class InterstitialActivity : BaseMaxAdActivity(), MaxAdListener {
    private var interstitialAd: MaxInterstitialAd? = null
    private var loadedAdUnitId: String? = null

    override fun getFormatName(): String = "Interstitial"

    override fun getDefaultAdUnitId(): String = MaxMediationHelper.INTERSTITIAL_AD_UNIT_ID

    override fun loadAd(adUnitId: String) {
        // MaxInterstitialAd is bound to one ad unit; recreate it when the input changes.
        var ad = interstitialAd
        if (ad == null || adUnitId != loadedAdUnitId) {
            destroyAd()
            ad = MaxInterstitialAd(adUnitId, this)
            ad.setListener(this)
            interstitialAd = ad
            loadedAdUnitId = adUnitId
        }
        ad.loadAd()
    }

    override fun showAd() {
        interstitialAd?.takeIf { it.isReady }?.showAd(this)
    }

    override fun destroyAd() {
        interstitialAd?.destroy()
        interstitialAd = null
    }

    // region MaxAdListener

    override fun onAdLoaded(ad: MaxAd) {
        renderLoaded(ad)
    }

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
        renderFailed(error)
    }

    override fun onAdDisplayed(ad: MaxAd) {
        renderState(AdState.SHOWING, "Showing ad from " + ad.networkName + ".")
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
        renderFailed(error)
    }

    override fun onAdHidden(ad: MaxAd) {
        renderState(AdState.CLOSED, "Ad closed.")
    }

    override fun onAdClicked(ad: MaxAd) {}

    // endregion
}
