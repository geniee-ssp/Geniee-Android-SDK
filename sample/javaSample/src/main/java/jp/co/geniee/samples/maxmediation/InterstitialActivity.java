package jp.co.geniee.samples.maxmediation;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;

import jp.co.geniee.samples.common.AdState;

public class InterstitialActivity extends BaseMaxAdActivity implements MaxAdListener {
    private MaxInterstitialAd interstitialAd;
    private String loadedAdUnitId;

    @Override
    protected String getFormatName() {
        return "Interstitial";
    }

    @Override
    protected String getDefaultAdUnitId() {
        return MaxMediationHelper.INTERSTITIAL_AD_UNIT_ID;
    }

    @Override
    protected void loadAd(String adUnitId) {
        // MaxInterstitialAd is bound to one ad unit; recreate it when the input changes.
        if (interstitialAd == null || !adUnitId.equals(loadedAdUnitId)) {
            destroyAd();
            interstitialAd = new MaxInterstitialAd(adUnitId, this);
            interstitialAd.setListener(this);
            loadedAdUnitId = adUnitId;
        }
        interstitialAd.loadAd();
    }

    @Override
    protected void showAd() {
        if (interstitialAd != null && interstitialAd.isReady()) {
            interstitialAd.showAd(this);
        }
    }

    @Override
    protected void destroyAd() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
            interstitialAd = null;
        }
    }

    // region MaxAdListener

    @Override
    public void onAdLoaded(MaxAd ad) {
        renderLoaded(ad);
    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        renderFailed(error);
    }

    @Override
    public void onAdDisplayed(MaxAd ad) {
        renderState(AdState.SHOWING, "Showing ad from " + ad.getNetworkName() + ".");
    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        renderFailed(error);
    }

    @Override
    public void onAdHidden(MaxAd ad) {
        renderState(AdState.CLOSED, "Ad closed.");
    }

    @Override
    public void onAdClicked(MaxAd ad) {
    }

    // endregion
}
