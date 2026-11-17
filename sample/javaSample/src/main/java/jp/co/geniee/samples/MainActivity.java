package jp.co.geniee.samples;

import android.content.Intent;

import jp.co.geniee.samples.common.BaseMenuActivity;
import jp.co.geniee.samples.common.MenuItem;

public class MainActivity extends BaseMenuActivity {
    @Override
    protected MenuItem[] getListViewContents() {
        // Geniee SDK screens not listed in its menu (Native, Multiple/XML banner, Video Player,
        // VAST, legacy Interstitial) are kept under the geniee package.
        MenuItem[] menuItem = {
                new MenuItem("Geniee SDK", "Banner,Interstitial,Reward", new Intent(this, jp.co.geniee.samples.geniee.MenuActivity.class)),
                new MenuItem("AppLovin MAX Mediation", "Banner,Interstitial,Rewarded", new Intent(this, jp.co.geniee.samples.maxmediation.MenuActivity.class)),
                new MenuItem("Google Mediation", "Banner,Interstitial,Reward", new Intent(this, jp.co.geniee.samples.googlemediation.MenuActivity.class)),
        };

        return menuItem;
    }
}
