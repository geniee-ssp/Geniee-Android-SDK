package jp.co.geniee.samples.googlemediation;

import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

public class BannerActivity extends BaseGoogleAdActivity {
    private static final AdSize AD_SIZE = AdSize.MEDIUM_RECTANGLE;

    private AdView adView;

    @Override
    protected String getFormatName() {
        return "Banner";
    }

    @Override
    protected String getDefaultAdUnitId() {
        return "ca-app-pub-3940256099942544/9214589741";
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
        adView.setAdUnitId(adUnitId);
        adView.setAdSize(AD_SIZE);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                renderLoaded(adView.getResponseInfo());
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                renderFailed(loadAdError);
            }
        });
        getAdSlot().addView(adView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        adView.loadAd(new AdRequest.Builder().build());
    }

    @Override
    protected void destroyAd() {
        if (adView != null) {
            getAdSlot().removeView(adView);
            adView.destroy();
            adView = null;
        }
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }
}
