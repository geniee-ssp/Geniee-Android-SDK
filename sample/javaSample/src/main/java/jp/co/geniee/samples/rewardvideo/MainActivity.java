package jp.co.geniee.samples.rewardvideo;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import jp.co.geniee.gnadsdk.rewardvideo.GNSRewardVideoAd;
import jp.co.geniee.gnadsdk.rewardvideo.GNSRewardVideoAdListener;
import jp.co.geniee.gnadsdk.rewardvideo.GNSVideoRewardData;
import jp.co.geniee.gnadsdk.rewardvideo.GNSVideoRewardException;
import jp.co.geniee.samples.R;
import jp.co.geniee.samples.SharedPreferenceManager;

public class MainActivity extends AppCompatActivity {
    private static String defaultZoneID = "YOUR_ZONE_ID";
    private GNSRewardVideoAd mReward;
    private Button mLoadRequestBtn;
    private Button mShowBtn;

    private ArrayList<String> mLogArrayList = new ArrayList<String>();
    private ArrayAdapter<String> mLogAdapter;

    public String statusMessage(String message)
    {
        return String.format("%s %s", (new SimpleDateFormat("HH:mm:ss")).format(Calendar.getInstance().getTime()), message);
    }

    private GNSRewardVideoAdListener mListener = new GNSRewardVideoAdListener() {
        @Override
        public void rewardVideoAdDidReceiveAd() {
            mLogArrayList.add(statusMessage("動画広告ロード成功。"));
            mLogAdapter.notifyDataSetChanged();
            // 広告再生ボタンを有効
            enableButton(mShowBtn);
        }
        @Override
        public void rewardVideoAdDidStartPlaying(GNSVideoRewardData data) {
            Toast.makeText(getApplicationContext(), "動画広告の再生を開始しました。(" +
                    data.adName + ")", Toast.LENGTH_LONG).show();
            mLogArrayList.add(statusMessage("動画広告再生開始。(" + data.adName + ")"));
            mLogAdapter.notifyDataSetChanged();
        }
        @Override
        public void didRewardUserWithReward(GNSVideoRewardData data) {
            mLogArrayList.add(statusMessage("ユーザにリワードを付与。(" + data.adName + " " + data.amount + data.type + ")"));
            mLogAdapter.notifyDataSetChanged();
        }
        @Override
        public void rewardVideoAdDidClose(GNSVideoRewardData data) {
            mLogArrayList.add(statusMessage("動画広告が閉じられた。(" + data.adName + ")"));
            mLogAdapter.notifyDataSetChanged();
            // ロードボタン有効
            enableButton(mLoadRequestBtn);
            // 広告再生ボタンを無効
            disableButton(mShowBtn);
        }
        @Override
        public void didFailToLoadWithError(GNSVideoRewardException e) {
            mLogArrayList.add(statusMessage("動画広告ロード失敗。(" + e.getAdnetworkName() + " Code:" + e.getCode() + " " + e.getMessage()));
            mLogAdapter.notifyDataSetChanged();
            // ロードボタン有効
            enableButton(mLoadRequestBtn);
            // 広告再生ボタンを無効
            disableButton(mShowBtn);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewardvideo);

        final EditText zoneIdEdit = (EditText) findViewById(R.id.gns_sample_zoneid_edit);
        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        defaultZoneID = preferences.getString(SharedPreferenceManager.REWARDED_VIDEO_AD_ZONE_ID, defaultZoneID);
        zoneIdEdit.setText(defaultZoneID);

        mReward = new GNSRewardVideoAd(defaultZoneID, MainActivity.this);
        mReward.setRewardVideoAdListener(mListener);

        mLoadRequestBtn = (Button)findViewById(R.id.gns_sample_preload_button);
        mLoadRequestBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String zoneId = zoneIdEdit.getText().toString();
                if (zoneId.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Missing zone id", Toast.LENGTH_SHORT).show();
                    return;
                }
                mReward.setZoneId(zoneId);

                loadRequestVideoReward();

                SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
                SharedPreferences.Editor preferencesEdit = preferences.edit();
                preferencesEdit.putString(SharedPreferenceManager.REWARDED_VIDEO_AD_ZONE_ID, zoneId);
                preferencesEdit.commit();
            }
        });
        mShowBtn = (Button)findViewById(R.id.gns_sample_show_button);
        // 広告表示ボタン無効
        disableButton(mShowBtn);
        mShowBtn.setOnClickListener(new OnClickListener() {
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

    private void loadRequestVideoReward() {
        // ロードボタンを無効
        disableButton(mLoadRequestBtn);
        mLogArrayList.add(statusMessage("動画広告ロード中。"));
        mLogAdapter.notifyDataSetChanged();
        mReward.loadRequest(false);
    }

    private void showVideoReward() {
        if (mReward.canShow()) {
            // 広告再生ボタンを無効
            disableButton(mShowBtn);
            mReward.show();
        } else {
            mLogArrayList.add(statusMessage("動画広告ロード中です。"));
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
    protected void onStart() {
        super.onStart();
        if (mReward != null) {
            mReward.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mReward != null) {
            mReward.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (mReward != null) {
            mReward.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mReward != null) {
            mReward.onStop();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mReward != null) {
            mReward.onDestroy();
        }
        super.onDestroy();
    }
}
