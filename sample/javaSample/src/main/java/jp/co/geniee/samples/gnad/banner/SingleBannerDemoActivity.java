package jp.co.geniee.samples.gnad.banner;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Switch;

import jp.co.geniee.gnadsdk.banner.GNAdEventListener;
import jp.co.geniee.gnadsdk.banner.GNAdSize;
import jp.co.geniee.gnadsdk.banner.GNAdView;
import jp.co.geniee.gnadsdk.inspector.GNSAdInspectorError;
import jp.co.geniee.samples.R;
import jp.co.geniee.samples.SharedPreferenceManager;
import jp.co.geniee.samples.common.AdState;
import jp.co.geniee.samples.common.BaseAdActivity;

public class SingleBannerDemoActivity extends BaseAdActivity {
    private static final String TAG = "[GNS]SingleBannerDemo";

    private GNAdView adView;
    private Spinner spinnerBannerSizes;
    private Switch switchMediation;
    private Switch switchCustomAdNetwork;

    @Override
    protected String getFormatName() {
        return "Banner";
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
        return SharedPreferenceManager.SINGLE_BANNER_ZONE_ID;
    }

    @Override
    protected boolean isFullscreen() {
        return false;
    }

    @Override
    protected String[] getInfoLabels() {
        return new String[]{INFO_NETWORK, INFO_AD_UNIT, INFO_SIZE, INFO_LATENCY};
    }

    @Override
    protected void onCreateOptions() {
        spinnerBannerSizes = addSpinner("Banner size", getResources().getStringArray(R.array.banner_size_arrays));
        spinnerBannerSizes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GNAdSize size = getSelectedAdSize();
                setAdSlotSize(size.getWidth(), size.getHeight());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        switchMediation = addSwitch("Use mediation", SharedPreferenceManager.SWITCH_BANNER_MEDIATION);
        switchCustomAdNetwork = addSwitch("Custom ad network", SharedPreferenceManager.SWITCH_BANNER_CUSTOM);
    }

    @Override
    protected String getExtraActionLabel() {
        return "Open Ad Inspector";
    }

    @Override
    protected void onExtraActionClicked() {
        if (adView == null) {
            renderState(getState(), "Load a banner first.");
            return;
        }
        adView.setCustomAdNetworkEnabled(switchCustomAdNetwork.isChecked());
        adView.openAdInspector(this, (GNSAdInspectorError error) -> {
            if (error != null) {
                Log.d(TAG, "Ad Inspector error: " + error.getMessage());
            }
        });
    }

    @Override
    protected void loadAd(String zoneId) {
        destroyAd();

        GNAdSize adSize = getSelectedAdSize();
        adView = new GNAdView(this, adSize);
        adView.setListener(listener);
        getAdSlot().addView(adView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        setInfo(INFO_SIZE, adSize.getWidth() + " × " + adSize.getHeight());

        try {
            adView.setAppId(zoneId);
            adView.useMediation(switchMediation.isChecked());
            adView.setCustomAdNetworkEnabled(switchCustomAdNetwork.isChecked());
            adView.startAdLoop();
        } catch (Exception e) {
            renderState(AdState.FAILED, e.getLocalizedMessage());
        }
    }

    @Override
    protected void destroyAd() {
        if (adView != null) {
            adView.clearAdView();
            getAdSlot().removeView(adView);
            adView = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adView != null) {
            adView.stopAdLoop();
        }
    }

    private GNAdSize getSelectedAdSize() {
        switch (spinnerBannerSizes.getSelectedItem().toString()) {
            case "W320H48":
                return GNAdSize.W320H48;
            case "W300H250":
                return GNAdSize.W300H250;
            case "W728H90":
                return GNAdSize.W728H90;
            case "W468H60":
                return GNAdSize.W468H60;
            case "W120H600":
                return GNAdSize.W120H600;
            case "W320H100":
                return GNAdSize.W320H100;
            case "W57H57":
                return GNAdSize.W57H57;
            case "W76H76":
                return GNAdSize.W76H76;
            case "W480H32":
                return GNAdSize.W480H32;
            case "W768H66":
                return GNAdSize.W768H66;
            case "W1024H66":
                return GNAdSize.W1024H66;
            case "W320H50":
            default:
                return GNAdSize.W320H50;
        }
    }

    private final GNAdEventListener listener = new GNAdEventListener() {
        @Override
        public void onReceiveAd(GNAdView gnAdView) {
            // Also called on every refresh; latency is only meaningful for the first request.
            if (getState() == AdState.LOADING) {
                setInfo(INFO_LATENCY, getElapsedSinceLoad());
            }
            setInfo(INFO_NETWORK, gnAdView.getWinnerNetworkName());
            setInfo(INFO_AD_UNIT, getAdUnitId());
            renderState(AdState.LOADED, "Banner displayed.");
        }

        @Override
        public void onFaildToReceiveAd(GNAdView gnAdView) {
            setInfo(INFO_LATENCY, getElapsedSinceLoad());
            renderState(AdState.FAILED, "Failed to receive a banner ad.");
        }

        @Override
        public void onAdHidden(GNAdView gnAdView) {
        }

        @Override
        public void onStartExternalBrowser(GNAdView gnAdView) {
        }

        @Override
        public void onStartInternalBrowser(GNAdView gnAdView) {
        }

        @Override
        public void onTerminateInternalBrowser(GNAdView gnAdView) {
        }

        @Override
        public boolean onShouldStartInternalBrowserWithClick(String s) {
            return false;
        }
    };
}
