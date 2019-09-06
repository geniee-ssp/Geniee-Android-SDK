package jp.co.geniee.samples.vast;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import jp.co.geniee.samples.R;
import jp.co.geniee.samples.SharedPreferenceManager;
import jp.co.geniee.gnadsdk.video.GNAdVideo;

public class VastDemoActivity extends AppCompatActivity implements GNAdVideo.GNAdVideoListener, View.OnClickListener {

    private static final String TAG = "[GNS]VastDemoActivity";

    private GNAdVideo videoAd;
    private EditText edtZoneId;
    private Button loadAdButton;
    private Button showAdButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vast_demo);
        Log.d(TAG, "onCreate");
        edtZoneId = findViewById(R.id.edtZoneId);
        edtZoneId.setText(SharedPreferenceManager.getInstance(this).getString(SharedPreferenceManager.VAST_AD_ZONE_ID));

        // Sample button to load the Ad
        loadAdButton = findViewById(R.id.load_ad_button);
        loadAdButton.setOnClickListener(this);
        showAdButton = findViewById(R.id.show_ad_button);
        showAdButton.setOnClickListener(this);
        showAdButton.setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        if (view == loadAdButton) {
            try {
                String zoneId = edtZoneId.getText().toString();
                SharedPreferenceManager.getInstance(this).putString(SharedPreferenceManager.VAST_AD_ZONE_ID, zoneId);

                // Initializes a GNAdVideo
                videoAd = new GNAdVideo(this, Integer.parseInt(zoneId));
                // The alternative interstitial ad will be shown when no video ad.
                // videoAd.setAlternativeInterstitialAppID(YOUR_SSP_APP_ID_FOR_INTERSTITIAL);
                videoAd.setListener(this);
                //videoAd.setAutoCloseMode(false);            // Optional mode to automatically close after playing a video ad. Default: true
                //videoAd.setShowRate(100);                   // (Optional) Ad display frequency. (percentage)：Set the number between 0-100 (%). Default: 100
                //videoAd.setShowLimit(0);                    // (Optional) The maximum ad display numbers per app use. : Set the number 0 or higher. Default: 0 (No limited)
                //videoAd.setShowReset(0);                    // (Optional) The maximum reset fraction number of ad display per app
                //videoAd.setLogPriority(GNAdLogger.INFO);    // (Optional) log level
                //videoAd.setGeoLocationEnable(true);         // (Optional) location optimization. Default: false

                // Load GNAdVideo Ad
                videoAd.load(this);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Err " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (view == showAdButton) {
            showAdButton.setEnabled(false);
            // Show GNAdVideo Ad
            if (videoAd.isReady()) {
                videoAd.show(this);
            }
        }
    }

    //GNAdVideoListener listener override function
    // Sent when an video ad request succeeded.
    @Override
    public void onGNAdVideoReceiveSetting() {
        showAdButton.setEnabled(true);
        Toast.makeText(this, "onGNAdVideoReceiveSetting", Toast.LENGTH_SHORT).show();
    }

    // GNAdVideoListener listener override function
    // Sent when an video ad request failed.
    // (Network Connection Unavailable, Frequency capping, Out of ad stock)
    @Override
    public void onGNAdVideoFailedToReceiveSetting() {
        showAdButton.setEnabled(false);
        Toast.makeText(this, "onGNAdVideoFailedToReceiveSetting", Toast.LENGTH_SHORT).show();
    }

    //GNAdVideoListener listener override function
    // Sent just after closing ad because the user clicked skip button in video ad or
    // close button in alternative interstitial ad.
    @Override
    public void onGNAdVideoClose() {
        Toast.makeText(this, "onGNAdVideoClose", Toast.LENGTH_SHORT).show();
    }

    //GNAdVideoListener listener override function
    // Sent just after closing alternative interstitial ad because the
    // user clicked the button configured through server-side.
    @Override
    public void onGNAdVideoButtonClick(int nButtonIndex) {
        Toast.makeText(this, "onGNAdVideoButtonClick:" + nButtonIndex, Toast.LENGTH_SHORT).show();
    }
}
