package jp.co.geniee.gnssamplefullscreeninterstitial;

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

import jp.co.geniee.gnadsdk.common.GNSException;
import jp.co.geniee.gnadsdk.fullscreeninterstitial.GNSFullscreenInterstitialAd;
import jp.co.geniee.gnadsdk.fullscreeninterstitial.GNSFullscreenInterstitialAdListener;

public class MainActivity extends AppCompatActivity {
    private static String defaultZoneID = "YOUR_ZONE_ID";
    private GNSFullscreenInterstitialAd mFullscreenInterstitial;
    private Button mLoadRequestBtn;
    private Button mShowBtn;

    private ArrayList<String> mLogArrayList = new ArrayList<String>();
    private ArrayAdapter<String> mLogAdapter;

    public String statusMessage(String message)
    {
        return String.format("%s %s", (new SimpleDateFormat("HH:mm:ss")).format(Calendar.getInstance().getTime()), message);
    }

    private GNSFullscreenInterstitialAdListener mListener = new GNSFullscreenInterstitialAdListener() {
        @Override
        public void fullscreenInterstitialAdDidReceiveAd() {
            mLogArrayList.add(statusMessage("全画面インステ広告ロード成功。"));
            mLogAdapter.notifyDataSetChanged();
            // 広告再生ボタンを有効
            enableButton(mShowBtn);
        }

        @Override
        public void didFailToLoadWithError(GNSException e) {
            mLogArrayList.add(statusMessage("全画面インステ広告ロード失敗。(" + e.getAdnetworkName() + " Code:" + e.getCode() + " " + e.getMessage()));
            mLogAdapter.notifyDataSetChanged();
            // ロードボタン有効
            enableButton(mLoadRequestBtn);
            // 広告再生ボタンを無効
            disableButton(mShowBtn);
        }

        @Override
        public void fullscreenInterstitialAdWillPresentScreen(String adName) {
            Toast.makeText(getApplicationContext(),  adName + " 全画面インステを表示した。", Toast.LENGTH_LONG).show();
            mLogArrayList.add(statusMessage(adName + " 全画面インステ表示"));
            mLogAdapter.notifyDataSetChanged();
        }

        @Override
        public void fullscreenInterstitialAdDidClose(String adName) {
            mLogArrayList.add(statusMessage(adName + " 全画面インステが閉じられた"));
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
        setContentView(R.layout.activity_main);

        final EditText zoneIdEdit = (EditText) findViewById(R.id.gns_sample_zoneid_edit);
        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        defaultZoneID = preferences.getString("zoneId", defaultZoneID);
        zoneIdEdit.setText(defaultZoneID);
        mFullscreenInterstitial = new GNSFullscreenInterstitialAd(defaultZoneID, MainActivity.this);
        mFullscreenInterstitial.setFullscreenInterstitialAdListener(mListener);

        mLoadRequestBtn = (Button)findViewById(R.id.gns_sample_preload_button);
        mLoadRequestBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String zoneId = zoneIdEdit.getText().toString();
                if (zoneId.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Missing zone id", Toast.LENGTH_SHORT).show();
                    return;
                }
                mFullscreenInterstitial.setZoneId(zoneId);

                loadRequestFullscreen();

                SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
                SharedPreferences.Editor preferencesEdit = preferences.edit();
                preferencesEdit.putString("zoneId", zoneId);
                preferencesEdit.commit();
            }
        });
        mShowBtn = (Button)findViewById(R.id.gns_sample_show_button);
        // 広告表示ボタン無効
        disableButton(mShowBtn);
        mShowBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showFullscreen();
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

    private void loadRequestFullscreen() {
        // ロードボタンを無効
        disableButton(mLoadRequestBtn);
        mLogArrayList.add(statusMessage("全画面広告ロード中。"));
        mLogAdapter.notifyDataSetChanged();
        mFullscreenInterstitial.loadRequest();
    }

    private void showFullscreen() {
        if (mFullscreenInterstitial.canShow()) {
            // 広告再生ボタンを無効
            disableButton(mShowBtn);
            mFullscreenInterstitial.show();
        } else {
            mLogArrayList.add(statusMessage("全画面広告ロード中です。"));
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
        if (mFullscreenInterstitial != null) {
            mFullscreenInterstitial.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFullscreenInterstitial != null) {
            mFullscreenInterstitial.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (mFullscreenInterstitial != null) {
            mFullscreenInterstitial.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mFullscreenInterstitial != null) {
            mFullscreenInterstitial.onStop();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mFullscreenInterstitial != null) {
            mFullscreenInterstitial.onDestroy();
        }
        super.onDestroy();
    }
}

