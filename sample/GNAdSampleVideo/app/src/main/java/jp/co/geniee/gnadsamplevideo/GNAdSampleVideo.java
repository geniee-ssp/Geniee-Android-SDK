package jp.co.geniee.gnadsamplevideo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import jp.co.geniee.gnadsdk.video.GNAdVideo;
import jp.co.geniee.gnadsdk.video.GNAdVideo.GNAdVideoListener;
import jp.co.geniee.gnadsdk.common.GNAdLogger;

public class GNAdSampleVideo extends ActionBarActivity implements GNAdVideoListener, OnClickListener {
    private GNAdVideo videoAd = null;
    private Button loadAdButton;
    private Button showAdButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gnad_sample_video);
        Log.d("GNAdSampleInterstitial", "onCreate");

        // Sample button to load the Ad
        loadAdButton = (Button) findViewById(R.id.load_ad_button);
        loadAdButton.setOnClickListener(this);
        showAdButton = (Button) findViewById(R.id.show_ad_button);
        showAdButton.setOnClickListener(this);
        showAdButton.setEnabled(false);

        // Initializes a GNAdVideo
        videoAd = new GNAdVideo(this, YOUR_SSP_APP_ID_FOR_VIDEO);
        // The alternative interstitial ad will be shown when no video ad.
        videoAd.setAlternativeInterstitialAppID(YOUR_SSP_APP_ID_FOR_INTERSTITIAL);
        videoAd.setListener(this);
        //videoAd.setAutoCloseMode(false);            // Optional mode to automatically close after playing a video ad. Default: true
        //videoAd.setShowRate(100);                   // (Optional) Ad display frequency. (percentage)：Set the number between 0-100 (%). Default: 100
        //videoAd.setShowLimit(0);                    // (Optional) The maximum ad display numbers per app use. : Set the number 0 or higher. Default: 0 (No limited)
        //videoAd.setShowReset(0);                    // (Optional) The maximum reset fraction number of ad display per app
        //videoAd.setLogPriority(GNAdLogger.INFO);    // (Optional) log level
        //videoAd.setGeoLocationEnable(true);         // (Optional) location optimization. Default: false
    }

    @Override
    public void onClick(View view) {
        if (view == loadAdButton) {
            // Load GNAdVideo Ad
            videoAd.load(this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gnad_sample_video, menu);
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
