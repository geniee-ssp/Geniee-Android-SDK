package jp.co.geniee.samples.gnad;

import android.content.Intent;

import jp.co.geniee.samples.BaseMenuActivity;
import jp.co.geniee.samples.MenuItem;
import jp.co.geniee.samples.gnad.banner.BannerMenuActivity;
import jp.co.geniee.samples.gnad.interstitial.InterstitialDemoActivity;
import jp.co.geniee.samples.gnad.nativead.NativeAdMenuActivity;
import jp.co.geniee.samples.gnad.vast.VastDemoActivity;

public class MenuActivity extends BaseMenuActivity {
    @Override
    protected MenuItem[] getListViewContents() {
        MenuItem[] menuItem = {
                new MenuItem("Banner", "Banner ads", new Intent(this, BannerMenuActivity.class)),
                new MenuItem("Native Ad", "Native advertising is the use of paid ads that match the look, feel and function of the media format in which they appear", new Intent(this, NativeAdMenuActivity.class)),
                new MenuItem("Interstitial (deprecated)", "Interstitial ads are full-screen ads that cover the interface of their host app", new Intent(this, InterstitialDemoActivity.class)),
                new MenuItem("Video Ad (deprecated)", "", new Intent(this, VastDemoActivity.class))
        };

        return menuItem;
    }
}
