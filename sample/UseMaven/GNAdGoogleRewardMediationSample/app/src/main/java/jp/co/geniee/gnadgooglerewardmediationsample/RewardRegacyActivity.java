package jp.co.geniee.gnadgooglerewardmediationsample;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class RewardRegacyActivity extends AppCompatActivity {
    private String TAG = "RewardRegacyActivity";
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
            mLogArrayList.add(statusMessage("RewardVideo completed"));
            mLogAdapter.notifyDataSetChanged();
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
        setContentView(R.layout.activity_reward_regacy);

        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        final String unitId = preferences.getString("UnitID", MainActivity.defaultUnitID);

        mReward = MobileAds.getRewardedVideoAdInstance(this);
        mReward.setRewardedVideoAdListener(mListener);

        mLoadRequestBtn = (Button)findViewById(R.id.gns_sample_preload_button);
        mLoadRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (unitId.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Missing unit id", Toast.LENGTH_SHORT).show();
                    return;
                }
                loadRewardedVideoAd(unitId);
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

    private void loadRewardedVideoAd(String unitId) {
        // Disable load button
        disableButton(mLoadRequestBtn);
        mLogArrayList.add(statusMessage("RewardVideo is loading start " + unitId));
        mLogAdapter.notifyDataSetChanged();
        mReward.loadAd(unitId,
                new AdRequest.Builder().build());
    }

    private void showVideoReward() {
        if (mReward.isLoaded()) {
            // Disable ad play button
            disableButton(mShowBtn);
            mReward.show();
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
