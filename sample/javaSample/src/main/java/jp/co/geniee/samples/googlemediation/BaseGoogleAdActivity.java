package jp.co.geniee.samples.googlemediation;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdapterResponseInfo;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.ResponseInfo;

import java.util.List;
import java.util.Locale;

import jp.co.geniee.samples.common.AdState;
import jp.co.geniee.samples.common.BaseAdActivity;

/**
 * Google Mobile Ads flavour of the sample ad screen: maps {@link ResponseInfo} and
 * {@link LoadAdError} to the status card so the winning (or failing) adapter is visible.
 */
public abstract class BaseGoogleAdActivity extends BaseAdActivity {
    private static final String PREF_KEY_PREFIX = "googlemediation_";

    protected abstract String getPreferenceKeySuffix();

    @Override
    protected String getProviderName() {
        return "Google Mobile Ads · Geniee mediation";
    }

    @Override
    protected String getAdUnitPreferenceKey() {
        return PREF_KEY_PREFIX + getPreferenceKeySuffix();
    }

    @Override
    protected String[] getInfoLabels() {
        return new String[]{INFO_NETWORK, INFO_ADAPTER_CLASS, INFO_AD_UNIT, INFO_LATENCY};
    }

    protected void renderLoaded(ResponseInfo responseInfo) {
        setInfo(INFO_AD_UNIT, getAdUnitId());
        AdapterResponseInfo adapter = responseInfo != null ? responseInfo.getLoadedAdapterResponseInfo() : null;
        if (adapter != null) {
            setInfo(INFO_NETWORK, adapter.getAdSourceName());
            setInfo(INFO_ADAPTER_CLASS, adapter.getAdapterClassName());
            setInfo(INFO_LATENCY, formatMillis(adapter.getLatencyMillis()));
        } else {
            setInfo(INFO_ADAPTER_CLASS, responseInfo != null ? responseInfo.getMediationAdapterClassName() : null);
            setInfo(INFO_LATENCY, getElapsedSinceLoad());
        }
        renderState(AdState.LOADED, isFullscreen() ? "Ready to show." : "Banner displayed.");
    }

    protected void renderFailed(LoadAdError error) {
        StringBuilder message = new StringBuilder()
                .append(error.getMessage())
                .append(" (code ").append(error.getCode()).append(")");
        setInfo(INFO_AD_UNIT, getAdUnitId());
        setInfo(INFO_LATENCY, getElapsedSinceLoad());

        // Show the adapter that failed last, so mediation problems are visible without logcat.
        ResponseInfo responseInfo = error.getResponseInfo();
        List<AdapterResponseInfo> adapters = responseInfo != null ? responseInfo.getAdapterResponses() : null;
        if (adapters != null && !adapters.isEmpty()) {
            AdapterResponseInfo failed = adapters.get(adapters.size() - 1);
            setInfo(INFO_NETWORK, failed.getAdSourceName());
            setInfo(INFO_ADAPTER_CLASS, failed.getAdapterClassName());
            setInfo(INFO_LATENCY, formatMillis(failed.getLatencyMillis()));
            AdError adapterError = failed.getAdError();
            if (adapterError != null) {
                message.append("\n").append(orDash(failed.getAdSourceName()))
                        .append(": ").append(adapterError.getMessage());
            }
        }
        renderState(AdState.FAILED, message.toString());
    }

    protected void renderShowFailed(AdError error) {
        renderState(AdState.FAILED, String.format(Locale.US, "Failed to show: %s (code %d)",
                error.getMessage(), error.getCode()));
    }
}
