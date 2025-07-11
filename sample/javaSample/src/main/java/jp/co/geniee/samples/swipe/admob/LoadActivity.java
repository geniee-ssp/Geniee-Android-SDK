package jp.co.geniee.samples.swipe.admob;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import jp.co.geniee.samples.R;

public class LoadActivity extends AppCompatActivity {

    private final String  TAG = "LoadActivity";
    private Button mLoadRequestBtn;
    private String mAdSize = "320x50";
    private static final String default_ad_unit_id = "ca-app-pub-3940256099942544/9214589741";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_admob_load);

        /*
        デバッグ・テスト用（本番時は必ず本番ように戻す必要がある）
        デバッグ・テスト時にTestDeviceの宣言をせずに何度も広告を表示した場合、AdMobの規約違反でその端末で広告が表示さ>れなくなる可能性があるため、
        addTestDeviceの設定を必ず実施する
        TestDeviceはloadAdする際にlogcatに表示される。
        testDevices.add(AdRequest.DEVICE_ID_EMULATOR);       // All emulators
        */

        String androidId =  Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String testDeviceId = md5(androidId).toUpperCase();
        List<String> testDevices = new ArrayList<>();
        testDevices.add(testDeviceId);
        RequestConfiguration requestConfiguration
                = new RequestConfiguration.Builder()
                .setTestDeviceIds(testDevices)
                .build();
        MobileAds.setRequestConfiguration(requestConfiguration);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[] {
                        "320x50",
                        "300x250",
                        "320x100"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.sizelist);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                Spinner spinner = (Spinner)parent;
                String adSize = (String)spinner.getSelectedItem();
                mAdSize = adSize;
                Log.d(TAG, "size=" + mAdSize);
                SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
                SharedPreferences.Editor preferencesEdit = preferences.edit();
                preferencesEdit.putInt("adSizeItemId", (int)spinner.getSelectedItemId());
                preferencesEdit.commit();
            }
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

        final EditText unitIdEdit = (EditText) findViewById(R.id.gns_sample_unitid_edit);
        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);

        String defaultZoneID = preferences.getString("unitId", default_ad_unit_id);
        unitIdEdit.setText(defaultZoneID);

        spinner.setSelection(preferences.getInt("adSizeItemId", 0));

        mLoadRequestBtn = (Button)findViewById(R.id.gns_sample_load_button);
        mLoadRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String unitId = unitIdEdit.getText().toString();
                if (unitId.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Missing unit id", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
                SharedPreferences.Editor preferencesEdit = preferences.edit();
                preferencesEdit.putString("unitId", unitId);
                preferencesEdit.commit();

                Intent intent = new Intent();
                intent.setClassName(getPackageName(), "jp.co.geniee.samples.swipe.admob.SwipeActivity");
                intent.putExtra("unitId", unitId);
                intent.putExtra("adSize", mAdSize);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

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
