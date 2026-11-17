package jp.co.geniee.samples.geniee.interstitial

import android.util.Log
import android.widget.Switch
import jp.co.geniee.gnadsdk.common.GNSException
import jp.co.geniee.gnadsdk.fullscreeninterstitial.GNSFullscreenInterstitialAd
import jp.co.geniee.gnadsdk.fullscreeninterstitial.GNSFullscreenInterstitialAdListener
import jp.co.geniee.gnadsdk.inspector.GNSAdInspectorListener
import jp.co.geniee.samples.common.AdState
import jp.co.geniee.samples.common.BaseAdActivity
import jp.co.geniee.samples.common.SharedPreferenceManager

class FullscreenInterstitialActivity : BaseAdActivity() {

    private var fullscreenInterstitial: GNSFullscreenInterstitialAd? = null
    private lateinit var switchCustomAdNetwork: Switch

    override fun getFormatName(): String = "Interstitial"

    override fun getProviderName(): String = "Geniee SDK"

    override fun getAdUnitLabel(): String = "Zone ID"

    override fun getDefaultAdUnitId(): String = ""

    override fun getAdUnitPreferenceKey(): String = SharedPreferenceManager.INTERSTITIAL_AD_ZONE_ID

    override fun getInfoLabels(): Array<String> = arrayOf(INFO_NETWORK, INFO_AD_UNIT, INFO_LATENCY)

    override fun onCreateOptions() {
        switchCustomAdNetwork = addSwitch("Custom ad network", SharedPreferenceManager.SWITCH_INTERSTITIAL_CUSTOM)
    }

    override fun getExtraActionLabel(): String = "Open Ad Inspector"

    override fun onExtraActionClicked() {
        val ad = getOrCreateAd(getAdUnitId())
        ad.setCustomAdNetworkEnabled(switchCustomAdNetwork.isChecked)
        ad.openAdInspector(this, GNSAdInspectorListener { error ->
            if (error != null) {
                Log.d(TAG, "Ad Inspector error: " + error.message)
            }
        })
    }

    override fun loadAd(adUnitId: String) {
        val ad = getOrCreateAd(adUnitId)
        ad.setZoneId(adUnitId)
        ad.setCustomAdNetworkEnabled(switchCustomAdNetwork.isChecked)
        ad.loadRequest()
    }

    override fun showAd() {
        val ad = fullscreenInterstitial
        if (ad != null && ad.canShow()) {
            ad.show()
        } else {
            renderState(AdState.FAILED, "Ad expired. Load again.")
        }
    }

    override fun destroyAd() {
        // The SDK releases the ad itself when the host activity is destroyed.
        fullscreenInterstitial = null
    }

    private fun getOrCreateAd(zoneId: String): GNSFullscreenInterstitialAd =
        fullscreenInterstitial ?: GNSFullscreenInterstitialAd(zoneId, this).also {
            it.setFullscreenInterstitialAdListener(listener)
            fullscreenInterstitial = it
        }

    private val listener = object : GNSFullscreenInterstitialAdListener {
        override fun fullscreenInterstitialAdDidReceiveAd(adNetworkName: String?) {
            setInfo(INFO_NETWORK, adNetworkName)
            setInfo(INFO_AD_UNIT, getAdUnitId())
            setInfo(INFO_LATENCY, getElapsedSinceLoad())
            renderState(AdState.LOADED, "Ready to show.")
        }

        override fun didFailToLoadWithError(e: GNSException) {
            setInfo(INFO_NETWORK, e.adnetworkName)
            setInfo(INFO_LATENCY, getElapsedSinceLoad())
            renderState(AdState.FAILED, "${e.message} (code ${e.code})")
        }

        override fun fullscreenInterstitialAdWillPresentScreen(adName: String?) {
            renderState(AdState.SHOWING, "Showing ad from $adName.")
        }

        override fun fullscreenInterstitialAdDidClose(adName: String?) {
            renderState(AdState.CLOSED, "Ad closed.")
        }
    }

    companion object {
        private const val TAG = "[GNS]FullscreenInterstitial"
    }
}
