package jp.co.geniee.samples.googlemediation;

import android.content.Intent;

import jp.co.geniee.samples.BaseMenuActivity;
import jp.co.geniee.samples.MenuItem;

public class MenuActivity extends BaseMenuActivity {
    @Override
    protected MenuItem[] getListViewContents() {
        MenuItem[] menuItem = {
                new MenuItem("Banner", "", new Intent(this, jp.co.geniee.samples.googlemediation.BannerActivity.class)),
                new MenuItem("Fullscreen Interstitial", "", new Intent(this, jp.co.geniee.samples.googlemediation.FullscreenInterstitialActivity.class)),
                new MenuItem("Reward Video", "", new Intent(this, jp.co.geniee.samples.googlemediation.RewardActivity.class))
        };

        return menuItem;
    }
}
