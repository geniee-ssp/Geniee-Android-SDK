package jp.co.geniee.gnadsampleinterstitial;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import jp.co.geniee.gnadsdk.interstitial.GNInterstitial;
import jp.co.geniee.gnadsdk.interstitial.GNInterstitial.GNInterstitialListener;
import jp.co.geniee.gnadsdk.interstitial.GNInterstitial.GNInterstitialDialogListener;
import jp.co.geniee.gnadsdk.common.GNAdLogger;


public class GNAdSampleInterstitial extends ActionBarActivity implements GNInterstitialListener, GNInterstitialDialogListener, View.OnClickListener {
    private GNInterstitial interstitial = null;
    private Button loadAdButton;
    private Button showAdButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gnad_sample_interstitial);
        Log.d("GNAdSampleInterstitial", "onCreate");

        // Sample button to load the Ad
        loadAdButton = (Button) findViewById(R.id.load_ad_button);
        loadAdButton.setOnClickListener(this);
        showAdButton = (Button) findViewById(R.id.show_ad_button);
        showAdButton.setOnClickListener(this);
        showAdButton.setEnabled(false);

        // Initializes a GNInterstitial
        interstitial = new GNInterstitial(this, YOUR_SSP_APP_ID);
        interstitial.setListener(this);
        interstitial.setDialoglistener(this);
        //interstitial.setGeoLocationEnable(true);
        //interstitial.setLogPriority(GNAdLogger.INFO);
    }

    @Override
    public void onClick(View view) {
        if (view == loadAdButton) {
            // Load GNInterstitial Ad
            interstitial.load(this);
        } else if (view == showAdButton) {
            showAdButton.setEnabled(false);
            // Show GNInterstitial Ad
            if(interstitial.isReady()) {
                interstitial.show(this);
            }
        }
    }

    //GNInterstitialListener listener override function
    // Sent when an interstitial ad request succeeded
    @Override
    public void onReceiveSetting() {
        showAdButton.setEnabled(true);
        Toast.makeText(this, "onReceiveSetting", Toast.LENGTH_SHORT).show();
    }

    //GNInterstitialListener listener override function
    // Sent when an interstitial ad request failed
    // (Network Connection Unavailable, Frequency capping, Out of ad stock)
    @Override
    public void onFailedToReceiveSetting() {
        showAdButton.setEnabled(false);
        Toast.makeText(this, "onFailedToReceiveSetting", Toast.LENGTH_SHORT).show();
    }

    //GNInterstitialDialogListener listener override function
    // Sent just after closing interstitial ad because the
    // user clicked the button configured through server-side.
    @Override
    public void onButtonClick(int nButtonIndex) {
        Toast.makeText(this, "onButtonClick:" + nButtonIndex, Toast.LENGTH_SHORT).show();
    }

    //GNInterstitialDialogListener listener override function
    // Sent just after closing interstitial ad because the
    // user clicked close button in an ad, or device back button.
    @Override
    public void onClose() {
        Toast.makeText(this, "onClose", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gnad_sample_interstitial, menu);
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
