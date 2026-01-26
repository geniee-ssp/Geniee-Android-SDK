package jp.co.geniee.samples;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import jp.co.geniee.gnadsdk.common.GNAdConstants;

public class SplashActivity extends AppCompatActivity {

    private final Handler mHandler = new Handler();
    private TextView tvSDKVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        tvSDKVersion = findViewById(R.id.tvSDKVersion);
        tvSDKVersion.setText(String.format("SDK version: %s", GNAdConstants.GN_CONST_VERSION));

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 2000);
    }
}
