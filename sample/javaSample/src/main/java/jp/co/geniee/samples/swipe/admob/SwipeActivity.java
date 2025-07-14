package jp.co.geniee.samples.swipe.admob;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.MobileAds;

public class SwipeActivity extends Activity {
    ViewPager viewPager;
    OriginalPagerAdapter mAdapter;
    private static final String default_ad_unit_id = "ca-app-pub-3940256099942544/9214589741";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this);

        // PagerAdapter を生成
        mAdapter = new OriginalPagerAdapter(this);
        mAdapter.add(Color.BLUE);
        mAdapter.add(Color.RED);
        mAdapter.add(Color.YELLOW);
        mAdapter.add(Color.GREEN);
        mAdapter.add(0);
        mAdapter.add(Color.BLACK);

        Intent intent = getIntent();
        mAdapter.setUnitId(intent.getStringExtra("unitId"));
        mAdapter.setAdSize(intent.getStringExtra("adSize"));

        // ViewPager を生成
        viewPager = new ViewPager(this);
        viewPager.setAdapter(mAdapter);
        //右スクロール対応のために最終ページに飛ぶ
        viewPager.setCurrentItem(mAdapter.getCount());
        //メモリ展開ページを増やす
//        viewPager.setOffscreenPageLimit(mAdapter.getCount());
        setContentView(viewPager);
        //openDebugMenu();
    }

    //TODO onConfigurationChangedメソッドを追加してください
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mAdapter.configurationChanged();

        super.onConfigurationChanged(newConfig);
    }


    public void openDebugMenu() {
        MobileAds.openDebugMenu(this, default_ad_unit_id);

    }

}
