package jp.co.geniee.samples;

import android.app.Application;
import android.provider.Settings;
import android.util.Log;

import com.google.android.libraries.ads.mobile.sdk.MobileAds;
import com.google.android.libraries.ads.mobile.sdk.common.RequestConfiguration;
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

public class SampleApplication extends Application {
    private static final String TAG = "SampleApplication";

    // AdMob app ID of the Geniee sample app (Google Mediation screens).
    public static final String GMA_APP_ID = "ca-app-pub-2963406282877816~5372623241";

    @Override
    public void onCreate() {
        super.onCreate();
        initializeMobileAds();
    }

    // GMA Next-Gen SDK must be initialized once, at app launch, on a background thread,
    // before loading any ad (including Geniee ads mediated through Google Ad Manager).
    private void initializeMobileAds() {
        new Thread(() -> {
            // For debugging/testing only: register this device as a test device.
            // Remove this setting in production.
            RequestConfiguration requestConfiguration = new RequestConfiguration.Builder()
                    .setTestDeviceIds(Collections.singletonList(testDeviceId()))
                    .build();
            MobileAds.initialize(this, new InitializationConfig.Builder(GMA_APP_ID)
                    .setRequestConfiguration(requestConfiguration)
                    .build());
            Log.d(TAG, "GMA Next-Gen SDK initialized: " + MobileAds.isInitialized());
        }).start();
    }

    private String testDeviceId() {
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return md5(androidId != null ? androidId : "").toUpperCase();
    }

    private static String md5(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] messageDigest = digest.digest(s.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                hexString.append(String.format("%02x", b & 0xFF));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }
}
