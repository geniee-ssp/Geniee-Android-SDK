package jp.co.geniee.samples.googlemediation

import android.os.Bundle
import android.util.Log
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.common.AdSourceResponseInfo
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.common.ResponseInfo
import jp.co.geniee.samples.common.AdState
import jp.co.geniee.samples.common.BaseAdActivity
import java.util.Locale

/**
 * Google Mobile Ads (Next-Gen SDK) flavour of the sample ad screen: maps [ResponseInfo] and
 * [LoadAdError] to the status card so the winning (or failing) adapter is visible.
 *
 * GMA Next-Gen SDK calls back on a background thread, so every render* method hops to the UI thread.
 */
abstract class BaseGoogleAdActivity : BaseAdActivity() {

    protected abstract fun getPreferenceKeySuffix(): String

    override fun getProviderName(): String = "Google Mobile Ads Next-Gen"

    override fun getAdUnitPreferenceKey(): String = PREF_KEY_PREFIX + getPreferenceKeySuffix()

    override fun getInfoLabels(): Array<String> = arrayOf(
        INFO_NETWORK, INFO_AD_SOURCE, INFO_ADAPTER_CLASS, INFO_PARAMETER,
        INFO_AD_UNIT, INFO_LATENCY, INFO_WATERFALL,
    )

    override fun getExtraActionLabel(): String = "Open Ad Inspector"

    /**
     * Opens the GMA Ad Inspector. It only opens on a registered test device
     * (see SampleApplication); the listener is called on a background thread.
     */
    override fun onExtraActionClicked() {
        MobileAds.openAdInspector { error ->
            if (error != null) {
                renderStateOnUiThread(AdState.FAILED, String.format(Locale.US,
                    "Ad Inspector: %s (code %s)", error.message, error.code))
            }
        }
    }

    protected fun renderLoaded(responseInfo: ResponseInfo?) {
        logMediation("loaded", responseInfo)
        runOnUiThread {
            setInfo(INFO_AD_UNIT, getAdUnitId())
            val winner = responseInfo?.loadedAdSourceResponseInfo
            if (winner != null) {
                renderAdSource(winner)
            } else {
                setInfo(INFO_ADAPTER_CLASS, responseInfo?.adapterClassName)
                setInfo(INFO_LATENCY, getElapsedSinceLoad())
            }
            setInfo(INFO_WATERFALL, describeWaterfall(responseInfo))
            renderState(AdState.LOADED, if (isFullscreen()) "Ready to show." else "Banner displayed.")
        }
    }

    protected fun renderFailed(error: LoadAdError) {
        logMediation("failed: ${error.message} (code ${error.code})", error.responseInfo)
        runOnUiThread {
            val message = StringBuilder("${error.message} (code ${error.code})")
            setInfo(INFO_AD_UNIT, getAdUnitId())
            setInfo(INFO_LATENCY, getElapsedSinceLoad())

            // Show the ad source that failed last, so mediation problems are visible without logcat.
            val responseInfo = error.responseInfo
            val failed = responseInfo?.adSourceResponses?.lastOrNull()
            if (failed != null) {
                renderAdSource(failed)
                failed.adError?.let { adSourceError ->
                    message.append("\n").append(describeNetwork(failed))
                        .append(": ").append(adSourceError.message)
                }
            }
            setInfo(INFO_WATERFALL, describeWaterfall(responseInfo))
            renderState(AdState.FAILED, message.toString())
        }
    }

    /** Fills the network rows of the status card with one ad source (the winner, or the last failure). */
    private fun renderAdSource(source: AdSourceResponseInfo) {
        setInfo(INFO_NETWORK, describeNetwork(source))
        setInfo(INFO_AD_SOURCE, source.instanceName)
        setInfo(INFO_ADAPTER_CLASS, getAdapterClassName(source))
        setInfo(INFO_PARAMETER, getCredential(source, CREDENTIAL_PARAMETER))
        setInfo(INFO_LATENCY, formatMillis(source.latencyMillis))
    }

    protected fun renderShowFailed(error: FullScreenContentError) {
        runOnUiThread {
            renderState(AdState.FAILED, String.format(Locale.US,
                "Failed to show: %s (code %s)", error.message, error.code))
        }
    }

