package jp.co.geniee.gnaddfprewardmediationsample;

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

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private String TAG = "GNAdDFPRewardMediationSample";
    private static String defaultUnitID= "/9116787/app_team_test_Reward";
    private RewardedVideoAd mReward;
    private Button mLoadRequestBtn;
    private Button mShowBtn;

    private ArrayList<String> mLogArrayList = new ArrayList<String>();
    private ArrayAdapter<String> mLogAdapter;

    public String statusMessage(String message)
    {
        return String.format("%s %s", (new SimpleDateFormat("HH:mm:ss")).format(Calendar.getInstance().getTime()), message);
    }

    private RewardedVideoAdListener mListener = new RewardedVideoAdListener() {
        @Override
        public void onRewardedVideoAdLoaded() {
            mLogArrayList.add(statusMessage("RewardVideo loading success"));
            mLogAdapter.notifyDataSetChanged();
            // Enable ad playback button
            enableButton(mShowBtn);
        }
        @Override
        public void onRewardedVideoAdOpened() {
            mLogArrayList.add(statusMessage("RewardVideo ad playback opened"));
            mLogAdapter.notifyDataSetChanged();
        }
        @Override
        public void onRewardedVideoStarted() {
            Toast.makeText(getApplicationContext(), "RewardVideo ad playback started(" + mReward.getMediationAdapterClassName() + ")", Toast.LENGTH_LONG).show();
            mLogArrayList.add(statusMessage("RewardVideo ad playback started(" + mReward.getMediationAdapterClassName() + ")"));
            mLogAdapter.notifyDataSetChanged();
        }
        @Override
        public void onRewarded(RewardItem rewardItem) {
            mLogArrayList.add(statusMessage("Give users a reward( " + rewardItem.getAmount() + rewardItem.getType() + ")"));
            Log.d(TAG, "onRewarded: RewardType = " + rewardItem.getType());
            Log.d(TAG, "onRewarded: RewardAmount = " + rewardItem.getAmount());
            mLogAdapter.notifyDataSetChanged();
        }

        @Override
        public void onRewardedVideoAdLeftApplication() {
            mLogArrayList.add(statusMessage("RewardVideo Left Application"));
            mLogAdapter.notifyDataSetChanged();
        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int errorCode) {
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
            Log.w(TAG, "onRewardedVideoAdFailedToLoad: RewardVideo loading Failed( Code:" + errorCode + " Massage:" + errorMassage + ")");
            mLogArrayList.add(statusMessage("RewardVideo loading Failed( Code:" + errorCode + " Massage:" + errorMassage + ")"));
            mLogAdapter.notifyDataSetChanged();
            // Load button enabled
            enableButton(mLoadRequestBtn);
            // Disable ad play button
            disableButton(mShowBtn);

        }

        @Override
        public void onRewardedVideoCompleted() {

        }

        @Override
        public void onRewardedVideoAdClosed() {
            mLogArrayList.add(statusMessage("RewardVideo has been closed"));
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

        mReward = MobileAds.getRewardedVideoAdInstance(this);
        mReward.setRewardedVideoAdListener(mListener);

        mLoadRequestBtn = (Button)findViewById(R.id.gns_sample_preload_button);
        mLoadRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String UnitID = UnitIdEdit.getText().toString();
                if (UnitID.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Missing unit id", Toast.LENGTH_SHORT).show();
                    return;
                }

                loadRewardedVideoAd(UnitID);

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
                showVideoReward();
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

    private void showVideoReward() {
        if (mReward.isLoaded()) {
            // Disable ad play button
            disableButton(mShowBtn);
            mReward.show();
        } else {
            mLogArrayList.add(statusMessage("RwardVideo is loading"));
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


    private void loadRewardedVideoAd(String unitID) {
        // Disable load button
        disableButton(mLoadRequestBtn);
        mLogArrayList.add(statusMessage("RwardVideo is loading start"));
        mLogAdapter.notifyDataSetChanged();
        mReward.loadAd(unitID,
                //When debugging, set the test device in the following format.
                //  new PublisherAdRequest.Builder().addTestDevice ("XXXXX").build())
                //Please do not forget to delete this setting when release.　
                new PublisherAdRequest.Builder()
                        //.addTestDevice("YOUR_TEST_DEVICE_ID")
                        .build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mReward != null) {
            mReward.resume(this);
        }
    }

    @Override
    protected void onPause() {
        if (mReward != null) {
            mReward.pause(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mReward != null) {
            mReward.destroy(this);
        }
        super.onDestroy();
    }
}
