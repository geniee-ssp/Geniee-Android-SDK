package jp.co.geniee.gnadgooglerewardmediationsample;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class RewardNewActivity extends AppCompatActivity {
    private String TAG = "RewardNewActivity";
    private Button mLoadRequestBtn;
    private Button mShowBtn;
    private RewardedAd mRewardedAd;
    private ArrayList<String> mLogArrayList = new ArrayList<String>();
    private ArrayAdapter<String> mLogAdapter;

    public String statusMessage(String message)
    {
        return String.format("%s %s", (new SimpleDateFormat("HH:mm:ss")).format(Calendar.getInstance().getTime()), message);
    }
    private RewardedAdLoadCallback mAdLoadCallback = new RewardedAdLoadCallback() {
        @Override
        public void onRewardedAdLoaded() {
            mLogArrayList.add(statusMessage("RewardVideo loading success"));
            mLogAdapter.notifyDataSetChanged();
            // Enable ad playback button
            enableButton(mShowBtn);
        }

        @Override
        public void onRewardedAdFailedToLoad(int errorCode) {
            String errorMassage = getErrorMassage(errorCode);
            Log.w(TAG, "onRewardedAdFailedToLoad: RewardVideo loading Failed( Code:" + errorCode + " Massage:" + errorMassage + ")");
            mLogArrayList.add(statusMessage("RewardVideo loading Failed( Code:" + errorCode + " Massage:" + errorMassage + ")"));
            mLogAdapter.notifyDataSetChanged();
            enableButton(mLoadRequestBtn);
            disableButton(mShowBtn);
        }
    };
    private RewardedAdCallback mAdCallback = new RewardedAdCallback() {
        public void onRewardedAdOpened() {
            Toast.makeText(getApplicationContext(), "RewardVideo ad playback opened " + mRewardedAd.getMediationAdapterClassName(), Toast.LENGTH_LONG).show();
            mLogArrayList.add(statusMessage("RewardVideo ad playback opened " + mRewardedAd.getMediationAdapterClassName()));
            mLogAdapter.notifyDataSetChanged();
        }

        public void onRewardedAdClosed() {
            mLogArrayList.add(statusMessage("RewardVideo has been closed"));
            mLogAdapter.notifyDataSetChanged();
            enableButton(mLoadRequestBtn);
            disableButton(mShowBtn);
        }

        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
            mLogArrayList.add(statusMessage("Give users a reward( " + rewardItem.getAmount() + rewardItem.getType() + ")"));
            Log.d(TAG, "onUserEarnedReward: RewardType = " + rewardItem.getType());
            Log.d(TAG, "onUserEarnedReward: RewardAmount = " + rewardItem.getAmount());
            mLogAdapter.notifyDataSetChanged();
        }

        public void onRewardedAdFailedToShow(int errorCode) {
            String errorMassage = getErrorMassage(errorCode);
            Log.w(TAG, "onRewardedAdFailedToShow: RewardVideo loading Failed( Code:" + errorCode + " Massage:" + errorMassage + ")");
            mLogArrayList.add(statusMessage("RewardVideo loading Failed( Code:" + errorCode + " Massage:" + errorMassage + ")"));
            mLogAdapter.notifyDataSetChanged();
            enableButton(mLoadRequestBtn);
            disableButton(mShowBtn);
        }
    };
    private String getErrorMassage(int errorCode) {
        String errorMassage = "";
        switch (errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                return "Something happened internally; for instance, an invalid response was received from the ad server";
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                return "The ad request was invalid for instance, the ad unit ID was incorrect";
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                return "The ad request was unsuccessful due to network connectivit";
            case AdRequest.ERROR_CODE_NO_FILL:
                return "The ad request was successful, but no ad was returned due to lack of ad inventory";
        }
        return errorMassage;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_new);

        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        final String unitId = preferences.getString("UnitID", MainActivity.defaultUnitID);

        mLoadRequestBtn = (Button)findViewById(R.id.gns_sample_preload_button);
        mLoadRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (unitId.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Missing unit id", Toast.LENGTH_SHORT).show();
                    return;
                }
                mRewardedAd = createAndLoadRewardedAd(unitId);

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

    private RewardedAd createAndLoadRewardedAd(String unitId) {
        // Disable load button
        disableButton(mLoadRequestBtn);
        mLogArrayList.add(statusMessage("RewardVideo is loading start " + unitId));
        mLogAdapter.notifyDataSetChanged();

        RewardedAd rewardedAd = new RewardedAd(this, unitId);

        rewardedAd.loadAd(
                new AdRequest.Builder().build(), mAdLoadCallback);
        return rewardedAd;
    }

    private void showVideoReward() {
        if (mRewardedAd.isLoaded()) {
            disableButton(mShowBtn);
            mRewardedAd.show(this, mAdCallback);
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

}
