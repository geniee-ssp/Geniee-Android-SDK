package jp.co.geniee.samples.maxmediation;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;

import jp.co.geniee.samples.common.AdState;

public class RewardedActivity extends BaseMaxAdActivity implements MaxRewardedAdListener {
    private MaxRewardedAd rewardedAd;
    private MaxReward reward;

    @Override
    protected String getFormatName() {
        return "Rewarded";
    }

    @Override
    protected String getDefaultAdUnitId() {
        return MaxMediationHelper.REWARDED_AD_UNIT_ID;
    }

    @Override
    protected void loadAd(String adUnitId) {
        // MaxRewardedAd is a per-ad-unit singleton; detach from the previous one instead of destroying it.
        destroyAd();
        rewardedAd = MaxRewardedAd.getInstance(adUnitId, this);
        rewardedAd.setListener(this);
        rewardedAd.loadAd();
    }

    @Override
    protected void showAd() {
        if (rewardedAd != null && rewardedAd.isReady()) {
            reward = null;
            rewardedAd.showAd(this);
        }
    }

    @Override
    protected void destroyAd() {
        if (rewardedAd != null) {
            rewardedAd.setListener(null);
            rewardedAd = null;
        }
    }

    // region MaxRewardedAdListener

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
    public void onUserRewarded(MaxAd ad, MaxReward reward) {
        this.reward = reward;
    }

    @Override
    public void onAdHidden(MaxAd ad) {
        String rewardMessage = reward != null
                ? "Rewarded: " + reward.getAmount() + " " + reward.getLabel() + "."
                : "No reward.";
        renderState(AdState.CLOSED, rewardMessage);
    }

    @Override
    public void onAdClicked(MaxAd ad) {
    }

    // endregion
}
