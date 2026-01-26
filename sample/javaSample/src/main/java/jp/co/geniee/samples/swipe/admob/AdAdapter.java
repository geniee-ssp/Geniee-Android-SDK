package jp.co.geniee.samples.swipe.admob;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import jp.co.geniee.samples.R;

import static com.google.android.gms.ads.AdRequest.ERROR_CODE_INTERNAL_ERROR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_INVALID_REQUEST;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NETWORK_ERROR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL;

public class AdAdapter {
    private Activity mActivity;

    private RelativeLayout mAdLayout;

    private AdRequest mAdRequest;

    private AdView mAdView;

    private String mUnitId = "";
    private String mAdSize = "";

    private final int mTabletLargeRequestSize = 960;
    private final int mTabletSmallRequestSize = 640;

    private final String mLogTag = "AdAdapter";

    public AdAdapter(Activity activity, String unitId, String adSize) {
        mActivity = activity;

        //AdViewを貼りつけるRelativeLayout
        mAdLayout = new RelativeLayout(mActivity);
        mAdView = new AdView(mActivity);

        mAdLayout.addView(mAdView);

        mUnitId = unitId;
        mAdSize = adSize;

        //AdLoaderを作成
        buildAdLoader();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    public void buildAdLoader() {
        //AdLoaderをビルドする
        Log.d(mLogTag, "buildAdLoader: S");
        Log.d(mLogTag, "buildAdLoader: UnitID > " + mUnitId);
        Point point = new Point();
        mActivity.getWindowManager().getDefaultDisplay().getSize(point);

        AdSize adSize = AdSize.BANNER;
        if (mAdSize.equals("300x250")) {
            adSize = AdSize.MEDIUM_RECTANGLE;
        } else if (mAdSize.equals("320x100")) {
            adSize = AdSize.LARGE_BANNER;
        }
        Log.d(mLogTag, "mAdSize=" + mAdSize);
        if (mAdView.getAdSize() == null) {
            mAdView.setAdSize(adSize);
        }

        //loadAdする際に使用するAdRequestを作成
        createAdRequest();

        if (mAdView.getAdUnitId() == null) {
            mAdView.setAdUnitId(mUnitId);
        }
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(mLogTag, "onAdLoaded: S");
                Log.d(mLogTag, "onAdViewLoaded: AdSize > " + mAdView.getAdSize());

                //レイアウトに貼り付け
                Log.d(mLogTag, "onAdLoaded: E");
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                //広告ロード時にエラーが発生する場合のクラス（string.xmlで文言を設定している）
                switch (loadAdError.getCode()) {
                    case ERROR_CODE_INTERNAL_ERROR:
                        Log.w(mLogTag, "onAdFailedToLoad: ErrorMSG > " + mActivity.getResources().getString(R.string.INTERNAL_ERROR));
                        break;
                    case ERROR_CODE_INVALID_REQUEST:
                        Log.w(mLogTag, "onAdFailedToLoad: ErrorMSG > " + mActivity.getResources().getString(R.string.INVALID_REQUEST));
                        break;
                    case ERROR_CODE_NETWORK_ERROR:
                        Log.w(mLogTag, "onAdFailedToLoad: ErrorMSG > " + mActivity.getResources().getString(R.string.NETWORK_ERROR));
                        break;
                    case ERROR_CODE_NO_FILL:
                        Log.w(mLogTag, "onAdFailedToLoad: ErrorMSG > " + mActivity.getResources().getString(R.string.NO_FILL));
                        break;
                }
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        Log.d(mLogTag, "buildAdLoader: E");

    }

    public void createAdRequest() {
        Log.d(mLogTag, "createAdRequest: S");
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        //adRequestBuilder.addCustomTargeting(FULL_CREEN_AD_SIZE, mFullscreen_KeyValue);

        /*
        子ども向け配信を管理するタグの設定となる。詳細は下記URLで解説がある。
        https://developers.google.com/mobile-ads-sdk/docs/dfp/android/mediation
        */
        //adRequestBuilder.tagForChildDirectedTreatment(true);

        /*
        他のターゲット設定を確認する場合、下記URLに詳細が記載されている。
        https://developers.google.com/mobile-ads-sdk/docs/dfp/android/targeting
         */

        mAdRequest = adRequestBuilder.build();

        Log.d(mLogTag, "createAdRequest: E");
    }


    public void adLoad() {
        //広告がLoadされていなければloadAdする
        Log.d(mLogTag, "adLoad: S");

        mAdView.loadAd(mAdRequest);

        Log.d(mLogTag, "adLoad: E");
    }

    public void destroyView() {
        //広告用のRelativeLayoutを初期化する
        Log.d(mLogTag, "destroyView: S");
        if (mAdLayout != null) {
            ViewGroup parent = (ViewGroup) mAdLayout.getParent();
            if (parent != null) {
                parent.removeView(mAdLayout);
            }
            mAdLayout.removeAllViews();
            mAdView.destroy();
        }

        Log.d(mLogTag, "destroyView: E");
    }

    public void addAdView(ViewGroup parentView) {
        //mAdLayoutをrelativeLayoutにaddViewする
        Log.d(mLogTag, "adAdView: S");
        parentView.addView(mAdLayout);
        adLoad();
        Log.d(mLogTag, "adAdView: E");
    }

    //タブレットか判定
    public Boolean isTablet() {
        Boolean isTablet = false;
        Point point = new Point();
        mActivity.getWindowManager().getDefaultDisplay().getSize(point);
        float windowDpSizeX = convertPxToDp(point.x);
        float windowDpSizeY = convertPxToDp(point.y);

        if (isPortrait()) {
            if (windowDpSizeX >= mTabletSmallRequestSize && windowDpSizeY >= mTabletLargeRequestSize)
                isTablet = true;
        } else {
            if (windowDpSizeX >= mTabletLargeRequestSize && windowDpSizeY >= mTabletSmallRequestSize)
                isTablet = true;
        }
        return isTablet;
    }

    private float convertPxToDp(int px) {
        DisplayMetrics metrics = mActivity.getResources().getDisplayMetrics();
        return px / metrics.density;
    }

    //縦画面か判定
    public boolean isPortrait() {
        Resources resources = mActivity.getResources();
        Configuration config = resources.getConfiguration();
        return config.orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public void configurationChanged() {
        //広告用のRelativeLayoutを初期化する
        Log.d(mLogTag, "configurationChanged: S");
        if (mAdLayout != null) {
            mAdLayout.removeAllViews();
        }

        //AdLoaderを作成
        buildAdLoader();
        //縦横の変更を検知して再ロード
        adLoad();

        Log.d(mLogTag, "configurationChanged: E");
    }


}
