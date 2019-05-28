package jp.co.geniee.gnadgooglefullscreeninterstitialsample;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private String TAG = "GNAdGoogleFullscreenMediationSample";
    private static String defaultUnitID= "";
    private PublisherInterstitialAd mPublisherInterstitialAd;
    private Button mLoadRequestBtn;
    private Button mShowBtn;

    private ArrayList<String> mLogArrayList = new ArrayList<String>();
    private ArrayAdapter<String> mLogAdapter;

    public String statusMessage(String message)
    {
        return String.format("%s %s", (new SimpleDateFormat("HH:mm:ss")).format(Calendar.getInstance().getTime()), message);
    }

    private AdListener mListener = new AdListener(){
        @Override
        public void onAdLoaded() {

            mLogArrayList.add(statusMessage("Interstitial loading success"));
            mLogAdapter.notifyDataSetChanged();
            // Enable ad playback button
            enableButton(mShowBtn);

        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            String errorMassage = "";
            switch (errorCode){
                case PublisherAdRequest.ERROR_CODE_INTERNAL_ERROR:
                    errorMassage = "Something happened internally; for instance, an invalid response was received from the ad server";
                    break;
                case PublisherAdRequest.ERROR_CODE_INVALID_REQUEST:
                    errorMassage = "The ad request was invalid for instance, the ad unit ID was incorrect";
                    break;
                case PublisherAdRequest.ERROR_CODE_NETWORK_ERROR:
                    errorMassage = "The ad request was unsuccessful due to network connectivit";
                    break;
                case PublisherAdRequest.ERROR_CODE_NO_FILL:
                    errorMassage = "The ad request was successful, but no ad was returned due to lack of ad inventory";
                    break;
            }
            Log.w(TAG, "onInterstitialAdFailedToLoad: Interstitial loading Failed( Code:" + errorCode + " Massage:" + errorMassage + ")");
            mLogArrayList.add(statusMessage("Interstitial loading Failed( Code:" + errorCode + " Massage:" + errorMassage + ")"));
            mLogAdapter.notifyDataSetChanged();
            // Load button enabled
            enableButton(mLoadRequestBtn);
            // Disable ad play button
            disableButton(mShowBtn);
        }

        @Override
        public void onAdOpened() {
            mLogArrayList.add(statusMessage("Interstitial ad playback opened"));
            mLogAdapter.notifyDataSetChanged();
        }

        @Override
        public void onAdLeftApplication() {
            mLogArrayList.add(statusMessage("Interstitial Left Application"));
            mLogAdapter.notifyDataSetChanged();
        }

        @Override
        public void onAdClosed() {
            mLogArrayList.add(statusMessage("Interstitial has been closed"));
            mLogAdapter.notifyDataSetChanged();
            // Load button enabled
            enableButton(mLoadRequestBtn);
            // Disable ad play button
            disableButton(mShowBtn);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText UnitIdEdit = (EditText) findViewById(R.id.gns_sample_unitid_edit);
        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        defaultUnitID = preferences.getString("UnitID", defaultUnitID);
        UnitIdEdit.setText(defaultUnitID);

        mPublisherInterstitialAd = new PublisherInterstitialAd(this);
        mPublisherInterstitialAd.setAdListener(mListener);
        mPublisherInterstitialAd.setAdUnitId(defaultUnitID);

        mLoadRequestBtn = (Button)findViewById(R.id.gns_sample_preload_button);
        mLoadRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadInterstitialAd();

                String UnitID = UnitIdEdit.getText().toString();
                if (UnitID.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Missing unit id", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
                SharedPreferences.Editor preferencesEdit = preferences.edit();
                preferencesEdit.putString("UnitID", UnitID);
                preferencesEdit.commit();
            }
        });
        mShowBtn = (Button)findViewById(R.id.gns_sample_show_button);
        // Disable ad play button
        disableButton(mShowBtn);
        mShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInterstitialAd();
            }
        });
        if(mLogAdapter == null)
            mLogAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, mLogArrayList){
                @Override
                public View getView(int position, View convertView, ViewGroup parent){
                    View view = super.getView(position, convertView, parent);
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                    tv.setTextColor(Color.BLACK);
                    tv.setSingleLine(false);
                    return view;
                }
            };
        ((ListView)findViewById(R.id.gns_sample_list_view)).setAdapter(mLogAdapter);
    }

    private void showInterstitialAd() {
        if (mPublisherInterstitialAd.isLoaded()) {
            // Disable ad play button
            disableButton(mShowBtn);
            mPublisherInterstitialAd.show();
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

        mPublisherInterstitialAd.loadAd(new PublisherAdRequest.Builder().build());
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
}