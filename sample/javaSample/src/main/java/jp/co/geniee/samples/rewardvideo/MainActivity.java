package jp.co.geniee.samples.rewardvideo;

import android.util.Log;
import android.widget.Switch;

import jp.co.geniee.gnadsdk.inspector.GNSAdInspectorError;
import jp.co.geniee.gnadsdk.rewardvideo.GNSRewardVideoAd;
import jp.co.geniee.gnadsdk.rewardvideo.GNSRewardVideoAdListener;
import jp.co.geniee.gnadsdk.rewardvideo.GNSVideoRewardData;
import jp.co.geniee.gnadsdk.rewardvideo.GNSVideoRewardException;
import jp.co.geniee.samples.SharedPreferenceManager;
import jp.co.geniee.samples.common.AdState;
import jp.co.geniee.samples.common.BaseAdActivity;

public class MainActivity extends BaseAdActivity {
    private static final String TAG = "[GNS]RewardVideo";

    private GNSRewardVideoAd mReward;
    private GNSVideoRewardData mRewardData;
    private Switch switchRTB;
    private Switch switchCustomAdNetwork;

    @Override
    protected String getFormatName() {
        return "Reward";
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
        return SharedPreferenceManager.REWARDED_VIDEO_AD_ZONE_ID;
    }

    @Override
    protected String[] getInfoLabels() {
        return new String[]{INFO_NETWORK, INFO_AD_UNIT, INFO_LATENCY, INFO_REWARD};
    }

    @Override
    protected void onCreateOptions() {
        switchRTB = addSwitch("Use RTB", SharedPreferenceManager.SWITCH_REWARD_RTB);
        switchCustomAdNetwork = addSwitch("Custom ad network", SharedPreferenceManager.SWITCH_REWARD_CUSTOM);
    }

    @Override
    protected String getExtraActionLabel() {
        return "Open Ad Inspector";
    }

    @Override
    protected void onExtraActionClicked() {
        GNSRewardVideoAd ad = getOrCreateAd(getAdUnitId());
        ad.setCustomAdNetworkEnabled(switchCustomAdNetwork.isChecked());
        ad.openAdInspector(this, (GNSAdInspectorError error) -> {
            if (error != null) {
                Log.d(TAG, "Ad Inspector error: " + error.getMessage());
            }
        });
    }

    @Override
    protected void loadAd(String zoneId) {
        GNSRewardVideoAd ad = getOrCreateAd(zoneId);
        ad.setZoneId(zoneId);
        ad.setCustomAdNetworkEnabled(switchCustomAdNetwork.isChecked());
        ad.loadRequest(switchRTB.isChecked());
    }

    @Override
    protected void showAd() {
        if (mReward != null && mReward.canShow()) {
            mRewardData = null;
            mReward.show();
        } else {
            renderState(AdState.FAILED, "Ad expired. Load again.");
        }
    }

    @Override
    protected void destroyAd() {
        // The SDK releases the ad itself when the host activity is destroyed.
        mReward = null;
    }

    private GNSRewardVideoAd getOrCreateAd(String zoneId) {
        if (mReward == null) {
            mReward = new GNSRewardVideoAd(zoneId, this);
            mReward.setRewardVideoAdListener(mListener);
        }
        return mReward;
    }

    private final GNSRewardVideoAdListener mListener = new GNSRewardVideoAdListener() {
        @Override
        public void rewardVideoAdDidReceiveAd(String adNetworkName) {
            setInfo(INFO_NETWORK, adNetworkName);
            setInfo(INFO_AD_UNIT, getAdUnitId());
            setInfo(INFO_LATENCY, getElapsedSinceLoad());
            renderState(AdState.LOADED, "Ready to show.");
        }

        @Override
        public void rewardVideoAdDidStartPlaying(GNSVideoRewardData data) {
            renderState(AdState.SHOWING, "Showing ad from " + data.adName + ".");
        }

        @Override
        public void didRewardUserWithReward(GNSVideoRewardData data) {
            mRewardData = data;
            setInfo(INFO_REWARD, data.amount + " " + data.type);
        }

        @Override
        public void rewardVideoAdDidClose(GNSVideoRewardData data) {
            String rewardMessage = mRewardData != null
                    ? "Rewarded: " + mRewardData.amount + " " + mRewardData.type + "."
                    : "No reward.";
            renderState(AdState.CLOSED, rewardMessage);
        }

        @Override
        public void didFailToLoadWithError(GNSVideoRewardException e) {
            setInfo(INFO_NETWORK, e.getAdnetworkName());
            setInfo(INFO_LATENCY, getElapsedSinceLoad());
            renderState(AdState.FAILED, e.getMessage() + " (code " + e.getCode() + ")");
        }
    };
}
