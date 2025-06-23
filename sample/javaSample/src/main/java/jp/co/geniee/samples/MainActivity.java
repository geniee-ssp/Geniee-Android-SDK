package jp.co.geniee.samples;

import android.content.Intent;

public class MainActivity extends BaseMenuActivity {

    @Override
    protected MenuItem[] getListViewContents() {
        MenuItem[] menuItem = {
                new MenuItem("GNAd", "Banner,Native,Interstitial (deprecated),Video Ad (deprecated)", new Intent(this, jp.co.geniee.samples.gnad.MenuActivity.class)),
                new MenuItem("Fullscreen Interstitial", "", new Intent(this, jp.co.geniee.samples.fullscreeninterstitial.MainActivity.class)),
                new MenuItem("Reward Video", "", new Intent(this, jp.co.geniee.samples.rewardvideo.MainActivity.class)),
                new MenuItem("Google Mediation", "Banner,Fullscreen Interstitial,Reward Video", new Intent(this, jp.co.geniee.samples.googlemediation.MenuActivity.class))
        };

        return menuItem;
    }
}
