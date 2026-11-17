package jp.co.geniee.samples.geniee.banner;

import android.content.Intent;

import jp.co.geniee.samples.common.BaseMenuActivity;
import jp.co.geniee.samples.common.MenuItem;

public class BannerMenuActivity extends BaseMenuActivity {


    @Override
    protected MenuItem[] getListViewContents() {
        MenuItem[] menuItem = {
                new MenuItem("Single Banner", "Classic banner ads", new Intent(this, SingleBannerDemoActivity.class)),
                new MenuItem("Multiple Banners", "Multiple banner ads", new Intent(this, MultipleBannerDemoActivity.class)),
                new MenuItem("XML Banner", "Banner declared in XML layout", new Intent(this, XmlBannerDemoActivity.class))
        };

        return menuItem;
    }
}
