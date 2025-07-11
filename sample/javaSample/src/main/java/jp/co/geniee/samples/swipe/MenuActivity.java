package jp.co.geniee.samples.swipe;

import android.content.Intent;

import jp.co.geniee.samples.BaseMenuActivity;
import jp.co.geniee.samples.MenuItem;

public class MenuActivity extends BaseMenuActivity {
    @Override
    protected MenuItem[] getListViewContents() {
        MenuItem[] menuItem = {
                new MenuItem("In View", "Swipe from right to left", new Intent(this, jp.co.geniee.samples.swipe.inview.MainActivity.class)),
                new MenuItem("Ad Mob", "Swipe from left to right", new Intent(this, jp.co.geniee.samples.swipe.admob.LoadActivity.class)),
                new MenuItem("Full Size", "Swipe from left to right", new Intent(this, jp.co.geniee.samples.swipe.fullsize.MainActivity.class))
        };

        return menuItem;
    }
}
