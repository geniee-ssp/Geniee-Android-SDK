package jp.co.geniee.samples.gnad.banner;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import jp.co.geniee.gnadsdk.banner.GNAdEventListener;
import jp.co.geniee.gnadsdk.banner.GNAdView;
import jp.co.geniee.samples.R;
import jp.co.geniee.samples.SharedPreferenceManager;

/**
 * Demonstrates declaring a Geniee banner ({@link GNAdView}) directly in an XML layout.
 * Size, touch type and options are set from the layout via app: attributes; only the
 * zone id and the load trigger are handled in code.
 */
public class XmlBannerDemoActivity extends AppCompatActivity {

    private static final String TAG = "[GNS]XmlBannerDemo";

    private GNAdView adView;
    private EditText edtZoneId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gnad_xml_banner);

        // The banner is inflated from XML - just grab the reference.
        adView = findViewById(R.id.gnAdView);
        adView.setListener(new GNAdEventListener() {
            @Override
            public void onReceiveAd(GNAdView gnAdView) {
                String winner = gnAdView.getWinnerNetworkName();
                Log.d(TAG, "onReceiveAd - winnerNetworkName=" + winner);
                Toast.makeText(XmlBannerDemoActivity.this, "Ad received - " + (winner != null ? winner : "Unknown"), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFaildToReceiveAd(GNAdView gnAdView) {
                Log.d(TAG, "onFaildToReceiveAd");
                Toast.makeText(XmlBannerDemoActivity.this, "Failed to receive ad", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdHidden(GNAdView gnAdView) {
                Log.d(TAG, "onAdHidden");
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

        edtZoneId = findViewById(R.id.edtZoneId);
        edtZoneId.setText(SharedPreferenceManager.getInstance(this).getString(SharedPreferenceManager.SINGLE_BANNER_ZONE_ID));

        Button loadBtn = findViewById(R.id.btLoadGNAd);
        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String zoneId = edtZoneId.getText().toString();
                try {
                    // Zone id can also be provided in the layout via app:zoneId.
                    adView.setAppId(zoneId);
                    adView.startAdLoop();
                    SharedPreferenceManager.getInstance(XmlBannerDemoActivity.this)
                            .putString(SharedPreferenceManager.SINGLE_BANNER_ZONE_ID, zoneId);
                } catch (Exception e) {
                    edtZoneId.setError(e.getLocalizedMessage());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.clearAdView();
        }
        super.onDestroy();
    }
}