    /**
     * Logs the mediation waterfall of a response to logcat (tag [MEDIATION_TAG]):
     * every ad source that was tried, in order, with its adapter, latency, credentials
     * (e.g. the custom event parameter) and result.
     */
    private fun logMediation(result: String, responseInfo: ResponseInfo?) {
        val header = "${getFormatName()} $result | adUnit=${getAdUnitId()}"
        if (responseInfo == null) {
            Log.i(MEDIATION_TAG, "$header | no response info (request did not reach the waterfall)")
            return
        }
        Log.i(MEDIATION_TAG, "$header | responseId=${responseInfo.responseId}" +
            " | adapter=${responseInfo.adapterClassName}")
        val loaded = responseInfo.loadedAdSourceResponseInfo
        val adSources = responseInfo.adSourceResponses
        if (adSources.isNullOrEmpty()) {
            Log.i(MEDIATION_TAG, "  waterfall: no ad sources")
            return
        }
        adSources.forEachIndexed { i, source ->
            val sourceError = source.adError
            val sourceResult = when {
                sourceError != null ->
                    "FAILED ${sourceError.code} ${sourceError.message} (${sourceError.domain})"
                source == loaded -> "LOADED"
                else -> "not used"
            }
            Log.i(MEDIATION_TAG, String.format(Locale.US,
                "  [%d] %s | instance=%s | adapter=%s | latency=%dms | credentials=%s | %s",
                i + 1, source.name, source.instanceName, source.adapterClassName,
                source.latencyMillis, formatBundle(source.credentials), sourceResult))
        }
    }

    protected fun renderStateOnUiThread(state: AdState, message: String) {
        runOnUiThread { renderState(state, message) }
    }

    companion object {
        private const val PREF_KEY_PREFIX = "googlemediation_"

        // Logcat tag for the mediation waterfall: adb logcat -s GoogleMediation
        const val MEDIATION_TAG = "GoogleMediation"

        const val INFO_AD_SOURCE = "Ad source"
        const val INFO_PARAMETER = "Parameter"
        const val INFO_WATERFALL = "Waterfall"

        // Keys of AdSourceResponseInfo#getCredentials() for custom events (values set in the AdMob UI).
        private const val CREDENTIAL_CLASS_NAME = "class_name"
        private const val CREDENTIAL_LABEL = "label"
        private const val CREDENTIAL_PARAMETER = "parameter"

        /**
         * Network name of an ad source. Custom events all report "Custom Event", so their label
         * (set in the AdMob UI, e.g. "Geniee_Banner") is appended to tell them apart.
         */
        private fun describeNetwork(source: AdSourceResponseInfo): String {
            val label = getCredential(source, CREDENTIAL_LABEL)
            return if (label != null) "${source.name} · $label" else source.name
        }

        /**
         * Adapter class that actually served the ad source. For custom events GMA may report its
         * generic CustomEventAdapter; the custom event class configured in AdMob is in the credentials.
         */
        private fun getAdapterClassName(source: AdSourceResponseInfo): String? =
            getCredential(source, CREDENTIAL_CLASS_NAME) ?: source.adapterClassName

        @Suppress("DEPRECATION")
        private fun getCredential(source: AdSourceResponseInfo, key: String): String? =
            source.credentials?.get(key)?.toString()?.takeIf { it.isNotEmpty() }

        /** One line per ad source tried, in waterfall order, e.g. "1. Custom Event · Geniee_Reward: FAILED 81001 (10950 ms)". */
        private fun describeWaterfall(responseInfo: ResponseInfo?): String? {
            val adSources = responseInfo?.adSourceResponses
            if (adSources.isNullOrEmpty()) {
                return null
            }
            val winner = responseInfo.loadedAdSourceResponseInfo
            return adSources.mapIndexed { i, source ->
                val sourceError = source.adError
                val result = when {
                    sourceError != null -> "FAILED ${sourceError.code}"
                    source == winner -> "WON"
                    else -> "not used"
                }
                "${i + 1}. ${describeNetwork(source)}: $result (${source.latencyMillis} ms)"
            }.joinToString("\n")
        }

        @Suppress("DEPRECATION")
        private fun formatBundle(bundle: Bundle?): String {
            if (bundle == null || bundle.isEmpty) {
                return "{}"
            }
            return bundle.keySet().joinToString(", ", "{", "}") { key -> "$key=${bundle.get(key)}" }
        }
    }
}
