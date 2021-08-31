package jp.co.geniee.samples.banner;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import jp.co.geniee.samples.R;
import jp.co.geniee.samples.SharedPreferenceManager;
import jp.co.geniee.gnadsdk.banner.GNAdEventListener;
import jp.co.geniee.gnadsdk.banner.GNAdSize;
import jp.co.geniee.gnadsdk.banner.GNAdView;

public class SingleBannerDemoActivity extends AppCompatActivity {

    private final String TAG = "[GNS]SingleBannerDemo";

    private Context mContext;

    private GNAdView adView;
    private Spinner spinnerBannerSizes;
    private EditText edtZoneId;
    private Button mBtLoadGNAd;
    private LinearLayout AdviewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_banner_demo);

        mContext = this;

        spinnerBannerSizes = findViewById(R.id.spinnerBannerSizes);
        setUpSpinnerBannerSizes();

        edtZoneId = findViewById(R.id.edtZoneId);
        edtZoneId.setText(SharedPreferenceManager.getInstance(mContext).getString(SharedPreferenceManager.SINGLE_BANNER_ZONE_ID));

        mBtLoadGNAd = findViewById(R.id.btLoadGNAd);
        AdviewLayout = findViewById(R.id.AdviewLayout);

        mBtLoadGNAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                prepareBannerView();

                if (adView != null) {

                    try {
                        adView.setAppId(edtZoneId.getText().toString());
                        // Enable this if you want to get ads from mediation
                        // adView.useMediation(true);
                        adView.startAdLoop();

                        SharedPreferenceManager.getInstance(mContext).putString(SharedPreferenceManager.SINGLE_BANNER_ZONE_ID, edtZoneId.getText().toString());
                    } catch (Exception e) {
                        edtZoneId.setError(e.getLocalizedMessage());
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adView != null) {
            adView.stopAdLoop();
        }
    }

    @Override
    protected void onDestroy() {
        if (null != adView) {
            adView.clearAdView();
        }
        super.onDestroy();
    }

    private void prepareBannerView() {

        if (adView != null) {
            adView.clearAdView();
            AdviewLayout.removeView(adView);
        }

        GNAdSize adSize = GNAdSize.W320H50;

        switch (spinnerBannerSizes.getSelectedItem().toString()) {
            case "W320H50":
                adSize = GNAdSize.W320H50;
                break;
            case "W320H48":
                adSize = GNAdSize.W320H48;
                break;
            case "W300H250":
                adSize = GNAdSize.W300H250;
                break;
            case "W728H90":
                adSize = GNAdSize.W728H90;
                break;
            case "W468H60":
                adSize = GNAdSize.W468H60;
                break;
            case "W120H600":
                adSize = GNAdSize.W120H600;
                break;
            case "W320H100":
                adSize = GNAdSize.W320H100;
                break;
            case "W57H57":
                adSize = GNAdSize.W57H57;
                break;
            case "W76H76":
                adSize = GNAdSize.W76H76;
                break;
            case "W480H32":
                adSize = GNAdSize.W480H32;
                break;
            case "W768H66":
                adSize = GNAdSize.W768H66;
                break;
            case "W1024H66":
                adSize = GNAdSize.W1024H66;
                break;
            default:
                adSize = GNAdSize.W320H50;
        }

        // Initializes a GNAdView
        adView = new GNAdView(mContext, adSize);
        // alternatively, initialize with a GNTouchType
        //adView = new GNAdView(mContext,adSize,GNTouchType.TAP_AND_FLICK);
        final LinearLayout layout = (LinearLayout)findViewById(R.id.AdviewLayout);
        layout.addView(adView);

        adView.setListener(new GNAdEventListener() {
            @Override
            public void onReceiveAd(GNAdView gnAdView) {
                Log.d(TAG, "onReceiveAd");
                Toast.makeText(mContext, "Ad received", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFaildToReceiveAd(GNAdView gnAdView) {
                Log.d(TAG, "onFaildToReceiveAd");
                Toast.makeText(mContext, "Failed to receive ad", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStartExternalBrowser(GNAdView gnAdView) {
                Log.d(TAG, "onStartExternalBrowser");
            }

            @Override
            public void onStartInternalBrowser(GNAdView gnAdView) {
                Log.d(TAG, "onStartInternalBrowser");
            }

            @Override
            public void onTerminateInternalBrowser(GNAdView gnAdView) {
                Log.d(TAG, "onTerminateInternalBrowser");
            }

            @Override
            public boolean onShouldStartInternalBrowserWithClick(String s) {
                return false;
            }
        });


    }

    private void setUpSpinnerBannerSizes() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, this.getResources().getStringArray(R.array.banner_size_arrays));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBannerSizes.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
    }
}
