package jp.co.geniee.samples.maxmediation

import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdWaterfallInfo
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxNetworkResponseInfo
import jp.co.geniee.samples.common.AdState
import jp.co.geniee.samples.common.BaseAdActivity
import java.util.Locale

/**
 * MAX flavour of the sample ad screen: initializes the AppLovin SDK before loading and maps
 * [MaxAd] / [MaxError] to the status card.
 */
abstract class BaseMaxAdActivity : BaseAdActivity() {

    override fun getProviderName(): String = "AppLovin MAX · Geniee custom network"

    override fun getAdUnitPreferenceKey(): String =
        PREF_KEY_PREFIX + getFormatName().lowercase(Locale.US)

    override fun getInfoLabels(): Array<String> = arrayOf(
        INFO_NETWORK, INFO_ADAPTER_CLASS, INFO_ADAPTER_VERSION, INFO_SDK_VERSION,
        INFO_PLACEMENT, INFO_LATENCY, INFO_REVENUE,
    )

    override fun getExtraActionLabel(): String = "Open Mediation Debugger"

    override fun onExtraActionClicked() {
        MaxMediationHelper.initialize(this) { MaxMediationHelper.showMediationDebugger(this) }
    }

    override fun requestLoad(adUnitId: String) {
        MaxMediationHelper.initialize(this) { loadAd(adUnitId) }
    }

    protected fun renderLoaded(ad: MaxAd) {
        setInfo(INFO_NETWORK, ad.networkName)
        renderNetworkInfo(findResponse(ad.waterfall, MaxNetworkResponseInfo.AdLoadState.AD_LOADED))
        setInfo(INFO_PLACEMENT, ad.networkPlacement)
        setInfo(INFO_LATENCY, formatMillis(ad.requestLatencyMillis))
        setInfo(INFO_REVENUE, if (ad.revenue > 0) String.format(Locale.US, "$%.6f", ad.revenue) else null)
        renderState(AdState.LOADED, if (isFullscreen()) "Ready to show." else "Banner displayed.")
    }

    protected fun renderFailed(error: MaxError) {
        val message = StringBuilder()
            .append(error.message)
            .append(" (code ").append(error.code).append(")")
        if (error.mediatedNetworkErrorCode != 0) {
            message.append("\nNetwork error ").append(error.mediatedNetworkErrorCode)
                .append(": ").append(error.mediatedNetworkErrorMessage)
        }

        // Show the network that failed last, so adapter problems (e.g. a wrong adapter class
        // name in the dashboard) are visible without reading logcat.
        val failed = findResponse(error.waterfall, MaxNetworkResponseInfo.AdLoadState.FAILED_TO_LOAD)
        if (failed != null) {
            setInfo(INFO_NETWORK, failed.mediatedNetwork.name)
            setInfo(INFO_LATENCY, formatMillis(failed.latencyMillis))
            failed.error?.let { failedError ->
                message.append("\n").append(failed.mediatedNetwork.name)
                    .append(": ").append(failedError.message)
            }
        }
        renderNetworkInfo(failed)
        renderState(AdState.FAILED, message.toString())
    }

    private fun renderNetworkInfo(response: MaxNetworkResponseInfo?) {
        val network = response?.mediatedNetwork
        setInfo(INFO_ADAPTER_CLASS, network?.adapterClassName)
        setInfo(INFO_ADAPTER_VERSION, network?.adapterVersion)
        setInfo(INFO_SDK_VERSION, network?.sdkVersion)
    }

    companion object {
        private const val PREF_KEY_PREFIX = "maxmediation_ad_unit_"

        /** Returns the last response in the waterfall with the given state, or null. */
        private fun findResponse(
            waterfall: MaxAdWaterfallInfo?,
            state: MaxNetworkResponseInfo.AdLoadState,
        ): MaxNetworkResponseInfo? =
            waterfall?.networkResponses?.lastOrNull { it.adLoadState == state }
    }
}
