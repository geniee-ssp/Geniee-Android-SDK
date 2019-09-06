package jp.co.geniee.samples;

import android.content.Intent;

import jp.co.geniee.samples.banner.BannerMenuActivity;
import jp.co.geniee.samples.interstitial.InterstitialDemoActivity;
import jp.co.geniee.samples.nativead.NativeAdMenuActivity;
import jp.co.geniee.samples.vast.VastDemoActivity;

public class MainActivity extends BaseMenuActivity {

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
