package jp.co.geniee.samples.googlemediation;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import jp.co.geniee.samples.SharedPreferenceManager;
import jp.co.geniee.samples.common.AdState;

public class RewardActivity extends BaseGoogleAdActivity {
    private RewardedAd mRewardedAd;
    private RewardItem mRewardItem;

    @Override
    protected String getFormatName() {
        return "Reward";
    }

    @Override
    protected String getDefaultAdUnitId() {
        return "/21775744923/example/rewarded";
    }

    @Override
    protected String getPreferenceKeySuffix() {
        return SharedPreferenceManager.REWARDED_VIDEO_AD_ZONE_ID;
    }

    @Override
    protected String[] getInfoLabels() {
        return new String[]{INFO_NETWORK, INFO_ADAPTER_CLASS, INFO_AD_UNIT, INFO_LATENCY, INFO_REWARD};
    }

    @Override
    protected void loadAd(String adUnitId) {
        mRewardedAd = null;
        RewardedAd.load(this, adUnitId, new AdRequest.Builder().build(), new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                mRewardedAd = rewardedAd;
                mRewardedAd.setFullScreenContentCallback(mFullScreenContentCallback);
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
                setInfo(INFO_REWARD, rewardItem.getAmount() + " " + rewardItem.getType());
            });
        }
    }

    @Override
    protected void destroyAd() {
        mRewardedAd = null;
    }

    private final FullScreenContentCallback mFullScreenContentCallback = new FullScreenContentCallback() {
        @Override
        public void onAdShowedFullScreenContent() {
            renderState(AdState.SHOWING, "Showing ad.");
        }

        @Override
        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
            mRewardedAd = null;
            renderShowFailed(adError);
        }

        @Override
        public void onAdDismissedFullScreenContent() {
            mRewardedAd = null;
            String rewardMessage = mRewardItem != null
                    ? "Rewarded: " + mRewardItem.getAmount() + " " + mRewardItem.getType() + "."
                    : "No reward.";
            renderState(AdState.CLOSED, rewardMessage);
        }
    };
}
