package jp.co.geniee.samples.swipe.inview;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.MobileAds;

public class MainActivity extends Activity {
    ViewPager viewPager;
    OriginalPagerAdapter mAdapter;

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

        // ViewPager を生成
        viewPager = new ViewPager(this);
        viewPager.setAdapter(mAdapter);
        //右スクロール対応のために最終ページに飛ぶ
        //viewPager.setCurrentItem(mAdapter.getCount());
        //メモリ展開ページを増やす
        viewPager.setOffscreenPageLimit(mAdapter.getCount());
        setContentView(viewPager);
    }

    @Override
    protected void onDestroy() {
        Log.d("test", "onDestroy: ");
        mAdapter.destroyAdaper();
        super.onDestroy();
    }

    /*
    //TODO onConfigurationChangedメソッドを追加してください
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mAdapter.configurationChanged();

        super.onConfigurationChanged(newConfig);
    }

     */

}
