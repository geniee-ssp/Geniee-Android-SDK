package jp.co.geniee.samples.gnadsamplenativead;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

public class NativeAdSampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ad);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, NativeAdSamplePrefsFragment.newInstance())
                    .commitNow();
        }
    }

}