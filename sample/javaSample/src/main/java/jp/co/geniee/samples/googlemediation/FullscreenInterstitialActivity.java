package jp.co.geniee.samples.googlemediation;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import jp.co.geniee.samples.R;
import jp.co.geniee.samples.SharedPreferenceManager;

public class FullscreenInterstitialActivity extends AppCompatActivity {
    private String TAG = "GNAdDFPFullscreenMediationSample";
    private static String defaultUnitID = "/424536528/1540247_In-app_test_GAM_Android_interstitial_com.geniee.get_the_gold";
    private AdManagerInterstitialAd mInterstitialAd;
    private Button mLoadRequestBtn;
    private Button mShowBtn;
    private EditText unitIdEdit;

    private ArrayList<String> mLogArrayList = new ArrayList<String>();
    private ArrayAdapter<String> mLogAdapter;

    public String statusMessage(String message) {
        return String.format("%s %s", (new SimpleDateFormat("HH:mm:ss")).format(Calendar.getInstance().getTime()), message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googlemediation_fullscreeninterstitial);

        unitIdEdit = (EditText) findViewById(R.id.gns_sample_unitid_edit);
        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        defaultUnitID = preferences.getString("googlemediation_" + SharedPreferenceManager.INTERSTITIAL_AD_ZONE_ID, defaultUnitID);
        unitIdEdit.setText(defaultUnitID);

        Log.i(TAG, "appId=" + unitIdEdit.getText().toString());

        mLoadRequestBtn = (Button) findViewById(R.id.gns_sample_preload_button);
        mLoadRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadInterstitialAd();

                String unitID = unitIdEdit.getText().toString();
                if (unitID.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Missing unit id", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
                SharedPreferences.Editor preferencesEdit = preferences.edit();
                preferencesEdit.putString("googlemediation_" + SharedPreferenceManager.INTERSTITIAL_AD_ZONE_ID, unitID);
                preferencesEdit.commit();
            }
        });
        mShowBtn = (Button) findViewById(R.id.gns_sample_show_button);
        // Disable ad play button
        disableButton(mShowBtn);
        mShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInterstitialAd();
            }
        });
        if (mLogAdapter == null)
            mLogAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, mLogArrayList) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                    tv.setTextColor(Color.BLACK);
                    tv.setSingleLine(false);
                    return view;
                }
            };
        ((ListView) findViewById(R.id.gns_sample_list_view)).setAdapter(mLogAdapter);
    }

    private void showInterstitialAd() {
        if (mInterstitialAd != null) {
            // Disable ad play button
            disableButton(mShowBtn);
            mInterstitialAd.show(FullscreenInterstitialActivity.this);
        } else {
            mLogArrayList.add(statusMessage("Interstitial is loading"));
            mLogAdapter.notifyDataSetChanged();
        }
    }

    private void enableButton(Button btn) {
        btn.setEnabled(true);
        AlphaAnimation alphaUp = new AlphaAnimation(1f, 1f);
        alphaUp.setFillAfter(true);
        btn.startAnimation(alphaUp);
    }

    private void disableButton(Button btn) {
        btn.setEnabled(false);
        AlphaAnimation alphaUp = new AlphaAnimation(0.45f, 0.45f);
        alphaUp.setFillAfter(true);
        btn.startAnimation(alphaUp);
    }


    private void loadInterstitialAd() {
        // Disable load button
        disableButton(mLoadRequestBtn);
        mLogArrayList.add(statusMessage("Interstitial is loading start"));
        mLogAdapter.notifyDataSetChanged();

        //When debugging, set the test device in the following format.
        //Please do not forget to delete this setting when release.　
        /*
        List<String> testDeviceIds = Arrays.asList("YOUR_TEST_DEVICE_ID");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);
         */

        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();

        AdManagerInterstitialAd.load(this, unitIdEdit.getEditableText().toString(), adRequest, new AdManagerInterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                mInterstitialAd.setFullScreenContentCallback(mFullScreenContentCallback);
                Log.i(TAG, "onAdLoaded");

                mLogArrayList.add(statusMessage("Interstitial loading success"));
                mLogAdapter.notifyDataSetChanged();
                // Enable ad playback button
                enableButton(mShowBtn);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;

                String errorMassage = "";
                switch (loadAdError.getCode()) {
                    case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                        errorMassage = "Something happened internally; for instance, an invalid response was received from the ad server";
                        break;
                    case AdRequest.ERROR_CODE_INVALID_REQUEST:
                        errorMassage = "The ad request was invalid for instance, the ad unit ID was incorrect";
                        break;
                    case AdRequest.ERROR_CODE_NETWORK_ERROR:
                        errorMassage = "The ad request was unsuccessful due to network connectivit";
                        break;
                    case AdRequest.ERROR_CODE_NO_FILL:
                        errorMassage = "The ad request was successful, but no ad was returned due to lack of ad inventory";
                        break;
                }
                Log.w(TAG, "onInterstitialAdFailedToLoad: Interstitial loading Failed( Code:" + loadAdError.getCode() + " Massage:" + errorMassage + ")");
                mLogArrayList.add(statusMessage("Interstitial loading Failed( Code:" + loadAdError.getCode() + " Massage:" + errorMassage + ")"));
                mLogAdapter.notifyDataSetChanged();
                // Load button enabled
                enableButton(mLoadRequestBtn);
                // Disable ad play button
                disableButton(mShowBtn);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private FullScreenContentCallback mFullScreenContentCallback = new FullScreenContentCallback() {
        @Override
        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
            super.onAdFailedToShowFullScreenContent(adError);

            mLogArrayList.add(statusMessage("Interstitial content failed to show"));
            mLogAdapter.notifyDataSetChanged();
            // Load button enabled
            enableButton(mLoadRequestBtn);
            // Disable ad play button
            disableButton(mShowBtn);
        }

        @Override
        public void onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent();

            mLogArrayList.add(statusMessage("Interstitial ad playback opened"));
            mLogAdapter.notifyDataSetChanged();
        }

        @Override
        public void onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent();

            mLogArrayList.add(statusMessage("Interstitial has been closed"));
            mLogAdapter.notifyDataSetChanged();
            // Load button enabled
            enableButton(mLoadRequestBtn);
            // Disable ad play button
            disableButton(mShowBtn);
        }

        @Override
        public void onAdImpression() {
            super.onAdImpression();
        }
    };

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
