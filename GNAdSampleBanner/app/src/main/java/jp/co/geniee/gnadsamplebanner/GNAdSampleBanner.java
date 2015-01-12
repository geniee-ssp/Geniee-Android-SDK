package jp.co.geniee.gnadsamplebanner;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import jp.co.geniee.gnadsdk.common.GNAdLogger;
import jp.co.geniee.gnadsdk.banner.GNAdView;
import jp.co.geniee.gnadsdk.banner.GNAdSize;
import jp.co.geniee.gnadsdk.banner.GNTouchType;


public class GNAdSampleBanner extends ActionBarActivity {
    private GNAdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gnad_sample_banner);
        Log.d("GNAdSampleBanner", "onCreate");

        // Initializes a GNAdView
        adView = new GNAdView(
                this,
                GNAdSize.W320H50,
                GNTouchType.TAP_AND_FLICK
        );
        adView.setAppId("YOUR_SSP_APP_ID");
        //adView.setLogPriority(GNAdLogger.INFO);
        //adView.setGeoLocationEnable(true);
        // Add AdView to view layer
        final LinearLayout layout = (LinearLayout)findViewById(R.id.AdviewLayout);
        layout.addView(adView, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GNAdSampleBanner", "onResume");
        if(adView != null){
            adView.startAdLoop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("GNAdSampleBanner", "onPause");
        if(adView != null){
            adView.stopAdLoop();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d("GNAdSampleBanner", "onDestroy");
        if (null != adView) {
            adView.clearAdView();
        }
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gnad_sample_banner, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
