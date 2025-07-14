package jp.co.geniee.samples.swipe.fullsize;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.co.geniee.samples.R;

public class MainActivity extends Activity {
    ViewPager viewPager;
    OriginalPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this);

        /*
        デバッグ・テスト用（本番時は必ず本番ように戻す必要がある）
        デバッグ・テスト時にTestDeviceの宣言をせずに何度も広告を表示した場合、AdMobの規約違反でその端末で広告が表示されなくなる可能性があるため、
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

        // PagerAdapter を生成
        mAdapter = new OriginalPagerAdapter(this);
        mAdapter.add(Color.BLUE);
        mAdapter.add(Color.RED);
        mAdapter.add(Color.YELLOW);
        mAdapter.add(Color.GREEN);
        mAdapter.add(0);
        mAdapter.add(Color.BLACK);

        // ViewPager を生成
        viewPager = new ViewPager(this);
        viewPager.setAdapter(mAdapter);
        //右スクロール対応のために最終ページに飛ぶ
        viewPager.setCurrentItem(mAdapter.getCount());
        //メモリ展開ページを増やす
//        viewPager.setOffscreenPageLimit(mAdapter.getCount());
        setContentView(viewPager);
    }

    //TODO onConfigurationChangedメソッドを追加してください
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mAdapter.configurationChanged();

        super.onConfigurationChanged(newConfig);
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
