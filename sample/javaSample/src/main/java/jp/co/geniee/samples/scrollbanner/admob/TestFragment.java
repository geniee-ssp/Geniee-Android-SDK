package jp.co.geniee.samples.scrollbanner.admob;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import jp.co.geniee.samples.R;

public class TestFragment extends Fragment {
    private final String TAG = "TestFragment";
    private AdView mAdView;
    private AdRequest mAdRequest;
    private String mUnitId = "";
    private String mAdSize = "";

    public void setUnitId(String unitId) {
        mUnitId = unitId;
    }
    public void setAdSize(String adSize) {
        mAdSize = adSize;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        mAdRequest = adRequestBuilder.build();
        mAdView = new AdView(getContext());
        // 先ほどのレイアウトをここでViewとして作成します
        return inflater.inflate(R.layout.fragment_subwindow, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onCreate");

        //WebViewデバッグを有効にする
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        view.findViewById(R.id.refreshbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdView.loadAd(mAdRequest);
            }
        });

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded: ");
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Code to be executed when an ad request fails.
                Log.d(TAG, "onAdFailedToLoad: " + loadAdError.getCode());
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened: ");
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClosed() {
                Log.d(TAG, "onAdClosed: ");
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        final LinearLayout layout = (LinearLayout) view.findViewById(R.id.AdviewLayout);
        layout.addView(mAdView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }
    public void load() {
        AdSize adSize = AdSize.BANNER;
        if (mAdSize.equals("300x250")) {
            adSize = AdSize.MEDIUM_RECTANGLE;
        } else if (mAdSize.equals("320x100")) {
            adSize = AdSize.LARGE_BANNER;
        }
        Log.d(TAG, "mAdSize=" + mAdSize);
        if (mAdView.getAdSize() == null) {
            mAdView.setAdSize(adSize);
        }
        if (mAdView.getAdUnitId() == null) {
            mAdView.setAdUnitId(mUnitId);
        }
        mAdView.loadAd(mAdRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mAdView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        mAdView.pause();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        mAdView.destroy();

        super.onDestroy();
    }


}
