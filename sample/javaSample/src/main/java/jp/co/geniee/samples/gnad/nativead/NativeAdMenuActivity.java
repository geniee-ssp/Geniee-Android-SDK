package jp.co.geniee.samples.gnad.nativead;

import android.content.Intent;

import jp.co.geniee.samples.BaseMenuActivity;
import jp.co.geniee.samples.MenuItem;

public class NativeAdMenuActivity extends BaseMenuActivity {

    @Override
    protected MenuItem[] getListViewContents() {
        MenuItem[] menuItem = {
                new MenuItem("Native Ad", "Native advertising is the use of paid ads that match the look, feel and function of the media format in which they appear", new Intent(this, NativeDemoActivity.class)),
                new MenuItem("Simple Native Video Ad", "", new Intent(this, NativeSimpleVideoDemoActivity.class)),
                new MenuItem("Native Video Ad", "", new Intent(this, NativeVideoDemoActivity.class))
        };
        return menuItem;
    }

}
