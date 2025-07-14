package jp.co.geniee.samples.swipe.inview;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;

import jp.co.geniee.gnadsdk.banner.GNAdSize;
import jp.co.geniee.gnadsdk.banner.GNAdView;
import jp.co.geniee.gnadsdk.banner.GNTouchType;
import jp.co.geniee.gnadsdk.common.GNAdLogger;

import static com.google.android.gms.ads.AdRequest.ERROR_CODE_INTERNAL_ERROR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_INVALID_REQUEST;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NETWORK_ERROR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL;

public class AdAdapter {

    private Activity mActivity;
    private RelativeLayout mAdLayout;

    private GNAdView mAdView;

    private final String mLogTag = "AdAdapter";


    public AdAdapter(Activity activity, String unit_id) {
        mActivity = activity;

        mAdView = new GNAdView(
                mActivity,
                GNAdSize.W300H250,
                GNTouchType.TAP_AND_FLICK
        );
        mAdView.setAppId("1573195");
        mAdView.setLogPriority(GNAdLogger.INFO);

        //AdViewを貼りつけるRelativeLayout
        mAdLayout = new RelativeLayout(mActivity);
        mAdLayout.addView(mAdView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);

        mAdView.startAdLoop();
    }

    public void destroyView() {
        //広告用のRelativeLayoutを初期化する
        Log.d(mLogTag, "destroyView: S");

        if(mAdLayout != null){
            mAdView.clearAdView();
            ViewGroup parent = (ViewGroup) mAdLayout.getParent();
            //parent.removeView(mAdLayout);
            //mAdLayout.removeAllViews();
        }

        Log.d(mLogTag, "destroyView: E");
    }

    public void addAdView(ViewGroup parentView) {
        //mAdLayoutをrelativeLayoutにaddViewする
        Log.d(mLogTag, "adAdView: S");
        parentView.addView(mAdLayout);
        Log.d(mLogTag, "adAdView: E");
    }

}
