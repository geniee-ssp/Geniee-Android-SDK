package jp.co.geniee.samples.googlemediation;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;

import jp.co.geniee.samples.SharedPreferenceManager;
import jp.co.geniee.samples.common.AdState;

public class FullscreenInterstitialActivity extends BaseGoogleAdActivity {
    private AdManagerInterstitialAd mInterstitialAd;

    @Override
    protected String getFormatName() {
        return "Interstitial";
    }

    @Override
    protected String getDefaultAdUnitId() {
        return "/424536528/1540247_In-app_test_GAM_Android_interstitial_com.geniee.get_the_gold";
    }

    @Override
    protected String getPreferenceKeySuffix() {
        return SharedPreferenceManager.INTERSTITIAL_AD_ZONE_ID;
    }

    @Override
    protected void loadAd(String adUnitId) {
        mInterstitialAd = null;
        //When debugging, set the test device with RequestConfiguration.Builder#setTestDeviceIds.
        //Please do not forget to delete this setting when release.
        AdManagerInterstitialAd.load(this, adUnitId, new AdManagerAdRequest.Builder().build(),
                new AdManagerInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.setFullScreenContentCallback(mFullScreenContentCallback);
                        renderLoaded(interstitialAd.getResponseInfo());
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        renderFailed(loadAdError);
                    }
                });
    }

    @Override
    protected void showAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        }
    }

    @Override
    protected void destroyAd() {
        mInterstitialAd = null;
    }

    private final FullScreenContentCallback mFullScreenContentCallback = new FullScreenContentCallback() {
        @Override
        public void onAdShowedFullScreenContent() {
            renderState(AdState.SHOWING, "Showing ad.");
        }

        @Override
        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
            mInterstitialAd = null;
            renderShowFailed(adError);
        }

        @Override
        public void onAdDismissedFullScreenContent() {
            mInterstitialAd = null;
            renderState(AdState.CLOSED, "Ad closed.");
        }
    };
}
