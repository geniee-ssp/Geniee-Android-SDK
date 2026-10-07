package jp.co.geniee.samples.maxmediation;

import android.content.Context;
import android.util.Log;

import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkInitializationConfiguration;

/**
 * Shared helpers for the AppLovin MAX mediation samples (MAX mediating Geniee via
 * {@code jp.co.geniee.gnadmaxmediationadapter.GNMaxMediationAdapter}).
 */
public final class MaxMediationHelper {
    public static final String TAG = "MaxMediationSample";

    // Ad units created in the MAX dashboard with the "Geniee" custom network enabled.
    public static final String BANNER_AD_UNIT_ID = "7567c1f41ab00e31";
    public static final String INTERSTITIAL_AD_UNIT_ID = "c4ae0c08aa949b21";
    public static final String REWARDED_AD_UNIT_ID = "27003c1f447adf1a";

    // SDK key of the MAX account that owns the ad units above.
    private static final String SDK_KEY = "N4G7uo57iqMqzxtL6sLW8hC0I7SL_CyNGDU90aGkOdXMZPifF2BOkNO1VrNV2KtJXLSTxkGJLpgvCNZRQmr88D";

    private MaxMediationHelper() {
    }

    public interface OnInitializedListener {
        void onInitialized();
    }

    public static void initialize(Context context, final OnInitializedListener listener) {
        AppLovinSdk sdk = AppLovinSdk.getInstance(context);
        if (sdk.isInitialized()) {
            listener.onInitialized();
            return;
        }

        Log.i(TAG, "Initializing AppLovin MAX SDK");

        // Verbose logging also prints GNMaxMediationAdapter's logs.
        sdk.getSettings().setVerboseLogging(true);

        AppLovinSdkInitializationConfiguration initConfig =
                AppLovinSdkInitializationConfiguration.builder(SDK_KEY, context)
                        .setMediationProvider(AppLovinMediationProvider.MAX)
                        .build();
        sdk.initialize(initConfig, configuration -> {
            Log.i(TAG, "AppLovin MAX SDK initialized");
            listener.onInitialized();
        });
    }

    public static void showMediationDebugger(Context context) {
        AppLovinSdk.getInstance(context).showMediationDebugger();
    }
}
