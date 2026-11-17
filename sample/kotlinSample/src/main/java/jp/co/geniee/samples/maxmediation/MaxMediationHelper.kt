package jp.co.geniee.samples.maxmediation

import android.content.Context
import android.util.Log
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkInitializationConfiguration

/**
 * Shared helpers for the AppLovin MAX mediation samples (MAX mediating Geniee via
 * `jp.co.geniee.gnadmaxmediationadapter.GNMaxMediationAdapter`).
 */
object MaxMediationHelper {
    const val TAG = "MaxMediationSample"

    // Ad units created in the MAX dashboard with the "Geniee" custom network enabled.
    const val BANNER_AD_UNIT_ID = "7567c1f41ab00e31"
    const val INTERSTITIAL_AD_UNIT_ID = "c4ae0c08aa949b21"
    const val REWARDED_AD_UNIT_ID = "27003c1f447adf1a"

    // SDK key of the MAX account that owns the ad units above.
    private const val SDK_KEY = "N4G7uo57iqMqzxtL6sLW8hC0I7SL_CyNGDU90aGkOdXMZPifF2BOkNO1VrNV2KtJXLSTxkGJLpgvCNZRQmr88D"

    fun initialize(context: Context, onInitialized: () -> Unit) {
        val sdk = AppLovinSdk.getInstance(context)
        if (sdk.isInitialized) {
            onInitialized()
            return
        }

        Log.i(TAG, "Initializing AppLovin MAX SDK")

        // Verbose logging also prints GNMaxMediationAdapter's logs.
        sdk.settings.setVerboseLogging(true)

        val initConfig = AppLovinSdkInitializationConfiguration.builder(SDK_KEY, context)
            .setMediationProvider(AppLovinMediationProvider.MAX)
            .build()
        sdk.initialize(initConfig) {
            Log.i(TAG, "AppLovin MAX SDK initialized")
            onInitialized()
        }
    }

    fun showMediationDebugger(context: Context) {
        AppLovinSdk.getInstance(context).showMediationDebugger()
    }
}
