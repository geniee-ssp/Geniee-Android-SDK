package jp.co.geniee.samples.fullscreeninterstitial;

import android.util.Log;
import android.widget.Switch;

import jp.co.geniee.gnadsdk.common.GNSException;
import jp.co.geniee.gnadsdk.fullscreeninterstitial.GNSFullscreenInterstitialAd;
import jp.co.geniee.gnadsdk.fullscreeninterstitial.GNSFullscreenInterstitialAdListener;
import jp.co.geniee.gnadsdk.inspector.GNSAdInspectorError;
import jp.co.geniee.samples.SharedPreferenceManager;
import jp.co.geniee.samples.common.AdState;
import jp.co.geniee.samples.common.BaseAdActivity;

public class MainActivity extends BaseAdActivity {
    private static final String TAG = "[GNS]FullscreenInterstitial";

    private GNSFullscreenInterstitialAd mFullscreenInterstitial;
    private Switch switchCustomAdNetwork;

    @Override
    protected String getFormatName() {
        return "Interstitial";
    }

    @Override
    protected String getProviderName() {
        return "Geniee SDK";
    }

    @Override
    protected String getAdUnitLabel() {
        return "Zone ID";
    }

    @Override
    protected String getDefaultAdUnitId() {
        return "";
    }

    @Override
    protected String getAdUnitPreferenceKey() {
        return SharedPreferenceManager.INTERSTITIAL_AD_ZONE_ID;
    }

    @Override
    protected String[] getInfoLabels() {
        return new String[]{INFO_NETWORK, INFO_AD_UNIT, INFO_LATENCY};
    }

    @Override
    protected void onCreateOptions() {
        switchCustomAdNetwork = addSwitch("Custom ad network", SharedPreferenceManager.SWITCH_INTERSTITIAL_CUSTOM);
    }

    @Override
    protected String getExtraActionLabel() {
        return "Open Ad Inspector";
    }

    @Override
    protected void onExtraActionClicked() {
        GNSFullscreenInterstitialAd ad = getOrCreateAd(getAdUnitId());
        ad.setCustomAdNetworkEnabled(switchCustomAdNetwork.isChecked());
        ad.openAdInspector(this, (GNSAdInspectorError error) -> {
            if (error != null) {
                Log.d(TAG, "Ad Inspector error: " + error.getMessage());
            }
        });
    }

    @Override
    protected void loadAd(String zoneId) {
        GNSFullscreenInterstitialAd ad = getOrCreateAd(zoneId);
        ad.setZoneId(zoneId);
        ad.setCustomAdNetworkEnabled(switchCustomAdNetwork.isChecked());
        ad.loadRequest();
    }

    @Override
    protected void showAd() {
        if (mFullscreenInterstitial != null && mFullscreenInterstitial.canShow()) {
            mFullscreenInterstitial.show();
        } else {
            renderState(AdState.FAILED, "Ad expired. Load again.");
        }
    }

    @Override
    protected void destroyAd() {
        // The SDK releases the ad itself when the host activity is destroyed.
        mFullscreenInterstitial = null;
    }

    private GNSFullscreenInterstitialAd getOrCreateAd(String zoneId) {
        if (mFullscreenInterstitial == null) {
            mFullscreenInterstitial = new GNSFullscreenInterstitialAd(zoneId, this);
            mFullscreenInterstitial.setFullscreenInterstitialAdListener(mListener);
        }
        return mFullscreenInterstitial;
    }

    private final GNSFullscreenInterstitialAdListener mListener = new GNSFullscreenInterstitialAdListener() {
        @Override
        public void fullscreenInterstitialAdDidReceiveAd(String adNetworkName) {
            setInfo(INFO_NETWORK, adNetworkName);
            setInfo(INFO_AD_UNIT, getAdUnitId());
            setInfo(INFO_LATENCY, getElapsedSinceLoad());
            renderState(AdState.LOADED, "Ready to show.");
        }

        @Override
        public void didFailToLoadWithError(GNSException e) {
            setInfo(INFO_NETWORK, e.getAdnetworkName());
            setInfo(INFO_LATENCY, getElapsedSinceLoad());
            renderState(AdState.FAILED, e.getMessage() + " (code " + e.getCode() + ")");
        }

        @Override
        public void fullscreenInterstitialAdWillPresentScreen(String adName) {
            renderState(AdState.SHOWING, "Showing ad from " + adName + ".");
        }

        @Override
        public void fullscreenInterstitialAdDidClose(String adName) {
            renderState(AdState.CLOSED, "Ad closed.");
        }
    };
}
