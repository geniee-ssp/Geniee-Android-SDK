package jp.co.geniee.samples.gnad.interstitial;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import jp.co.geniee.samples.R;
import jp.co.geniee.samples.SharedPreferenceManager;
import jp.co.geniee.gnadsdk.common.GNAdLogger;
import jp.co.geniee.gnadsdk.interstitial.GNInterstitial;

public class InterstitialDemoActivity extends AppCompatActivity implements GNInterstitial.GNInterstitialListener, GNInterstitial.GNInterstitialDialogListener, View.OnClickListener {

    private final String TAG = "[GNS]InterstitialDemo";

    private Context mContext;

    private EditText edtZoneId;
    private Button btLoadGNAd, btShowGNAd;

    private GNInterstitial mGnInterstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gnad_interstitial);

        mContext = this;

        edtZoneId = findViewById(R.id.edtZoneId);
        edtZoneId.setText(SharedPreferenceManager.getInstance(mContext).getString(SharedPreferenceManager.INTERSTITIAL_AD_ZONE_ID));
        btLoadGNAd = findViewById(R.id.btLoadGNAd);
        btShowGNAd = findViewById(R.id.btShowGNAd);

        btLoadGNAd.setOnClickListener(this);
        btShowGNAd.setOnClickListener(this);
    }

    @Override
    public void onReceiveSetting() {
        btShowGNAd.setEnabled(true);

        Log.d(TAG, "onReceiveSetting");

        Toast.makeText(mContext, "Interstitial ad received", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailedToReceiveSetting() {
        btShowGNAd.setEnabled(false);

        Log.d(TAG, "onFailedToReceiveSetting");

        Toast.makeText(mContext, "Interstitial ad failed to receive", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onButtonClick(int i) {

        Log.d(TAG, "onButtonClick");
    }

    @Override
    public void onClose() {

        Log.d(TAG, "onClose interstitial");
        btShowGNAd.setEnabled(false);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btLoadGNAd) {

            prepareInterstitialAd();

        } else if (view.getId() == R.id.btShowGNAd) {

            if (mGnInterstitial.isReady()) {
                mGnInterstitial.show(this);
            }
        }

    }

    private void prepareInterstitialAd() {

        try {
            mGnInterstitial = new GNInterstitial(this, Integer.parseInt(edtZoneId.getText().toString()));
            mGnInterstitial.setListener(this);
            mGnInterstitial.setDialoglistener(this);
            mGnInterstitial.load(this);
            mGnInterstitial.setGeoLocationEnable(true);
            mGnInterstitial.setLogPriority(GNAdLogger.INFO);

            SharedPreferenceManager.getInstance(mContext).putString(SharedPreferenceManager.INTERSTITIAL_AD_ZONE_ID, edtZoneId.getText().toString());
        } catch (Exception e) {
            edtZoneId.setError(e.getLocalizedMessage());
        }

    }
}
