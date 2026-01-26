package jp.co.geniee.samples.scrollbanner.inview;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import jp.co.geniee.gnadsdk.banner.GNAdSize;
import jp.co.geniee.gnadsdk.banner.GNAdView;
import jp.co.geniee.gnadsdk.banner.GNTouchType;
import jp.co.geniee.gnadsdk.common.GNAdLogger;
import jp.co.geniee.samples.R;

public class TestFragment extends Fragment {
    private GNAdView adView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // 先ほどのレイアウトをここでViewとして作成します
        return inflater.inflate(R.layout.fragment_subwindow, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("GNAdSampleBanner", "onCreate");

        //WebViewデバッグを有効にする
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        view.findViewById(R.id.refreshbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adView.startLoadNewAd();
            }
        });


        adView = new GNAdView(
                getContext(),
                GNAdSize.W300H250,
                GNTouchType.TAP_AND_FLICK
        );
        adView.setAppId("1573195");
        adView.setLogPriority(GNAdLogger.INFO);
        //adView.setGeoLocationEnable(true);
        // Add AdView to view layer
        final LinearLayout layout = (LinearLayout)view.findViewById(R.id.AdviewLayout);
        layout.addView(adView, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        adView.startAdLoop();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("GNAdSampleBanner", "onResume");
        if(adView != null){
            //adView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("GNAdSampleBanner", "onPause");
        if(adView != null){
            //adView.pause();
        }
    }

    @Override
    public void onDestroy() {
        Log.d("GNAdSampleBanner", "onDestroy");
        if (null != adView) {
            //adView.destroy();
        }
        super.onDestroy();
    }


}
