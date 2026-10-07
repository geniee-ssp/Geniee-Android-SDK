package jp.co.geniee.samples.maxmediation;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdWaterfallInfo;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxMediatedNetworkInfo;
import com.applovin.mediation.MaxNetworkResponseInfo;

import java.util.Locale;

import jp.co.geniee.samples.common.AdState;
import jp.co.geniee.samples.common.BaseAdActivity;

/**
 * MAX flavour of the sample ad screen: initializes the AppLovin SDK before loading and maps
 * {@link MaxAd} / {@link MaxError} to the status card.
 */
public abstract class BaseMaxAdActivity extends BaseAdActivity {
    private static final String PREF_KEY_PREFIX = "maxmediation_ad_unit_";

    @Override
    protected String getProviderName() {
        return "AppLovin MAX · Geniee custom network";
    }

    @Override
    protected String getAdUnitPreferenceKey() {
        return PREF_KEY_PREFIX + getFormatName().toLowerCase(Locale.US);
    }

    @Override
    protected String[] getInfoLabels() {
        return new String[]{INFO_NETWORK, INFO_ADAPTER_CLASS, INFO_ADAPTER_VERSION, INFO_SDK_VERSION,
                INFO_PLACEMENT, INFO_LATENCY, INFO_REVENUE};
    }

    @Override
    protected String getExtraActionLabel() {
        return "Open Mediation Debugger";
    }

    @Override
    protected void onExtraActionClicked() {
        MaxMediationHelper.initialize(this, () -> MaxMediationHelper.showMediationDebugger(this));
    }

    @Override
    protected void requestLoad(String adUnitId) {
        MaxMediationHelper.initialize(this, () -> loadAd(adUnitId));
    }

    protected void renderLoaded(MaxAd ad) {
        setInfo(INFO_NETWORK, ad.getNetworkName());
        renderNetworkInfo(findResponse(ad.getWaterfall(), MaxNetworkResponseInfo.AdLoadState.AD_LOADED));
        setInfo(INFO_PLACEMENT, ad.getNetworkPlacement());
        setInfo(INFO_LATENCY, formatMillis(ad.getRequestLatencyMillis()));
        setInfo(INFO_REVENUE, ad.getRevenue() > 0 ? String.format(Locale.US, "$%.6f", ad.getRevenue()) : null);
        renderState(AdState.LOADED, isFullscreen() ? "Ready to show." : "Banner displayed.");
    }

    protected void renderFailed(MaxError error) {
        StringBuilder message = new StringBuilder()
                .append(error.getMessage())
                .append(" (code ").append(error.getCode()).append(")");
        if (error.getMediatedNetworkErrorCode() != 0) {
            message.append("\nNetwork error ").append(error.getMediatedNetworkErrorCode())
                    .append(": ").append(error.getMediatedNetworkErrorMessage());
        }

        // Show the network that failed last, so adapter problems (e.g. a wrong adapter class
        // name in the dashboard) are visible without reading logcat.
        MaxNetworkResponseInfo failed = findResponse(error.getWaterfall(), MaxNetworkResponseInfo.AdLoadState.FAILED_TO_LOAD);
        if (failed != null) {
            setInfo(INFO_NETWORK, failed.getMediatedNetwork().getName());
            setInfo(INFO_LATENCY, formatMillis(failed.getLatencyMillis()));
            if (failed.getError() != null) {
                message.append("\n").append(failed.getMediatedNetwork().getName())
                        .append(": ").append(failed.getError().getMessage());
            }
        }
        renderNetworkInfo(failed);
        renderState(AdState.FAILED, message.toString());
    }

    private void renderNetworkInfo(MaxNetworkResponseInfo response) {
        MaxMediatedNetworkInfo network = response != null ? response.getMediatedNetwork() : null;
        setInfo(INFO_ADAPTER_CLASS, network != null ? network.getAdapterClassName() : null);
        setInfo(INFO_ADAPTER_VERSION, network != null ? network.getAdapterVersion() : null);
        setInfo(INFO_SDK_VERSION, network != null ? network.getSdkVersion() : null);
    }

    /** Returns the last response in the waterfall with the given state, or null. */
    private static MaxNetworkResponseInfo findResponse(MaxAdWaterfallInfo waterfall, MaxNetworkResponseInfo.AdLoadState state) {
        if (waterfall == null) {
            return null;
        }
        MaxNetworkResponseInfo found = null;
        for (MaxNetworkResponseInfo response : waterfall.getNetworkResponses()) {
            if (response.getAdLoadState() == state) {
                found = response;
            }
        }
        return found;
    }
}
