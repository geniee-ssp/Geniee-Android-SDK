package jp.co.geniee.samples.geniee;

import android.content.Intent;

import jp.co.geniee.samples.common.BaseMenuActivity;
import jp.co.geniee.samples.common.MenuItem;
import jp.co.geniee.samples.geniee.banner.SingleBannerDemoActivity;

public class MenuActivity extends BaseMenuActivity {
    @Override
    protected MenuItem[] getListViewContents() {
        // Native, Multiple/XML banners, Interstitial (deprecated) and Video Ad (deprecated) are
        // kept in the project but no longer listed here.
        MenuItem[] menuItem = {
                new MenuItem("Banner", "", new Intent(this, SingleBannerDemoActivity.class)),
                new MenuItem("Interstitial", "", new Intent(this, jp.co.geniee.samples.geniee.interstitial.FullscreenInterstitialActivity.class)),
                new MenuItem("Reward", "", new Intent(this, jp.co.geniee.samples.geniee.reward.RewardVideoActivity.class))
        };

        return menuItem;
    }
}
