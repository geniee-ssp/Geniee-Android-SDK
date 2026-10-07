package jp.co.geniee.samples;

import android.content.Intent;

public class MainActivity extends BaseMenuActivity {
    @Override
    protected MenuItem[] getListViewContents() {
        // Swipe, Scroll Banner, Video Player and the deprecated GNAd formats are kept in the
        // project but no longer listed here.
        MenuItem[] menuItem = {
                new MenuItem("Geniee SDK", "Banner,Interstitial,Reward", new Intent(this, jp.co.geniee.samples.gnad.MenuActivity.class)),
                new MenuItem("AppLovin MAX Mediation", "Banner,Interstitial,Rewarded", new Intent(this, jp.co.geniee.samples.maxmediation.MenuActivity.class)),
                new MenuItem("Google Mediation", "Banner,Interstitial,Reward", new Intent(this, jp.co.geniee.samples.googlemediation.MenuActivity.class)),
        };

        return menuItem;
    }
}
