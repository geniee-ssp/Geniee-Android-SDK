package jp.co.geniee.samples.scrollbanner;

import android.content.Intent;

import jp.co.geniee.samples.BaseMenuActivity;
import jp.co.geniee.samples.MenuItem;

public class MenuActivity extends BaseMenuActivity {
    @Override
    protected MenuItem[] getListViewContents() {
        MenuItem[] menuItem = {
                new MenuItem("In View", "", new Intent(this, jp.co.geniee.samples.scrollbanner.inview.GNAdSampleScrollBanner.class)),
                new MenuItem("Ad Mob", "", new Intent(this, jp.co.geniee.samples.scrollbanner.admob.LoadActivity.class))
        };

        return menuItem;
    }
}
