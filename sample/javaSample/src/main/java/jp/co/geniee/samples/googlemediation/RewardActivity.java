package jp.co.geniee.samples.googlemediation;

import androidx.annotation.NonNull;

import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback;
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest;
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardItem;
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardedAd;
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardedAdEventCallback;

import jp.co.geniee.samples.common.SharedPreferenceManager;
import jp.co.geniee.samples.common.AdState;

public class RewardActivity extends BaseGoogleAdActivity {
    private volatile RewardedAd mRewardedAd;
    private volatile RewardItem mRewardItem;

    @Override
    protected String getFormatName() {
        return "Reward";
    }

    @Override
    protected String getDefaultAdUnitId() {
        // AdMob rewarded ad unit whose mediation group has Geniee as a custom event.
        return "ca-app-pub-2963406282877816/4959490587";
    }

    @Override
    protected String getPreferenceKeySuffix() {
        return SharedPreferenceManager.REWARDED_VIDEO_AD_ZONE_ID;
    }

    @Override
    protected String[] getInfoLabels() {
        String[] labels = super.getInfoLabels();
        String[] withReward = java.util.Arrays.copyOf(labels, labels.length + 1);
        withReward[labels.length] = INFO_REWARD;
        return withReward;
    }

    @Override
    protected void loadAd(String adUnitId) {
        mRewardedAd = null;
        RewardedAd.load(new AdRequest.Builder(adUnitId).build(), new AdLoadCallback<RewardedAd>() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                rewardedAd.setAdEventCallback(mAdEventCallback);
                mRewardedAd = rewardedAd;
                renderLoaded(rewardedAd.getResponseInfo());
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                renderFailed(loadAdError);
            }
        });
    }

    @Override
    protected void showAd() {
        if (mRewardedAd != null) {
            mRewardItem = null;
            mRewardedAd.show(this, rewardItem -> {
                mRewardItem = rewardItem;
                runOnUiThread(() -> setInfo(INFO_REWARD, rewardItem.getAmount() + " " + rewardItem.getType()));
            });
        }
    }

    @Override
    protected void destroyAd() {
        mRewardedAd = null;
    }

    private final RewardedAdEventCallback mAdEventCallback = new RewardedAdEventCallback() {
        @Override
        public void onAdShowedFullScreenContent() {
            renderStateOnUiThread(AdState.SHOWING, "Showing ad.");
        }

        @Override
        public void onAdFailedToShowFullScreenContent(@NonNull FullScreenContentError error) {
            mRewardedAd = null;
            renderShowFailed(error);
        }

        @Override
        public void onAdDismissedFullScreenContent() {
            mRewardedAd = null;
            RewardItem rewardItem = mRewardItem;
            String rewardMessage = rewardItem != null
                    ? "Rewarded: " + rewardItem.getAmount() + " " + rewardItem.getType() + "."
                    : "No reward.";
            renderStateOnUiThread(AdState.CLOSED, rewardMessage);
        }
    };
}
