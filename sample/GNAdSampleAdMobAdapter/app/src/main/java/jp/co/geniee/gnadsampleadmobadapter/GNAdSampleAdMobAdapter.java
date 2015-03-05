package jp.co.geniee.gnadsampleadmobadapter;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import com.google.android.gms.ads.*;


public class GNAdSampleAdMobAdapter extends ActionBarActivity {
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gnad_sample_admob_adapter);

        // Create the adView.
        adView = new AdView(this);
        // Set MY_ADMOB_AD_UNIT_ID
        adView.setAdUnitId("MY_ADMOB_AD_UNIT_ID");
        adView.setAdSize(AdSize.BANNER);

        // Add the AdView to the view hierarchy. The view will have no size
        // until the ad is loaded. This code assumes you have a LinearLayout with
        // attribute android:id="@+id/mainLayout" in your main.xml.
        LinearLayout layout = (LinearLayout)findViewById(R.id.mainLayout);
        layout.addView(adView);

        // Start loading the ad in the background.
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gnad_sample_ad_mob_adapter, menu);
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

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }
}
