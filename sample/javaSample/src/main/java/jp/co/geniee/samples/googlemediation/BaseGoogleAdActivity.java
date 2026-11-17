package jp.co.geniee.samples.googlemediation;

import android.os.Bundle;
import android.util.Log;

import com.google.android.libraries.ads.mobile.sdk.MobileAds;
import com.google.android.libraries.ads.mobile.sdk.common.AdSourceResponseInfo;
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;
import com.google.android.libraries.ads.mobile.sdk.common.MediationAdError;
import com.google.android.libraries.ads.mobile.sdk.common.ResponseInfo;

import java.util.List;
import java.util.Locale;

import jp.co.geniee.samples.common.AdState;
import jp.co.geniee.samples.common.BaseAdActivity;

/**
 * Google Mobile Ads (Next-Gen SDK) flavour of the sample ad screen: maps {@link ResponseInfo} and
 * {@link LoadAdError} to the status card so the winning (or failing) adapter is visible.
 *
 * GMA Next-Gen SDK calls back on a background thread, so every render* method hops to the UI thread.
 */
public abstract class BaseGoogleAdActivity extends BaseAdActivity {
    private static final String PREF_KEY_PREFIX = "googlemediation_";
    // Logcat tag for the mediation waterfall: adb logcat -s GoogleMediation
    protected static final String MEDIATION_TAG = "GoogleMediation";

    protected static final String INFO_AD_SOURCE = "Ad source";
    protected static final String INFO_PARAMETER = "Parameter";
    protected static final String INFO_WATERFALL = "Waterfall";

    // Keys of AdSourceResponseInfo#getCredentials() for custom events (values set in the AdMob UI).
    private static final String CREDENTIAL_CLASS_NAME = "class_name";
    private static final String CREDENTIAL_LABEL = "label";
    private static final String CREDENTIAL_PARAMETER = "parameter";

    protected abstract String getPreferenceKeySuffix();

    @Override
    protected String getProviderName() {
        return "Google Mobile Ads Next-Gen";
    }

    @Override
    protected String getAdUnitPreferenceKey() {
        return PREF_KEY_PREFIX + getPreferenceKeySuffix();
    }

    @Override
    protected String[] getInfoLabels() {
        return new String[]{INFO_NETWORK, INFO_AD_SOURCE, INFO_ADAPTER_CLASS, INFO_PARAMETER,
                INFO_AD_UNIT, INFO_LATENCY, INFO_WATERFALL};
    }

    @Override
    protected String getExtraActionLabel() {
        return "Open Ad Inspector";
    }

    /**
     * Opens the GMA Ad Inspector. It only opens on a registered test device
     * (see SampleApplication); the listener is called on a background thread.
     */
    @Override
    protected void onExtraActionClicked() {
        MobileAds.openAdInspector(error -> {
            if (error != null) {
                renderStateOnUiThread(AdState.FAILED, String.format(Locale.US,
                        "Ad Inspector: %s (code %s)", error.getMessage(), error.getCode()));
            }
        });
    }

    protected void renderLoaded(ResponseInfo responseInfo) {
        logMediation("loaded", responseInfo);
        runOnUiThread(() -> {
            setInfo(INFO_AD_UNIT, getAdUnitId());
            AdSourceResponseInfo winner = responseInfo != null ? responseInfo.getLoadedAdSourceResponseInfo() : null;
            if (winner != null) {
                renderAdSource(winner);
            } else {
                setInfo(INFO_ADAPTER_CLASS, responseInfo != null ? responseInfo.getAdapterClassName() : null);
                setInfo(INFO_LATENCY, getElapsedSinceLoad());
            }
            setInfo(INFO_WATERFALL, describeWaterfall(responseInfo));
            renderState(AdState.LOADED, isFullscreen() ? "Ready to show." : "Banner displayed.");
        });
    }

    protected void renderFailed(LoadAdError error) {
        logMediation("failed: " + error.getMessage() + " (code " + error.getCode() + ")",
                error.getResponseInfo());
        runOnUiThread(() -> {
            StringBuilder message = new StringBuilder()
                    .append(error.getMessage())
                    .append(" (code ").append(error.getCode()).append(")");
            setInfo(INFO_AD_UNIT, getAdUnitId());
            setInfo(INFO_LATENCY, getElapsedSinceLoad());

            // Show the ad source that failed last, so mediation problems are visible without logcat.
            ResponseInfo responseInfo = error.getResponseInfo();
            List<AdSourceResponseInfo> adSources = responseInfo != null ? responseInfo.getAdSourceResponses() : null;
            if (adSources != null && !adSources.isEmpty()) {
                AdSourceResponseInfo failed = adSources.get(adSources.size() - 1);
                renderAdSource(failed);
                MediationAdError adSourceError = failed.getAdError();
                if (adSourceError != null) {
                    message.append("\n").append(describeNetwork(failed))
                            .append(": ").append(adSourceError.getMessage());
                }
            }
            setInfo(INFO_WATERFALL, describeWaterfall(responseInfo));
            renderState(AdState.FAILED, message.toString());
        });
    }

