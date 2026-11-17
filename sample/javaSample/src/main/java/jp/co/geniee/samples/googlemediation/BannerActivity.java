package jp.co.geniee.samples.googlemediation;

import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.android.libraries.ads.mobile.sdk.banner.AdSize;
import com.google.android.libraries.ads.mobile.sdk.banner.AdView;
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd;
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest;
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;

public class BannerActivity extends BaseGoogleAdActivity {
    private static final AdSize AD_SIZE = AdSize.MEDIUM_RECTANGLE;

    private AdView adView;

    @Override
    protected String getFormatName() {
        return "Banner";
    }

    @Override
    protected String getDefaultAdUnitId() {
        // AdMob banner ad unit whose mediation group has Geniee as a custom event.
        return "ca-app-pub-2963406282877816/6675896935";
    }

    @Override
    protected String getPreferenceKeySuffix() {
        return "BANNER_AD_UNIT_ID";
    }

    @Override
    protected boolean isFullscreen() {
        return false;
    }

    @Override
    protected void onCreateOptions() {
        setAdSlotSize(AD_SIZE.getWidth(), AD_SIZE.getHeight());
    }

    @Override
    protected void loadAd(String adUnitId) {
        destroyAd();
        adView = new AdView(this);
        getAdSlot().addView(adView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        adView.loadAd(new BannerAdRequest.Builder(adUnitId, AD_SIZE).build(), new AdLoadCallback<BannerAd>() {
            @Override
            public void onAdLoaded(@NonNull BannerAd bannerAd) {
                renderLoaded(bannerAd.getResponseInfo());
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                renderFailed(loadAdError);
            }
        });
    }

    @Override
    protected void destroyAd() {
        if (adView != null) {
            getAdSlot().removeView(adView);
            adView.destroy();
            adView = null;
        }
    }
}
