package jp.co.geniee.samples.maxmediation;

import android.widget.FrameLayout;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;

public class BannerActivity extends BaseMaxAdActivity implements MaxAdViewAdListener {
    private MaxAdView adView;

    @Override
    protected String getFormatName() {
        return "Banner";
    }

    @Override
    protected String getDefaultAdUnitId() {
        return MaxMediationHelper.BANNER_AD_UNIT_ID;
    }

    @Override
    protected boolean isFullscreen() {
        return false;
    }

    @Override
    protected void loadAd(String adUnitId) {
        destroyAd();
        adView = new MaxAdView(adUnitId, this);
        adView.setListener(this);
        FrameLayout container = getAdSlot();
        container.addView(adView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        adView.loadAd();
    }

    @Override
    protected void destroyAd() {
        if (adView != null) {
            getAdSlot().removeView(adView);
            adView.destroy();
            adView = null;
        }
    }

    // region MaxAdViewAdListener

    @Override
    public void onAdLoaded(MaxAd ad) {
        // Also called on every auto-refresh, so the status always shows the current winner.
        renderLoaded(ad);
    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        renderFailed(error);
    }

    @Override
    public void onAdDisplayed(MaxAd ad) {
    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        renderFailed(error);
    }

    @Override
    public void onAdHidden(MaxAd ad) {
    }

    @Override
    public void onAdClicked(MaxAd ad) {
    }

    @Override
    public void onAdExpanded(MaxAd ad) {
    }

    @Override
    public void onAdCollapsed(MaxAd ad) {
    }

    // endregion
}
