package jp.co.geniee.samples.gnadsamplenativead;

import android.app.Application;
import jp.co.geniee.gnadsdk.common.GNAdSDK;

public class MyApplication extends Application {
    GNAdSDK gnadsdk = null;
    @Override
    public void onCreate() {
        GNAdSDK gnadsdk = GNAdSDK.getInstatnce(getApplicationContext());
        gnadsdk.init();
    }
}
