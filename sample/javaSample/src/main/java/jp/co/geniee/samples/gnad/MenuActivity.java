package jp.co.geniee.samples.gnad;

import android.content.Intent;

import jp.co.geniee.samples.BaseMenuActivity;
import jp.co.geniee.samples.MenuItem;
import jp.co.geniee.samples.gnad.banner.SingleBannerDemoActivity;

public class MenuActivity extends BaseMenuActivity {
    @Override
    protected MenuItem[] getListViewContents() {
        // Native, Multiple/XML banners, Interstitial (deprecated) and Video Ad (deprecated) are
        // kept in the project but no longer listed here.
        MenuItem[] menuItem = {
                new MenuItem("Banner", "", new Intent(this, SingleBannerDemoActivity.class)),
                new MenuItem("Interstitial", "", new Intent(this, jp.co.geniee.samples.fullscreeninterstitial.MainActivity.class)),
                new MenuItem("Reward", "", new Intent(this, jp.co.geniee.samples.rewardvideo.MainActivity.class))
        };

        return menuItem;
    }
}
