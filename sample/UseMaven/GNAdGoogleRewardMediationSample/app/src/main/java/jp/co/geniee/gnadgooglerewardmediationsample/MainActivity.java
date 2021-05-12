package jp.co.geniee.gnadgooglerewardmediationsample;

import android.app.Activity;
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
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private RewardedAd mRewardedAd;
    private Button mLoadRequestBtn;
    private Button mShowBtn;

    private ArrayList<String> mLogArrayList = new ArrayList<String>();
    private ArrayAdapter<String> mLogAdapter;

    public static String defaultUnitID = "YOUR_ADMOB_OR_DFP_AD_UNIT_ID";
    private EditText mUnitIdEdit;
    SharedPreferences mPreferences;

    public String statusMessage(String message) {
        return String.format("%s %s", (new SimpleDateFormat("HH:mm:ss")).format(Calendar.getInstance().getTime()), message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //When debugging, set the test device in the following format.
        //Please do not forget to delete this setting when release.
        /*
        List<String> testDeviceIds = Arrays.asList("YOUR_TEST_DEVICE_ID");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);
         */

        mUnitIdEdit = (EditText) findViewById(R.id.gns_sample_unitid_edit);
        mPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        defaultUnitID = mPreferences.getString("UnitID", defaultUnitID);
        mUnitIdEdit.setText(defaultUnitID);

        mLoadRequestBtn = (Button) findViewById(R.id.gns_sample_preload_button);
        mLoadRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (defaultUnitID.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Missing unit id", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveUnitId();
                loadRewardedVideoAd(defaultUnitID);
            }
        });
        mShowBtn = (Button) findViewById(R.id.gns_sample_show_button);
        // Disable ad play button
        disableButton(mShowBtn);
        mShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVideoReward();
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

    private void loadRewardedVideoAd(String unitId) {
        // Disable load button
        disableButton(mLoadRequestBtn);
        mLogArrayList.add(statusMessage("RewardVideo is loading start " + unitId));
        mLogAdapter.notifyDataSetChanged();

        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, defaultUnitID,
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.getMessage());
                        mRewardedAd = null;

                        String errorMessage = "";
                        switch (loadAdError.getCode()) {
                            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                                errorMessage = "Something happened internally; for instance, an invalid response was received from the ad server";
                                break;
                            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                                errorMessage = "The ad request was invalid for instance, the ad unit ID was incorrect";
                                break;
                            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                                errorMessage = "The ad request was unsuccessful due to network connectivit";
                                break;
                            case AdRequest.ERROR_CODE_NO_FILL:
                                errorMessage = "The ad request was successful, but no ad was returned due to lack of ad inventory";
                                break;
                        }
                        Log.w(TAG, "onRewardedVideoAdFailedToLoad: RewardVideo loading Failed( Code:" + loadAdError + " Message:" + errorMessage + ")");
                        mLogArrayList.add(statusMessage("RewardVideo loading Failed( Code:" + loadAdError + " Message:" + errorMessage + ")"));
                        mLogAdapter.notifyDataSetChanged();
                        // Load button enabled
                        enableButton(mLoadRequestBtn);
                        // Disable ad play button
                        disableButton(mShowBtn);
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {

                        Log.d(TAG, "onAdFailedToLoad");

                        mRewardedAd = rewardedAd;
                        mRewardedAd.setFullScreenContentCallback(mFullScreenContentCallback);

                        mLogArrayList.add(statusMessage("RewardVideo loading success"));
                        mLogAdapter.notifyDataSetChanged();
                        // Enable ad playback button
                        enableButton(mShowBtn);
                    }
                });
    }

    private void showVideoReward() {
        if (mRewardedAd != null) {
            // Disable ad play button
            disableButton(mShowBtn);

            Activity activityContext = MainActivity.this;
            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    mLogArrayList.add(statusMessage("Give users a reward( " + rewardItem.getAmount() + rewardItem.getType() + ")"));
                    Log.d(TAG, "onRewarded: RewardType = " + rewardItem.getType());
                    Log.d(TAG, "onRewarded: RewardAmount = " + rewardItem.getAmount());
                    mLogAdapter.notifyDataSetChanged();
                }
            });
        } else {
            mLogArrayList.add(statusMessage("RewardVideo is loading"));
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    private FullScreenContentCallback mFullScreenContentCallback = new FullScreenContentCallback() {
        @Override
        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
            super.onAdFailedToShowFullScreenContent(adError);

            mLogArrayList.add(statusMessage("RewardVideo content failed to show"));
            mLogAdapter.notifyDataSetChanged();
            // Load button enabled
            enableButton(mLoadRequestBtn);
            // Disable ad play button
            disableButton(mShowBtn);
        }

        @Override
        public void onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent();

            mLogArrayList.add(statusMessage("RewardVideo ad playback opened"));
            mLogAdapter.notifyDataSetChanged();
        }

        @Override
        public void onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent();

            mLogArrayList.add(statusMessage("RewardVideo has been closed"));
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

    private void saveUnitId() {
        String unitId = mUnitIdEdit.getText().toString();
        if (!unitId.isEmpty()) {
            SharedPreferences.Editor preferencesEdit = mPreferences.edit();
            preferencesEdit.putString("UnitID", unitId);
            preferencesEdit.commit();
        }
    }
}
