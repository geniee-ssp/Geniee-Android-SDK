package jp.co.geniee.samples.geniee.banner

import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.Spinner
import android.widget.Switch
import jp.co.geniee.gnadsdk.banner.GNAdEventListener
import jp.co.geniee.gnadsdk.banner.GNAdSize
import jp.co.geniee.gnadsdk.banner.GNAdView
import jp.co.geniee.gnadsdk.inspector.GNSAdInspectorListener
import jp.co.geniee.samples.R
import jp.co.geniee.samples.common.AdState
import jp.co.geniee.samples.common.BaseAdActivity
import jp.co.geniee.samples.common.SharedPreferenceManager

class SingleBannerDemoActivity : BaseAdActivity() {

    private var adView: GNAdView? = null
    private lateinit var spinnerBannerSizes: Spinner
    private lateinit var switchMediation: Switch
    private lateinit var switchCustomAdNetwork: Switch

    override fun getFormatName(): String = "Banner"

    override fun getProviderName(): String = "Geniee SDK"

    override fun getAdUnitLabel(): String = "Zone ID"

    override fun getDefaultAdUnitId(): String = ""

    override fun getAdUnitPreferenceKey(): String = SharedPreferenceManager.SINGLE_BANNER_ZONE_ID

    override fun isFullscreen(): Boolean = false

    override fun getInfoLabels(): Array<String> = arrayOf(INFO_NETWORK, INFO_AD_UNIT, INFO_SIZE, INFO_LATENCY)

    override fun onCreateOptions() {
        spinnerBannerSizes = addSpinner("Banner size", resources.getStringArray(R.array.banner_size_arrays))
        spinnerBannerSizes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val size = getSelectedAdSize()
                setAdSlotSize(size.width, size.height)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        switchMediation = addSwitch("Use mediation", SharedPreferenceManager.SWITCH_BANNER_MEDIATION)
        switchCustomAdNetwork = addSwitch("Custom ad network", SharedPreferenceManager.SWITCH_BANNER_CUSTOM)
    }

    override fun getExtraActionLabel(): String = "Open Ad Inspector"

    override fun onExtraActionClicked() {
        val view = adView
        if (view == null) {
            renderState(getState(), "Load a banner first.")
            return
        }
        view.setCustomAdNetworkEnabled(switchCustomAdNetwork.isChecked)
        view.openAdInspector(this, GNSAdInspectorListener { error ->
            if (error != null) {
                Log.d(TAG, "Ad Inspector error: " + error.message)
            }
        })
    }

    override fun loadAd(adUnitId: String) {
        destroyAd()

        val adSize = getSelectedAdSize()
        val view = GNAdView(this, adSize)
        adView = view
        view.setListener(listener)
        getAdSlot().addView(view, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER))
        setInfo(INFO_SIZE, "${adSize.width} × ${adSize.height}")

        try {
            view.setAppId(adUnitId)
            view.useMediation(switchMediation.isChecked)
            view.setCustomAdNetworkEnabled(switchCustomAdNetwork.isChecked)
            view.startAdLoop()
        } catch (e: Exception) {
            renderState(AdState.FAILED, e.localizedMessage ?: e.toString())
        }
    }

    override fun destroyAd() {
        adView?.let {
            it.clearAdView()
            getAdSlot().removeView(it)
        }
        adView = null
    }

    override fun onPause() {
        super.onPause()
        adView?.stopAdLoop()
    }

    private fun getSelectedAdSize(): GNAdSize = when (spinnerBannerSizes.selectedItem.toString()) {
        "W320H48" -> GNAdSize.W320H48
        "W300H250" -> GNAdSize.W300H250
        "W728H90" -> GNAdSize.W728H90
        "W468H60" -> GNAdSize.W468H60
        "W120H600" -> GNAdSize.W120H600
        "W320H100" -> GNAdSize.W320H100
        "W57H57" -> GNAdSize.W57H57
        "W76H76" -> GNAdSize.W76H76
        "W480H32" -> GNAdSize.W480H32
        "W768H66" -> GNAdSize.W768H66
        "W1024H66" -> GNAdSize.W1024H66
        else -> GNAdSize.W320H50
    }

    private val listener = object : GNAdEventListener {
        override fun onReceiveAd(gnAdView: GNAdView) {
            // Also called on every refresh; latency is only meaningful for the first request.
            if (getState() == AdState.LOADING) {
                setInfo(INFO_LATENCY, getElapsedSinceLoad())
            }
            setInfo(INFO_NETWORK, gnAdView.winnerNetworkName)
            setInfo(INFO_AD_UNIT, getAdUnitId())
            renderState(AdState.LOADED, "Banner displayed.")
        }

        override fun onFaildToReceiveAd(gnAdView: GNAdView) {
            setInfo(INFO_LATENCY, getElapsedSinceLoad())
            renderState(AdState.FAILED, "Failed to receive a banner ad.")
        }

        override fun onAdHidden(gnAdView: GNAdView) {}

        override fun onStartExternalBrowser(gnAdView: GNAdView) {}

        override fun onStartInternalBrowser(gnAdView: GNAdView) {}

        override fun onTerminateInternalBrowser(gnAdView: GNAdView) {}

        override fun onShouldStartInternalBrowserWithClick(s: String): Boolean = false
    }

    companion object {
        private const val TAG = "[GNS]SingleBannerDemo"
    }
}
