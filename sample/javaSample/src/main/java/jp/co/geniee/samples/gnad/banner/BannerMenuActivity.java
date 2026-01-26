package jp.co.geniee.samples.gnad.banner;

import android.content.Intent;

import jp.co.geniee.samples.BaseMenuActivity;
import jp.co.geniee.samples.MenuItem;

public class BannerMenuActivity extends BaseMenuActivity {


    @Override
    protected MenuItem[] getListViewContents() {
        MenuItem[] menuItem = {
                new MenuItem("Single Banner", "Classic banner ads", new Intent(this, SingleBannerDemoActivity.class)),
                new MenuItem("Multiple Banners", "Multiple banner ads", new Intent(this, MultipleBannerDemoActivity.class))
        };

        return menuItem;
    }
}
