package jp.co.geniee.samples.googlemediation;

import androidx.annotation.NonNull;

import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback;
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest;
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd;
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback;

import jp.co.geniee.samples.common.SharedPreferenceManager;
import jp.co.geniee.samples.common.AdState;

public class FullscreenInterstitialActivity extends BaseGoogleAdActivity {
    private volatile InterstitialAd mInterstitialAd;

    @Override
    protected String getFormatName() {
        return "Interstitial";
    }

    @Override
    protected String getDefaultAdUnitId() {
        // AdMob interstitial ad unit whose mediation group has Geniee as a custom event.
        return "ca-app-pub-2963406282877816/9744400052";
    }

    @Override
    protected String getPreferenceKeySuffix() {
        return SharedPreferenceManager.INTERSTITIAL_AD_ZONE_ID;
    }

    @Override
    protected void loadAd(String adUnitId) {
        mInterstitialAd = null;
        //When debugging, set the test device with RequestConfiguration.Builder#setTestDeviceIds (see SampleApplication).
        //Please do not forget to delete this setting when release.
        InterstitialAd.load(new AdRequest.Builder(adUnitId).build(), new AdLoadCallback<InterstitialAd>() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                interstitialAd.setAdEventCallback(mAdEventCallback);
                mInterstitialAd = interstitialAd;
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

    private final InterstitialAdEventCallback mAdEventCallback = new InterstitialAdEventCallback() {
        @Override
        public void onAdShowedFullScreenContent() {
            renderStateOnUiThread(AdState.SHOWING, "Showing ad.");
        }

        @Override
        public void onAdFailedToShowFullScreenContent(@NonNull FullScreenContentError error) {
            mInterstitialAd = null;
            renderShowFailed(error);
        }

        @Override
        public void onAdDismissedFullScreenContent() {
            mInterstitialAd = null;
            renderStateOnUiThread(AdState.CLOSED, "Ad closed.");
        }
    };
}
