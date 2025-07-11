package jp.co.geniee.samples.scrollbanner.admob;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import jp.co.geniee.samples.R;

public class GNAdAdMobSampleScrollBanner extends AppCompatActivity {
    TestFragment fragment1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragmentbase);

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

        // Fragmentを作成します
        fragment1 = new TestFragment();
        TestFragment2 fragment2 = new TestFragment2();
        TestFragment2 fragment3 = new TestFragment2();

        // Fragmentの追加や削除といった変更を行う際は、Transactionを利用します
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragmentlayout1, fragment2);
        transaction.add(R.id.fragmentlayout2, fragment1);
        transaction.add(R.id.fragmentlayout3, fragment3);
        transaction.commit();

    }
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        fragment1.setUnitId(intent.getStringExtra("unitId"));
        fragment1.setAdSize(intent.getStringExtra("adSize"));
        fragment1.load();
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