    /** Fills the network rows of the status card with one ad source (the winner, or the last failure). */
    private void renderAdSource(AdSourceResponseInfo source) {
        setInfo(INFO_NETWORK, describeNetwork(source));
        setInfo(INFO_AD_SOURCE, source.getInstanceName());
        setInfo(INFO_ADAPTER_CLASS, getAdapterClassName(source));
        setInfo(INFO_PARAMETER, getCredential(source, CREDENTIAL_PARAMETER));
        setInfo(INFO_LATENCY, formatMillis(source.getLatencyMillis()));
    }

    /**
     * Network name of an ad source. Custom events all report "Custom Event", so their label
     * (set in the AdMob UI, e.g. "Geniee_Banner") is appended to tell them apart.
     */
    private static String describeNetwork(AdSourceResponseInfo source) {
        String label = getCredential(source, CREDENTIAL_LABEL);
        return label != null ? source.getName() + " · " + label : source.getName();
    }

    /**
     * Adapter class that actually served the ad source. For custom events GMA may report its
     * generic CustomEventAdapter; the custom event class configured in AdMob is in the credentials.
     */
    private static String getAdapterClassName(AdSourceResponseInfo source) {
        String customEventClass = getCredential(source, CREDENTIAL_CLASS_NAME);
        return customEventClass != null ? customEventClass : source.getAdapterClassName();
    }

    private static String getCredential(AdSourceResponseInfo source, String key) {
        Bundle credentials = source.getCredentials();
        Object value = credentials != null ? credentials.get(key) : null;
        return value != null && !value.toString().isEmpty() ? value.toString() : null;
    }

    /** One line per ad source tried, in waterfall order, e.g. "1. Custom Event · Geniee_Reward: FAILED 81001 (10950 ms)". */
    private static String describeWaterfall(ResponseInfo responseInfo) {
        List<AdSourceResponseInfo> adSources = responseInfo != null ? responseInfo.getAdSourceResponses() : null;
        if (adSources == null || adSources.isEmpty()) {
            return null;
        }
        AdSourceResponseInfo winner = responseInfo.getLoadedAdSourceResponseInfo();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < adSources.size(); i++) {
            AdSourceResponseInfo source = adSources.get(i);
            MediationAdError sourceError = source.getAdError();
            String result;
            if (sourceError != null) {
                result = "FAILED " + sourceError.getCode();
            } else if (source.equals(winner)) {
                result = "WON";
            } else {
                result = "not used";
            }
            if (i > 0) {
                sb.append('\n');
            }
            sb.append(i + 1).append(". ").append(describeNetwork(source)).append(": ").append(result)
                    .append(" (").append(source.getLatencyMillis()).append(" ms)");
        }
        return sb.toString();
    }

    protected void renderShowFailed(FullScreenContentError error) {
        runOnUiThread(() -> renderState(AdState.FAILED, String.format(Locale.US, "Failed to show: %s (code %s)",
                error.getMessage(), error.getCode())));
    }

    /**
     * Logs the mediation waterfall of a response to logcat (tag {@value #MEDIATION_TAG}):
     * every ad source that was tried, in order, with its adapter, latency, credentials
     * (e.g. the custom event parameter) and result.
     */
    private void logMediation(String result, ResponseInfo responseInfo) {
        String header = getFormatName() + " " + result + " | adUnit=" + getAdUnitId();
        if (responseInfo == null) {
            Log.i(MEDIATION_TAG, header + " | no response info (request did not reach the waterfall)");
            return;
        }
        Log.i(MEDIATION_TAG, header + " | responseId=" + responseInfo.getResponseId()
                + " | adapter=" + responseInfo.getAdapterClassName());
        AdSourceResponseInfo loaded = responseInfo.getLoadedAdSourceResponseInfo();
        List<AdSourceResponseInfo> adSources = responseInfo.getAdSourceResponses();
        if (adSources == null || adSources.isEmpty()) {
            Log.i(MEDIATION_TAG, "  waterfall: no ad sources");
            return;
        }
        for (int i = 0; i < adSources.size(); i++) {
            AdSourceResponseInfo source = adSources.get(i);
            MediationAdError sourceError = source.getAdError();
            String sourceResult;
            if (sourceError != null) {
                sourceResult = "FAILED " + sourceError.getCode() + " " + sourceError.getMessage()
                        + " (" + sourceError.getDomain() + ")";
            } else if (source.equals(loaded)) {
                sourceResult = "LOADED";
            } else {
                sourceResult = "not used";
            }
            Log.i(MEDIATION_TAG, String.format(Locale.US,
                    "  [%d] %s | instance=%s | adapter=%s | latency=%dms | credentials=%s | %s",
                    i + 1, source.getName(), source.getInstanceName(), source.getAdapterClassName(),
                    source.getLatencyMillis(), formatBundle(source.getCredentials()), sourceResult));
        }
    }

    private static String formatBundle(Bundle bundle) {
        if (bundle == null || bundle.isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder("{");
        for (String key : bundle.keySet()) {
            if (sb.length() > 1) {
                sb.append(", ");
            }
            sb.append(key).append('=').append(bundle.get(key));
        }
        return sb.append('}').toString();
    }

    protected void renderStateOnUiThread(AdState state, String message) {
        runOnUiThread(() -> renderState(state, message));
    }
}
