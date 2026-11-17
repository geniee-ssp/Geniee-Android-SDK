package jp.co.geniee.samples.googlemediation

import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback
import jp.co.geniee.samples.common.AdState
import jp.co.geniee.samples.common.SharedPreferenceManager

class FullscreenInterstitialActivity : BaseGoogleAdActivity() {

    @Volatile
    private var interstitialAd: InterstitialAd? = null

    override fun getFormatName(): String = "Interstitial"

    // AdMob interstitial ad unit whose mediation group has Geniee as a custom event.
    override fun getDefaultAdUnitId(): String = "ca-app-pub-2963406282877816/9744400052"

    override fun getPreferenceKeySuffix(): String = SharedPreferenceManager.INTERSTITIAL_AD_ZONE_ID

    override fun loadAd(adUnitId: String) {
        interstitialAd = null
        // When debugging, set the test device with RequestConfiguration.Builder#setTestDeviceIds (see SampleApplication).
        // Please do not forget to delete this setting when release.
        InterstitialAd.load(AdRequest.Builder(adUnitId).build(), object : AdLoadCallback<InterstitialAd> {
            override fun onAdLoaded(ad: InterstitialAd) {
                ad.adEventCallback = adEventCallback
                interstitialAd = ad
                renderLoaded(ad.getResponseInfo())
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                renderFailed(adError)
            }
        })
    }

    override fun showAd() {
        interstitialAd?.show(this)
    }

    override fun destroyAd() {
        interstitialAd = null
    }

    private val adEventCallback = object : InterstitialAdEventCallback {
        override fun onAdShowedFullScreenContent() {
            renderStateOnUiThread(AdState.SHOWING, "Showing ad.")
        }

        override fun onAdFailedToShowFullScreenContent(fullScreenContentError: FullScreenContentError) {
            interstitialAd = null
            renderShowFailed(fullScreenContentError)
        }

        override fun onAdDismissedFullScreenContent() {
            interstitialAd = null
            renderStateOnUiThread(AdState.CLOSED, "Ad closed.")
        }
    }
}
