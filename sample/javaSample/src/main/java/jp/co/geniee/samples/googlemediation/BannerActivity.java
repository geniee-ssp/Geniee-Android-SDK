package jp.co.geniee.samples.googlemediation;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.Arrays;
import java.util.List;

import jp.co.geniee.samples.R;


public class BannerActivity extends AppCompatActivity {
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googlemediation_banner);

        // Create the adView.
        adView = new AdView(this);
        // This is the sample ID obtained from https://developers.google.com/admob/android/test-ads?demo_ad_units.
        // Set YOUR_ADMOB_OR_DFP_AD_UNIT_ID
        adView.setAdUnitId("ca-app-pub-3940256099942544/9214589741");
        //adView.setAdSize(AdSize.BANNER);
        adView.setAdSize(AdSize.MEDIUM_RECTANGLE);

        // Add the AdView to the view hierarchy. The view will have no size
        // until the ad is loaded. This code assumes you have a LinearLayout with
        // attribute android:id="@+id/mainLayout" in your main.xml.
        LinearLayout layout = (LinearLayout)findViewById(R.id.mainLayout);
        layout.addView(adView);

        //When debugging, set the test device in the following format.
        //Please do not forget to delete this setting when release.　
        /*
        List<String> testDeviceIds = Arrays.asList("YOUR_TEST_DEVICE_ID");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);
         */

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
