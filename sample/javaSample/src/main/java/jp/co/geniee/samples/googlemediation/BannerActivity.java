package jp.co.geniee.samples.googlemediation;

import jp.co.geniee.samples.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import com.google.android.gms.ads.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class BannerActivity extends AppCompatActivity {
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googlemediation_banner);

        // Create the adView.
        adView = new AdView(this);
        // Set YOUR_ADMOB_OR_DFP_AD_UNIT_ID
        adView.setAdUnitId("ca-app-pub-3940256099942544/9214589741");
        //adView.setAdSize(AdSize.BANNER);
        adView.setAdSize(AdSize.MEDIUM_RECTANGLE);

        // Add the AdView to the view hierarchy. The view will have no size
        // until the ad is loaded. This code assumes you have a LinearLayout with
        // attribute android:id="@+id/mainLayout" in your main.xml.
        LinearLayout layout = (LinearLayout)findViewById(R.id.mainLayout);
        layout.addView(adView);

        List<String> testDeviceIds = Arrays.asList("YOUR_DEVICE_ID");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);

        // Start loading the ad in the background.
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d("LongUni", "onAdLoaded: ");
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });
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
    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
