package jp.co.geniee.samples.gnad.nativead;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import jp.co.geniee.samples.R;
import jp.co.geniee.samples.SharedPreferenceManager;
import jp.co.geniee.samples.gnad.common.CellData;
import jp.co.geniee.samples.gnad.common.GNQueue;
import jp.co.geniee.samples.gnad.common.MemCache;
import jp.co.geniee.gnadsdk.common.GNAdLogger;
import jp.co.geniee.gnadsdk.common.GNSException;
import jp.co.geniee.sdk.ads.nativead.GNNativeAd;
import jp.co.geniee.sdk.ads.nativead.GNNativeAdRequest;
import jp.co.geniee.sdk.ads.nativead.GNNativeAdRequestListener;

public class NativeDemoActivity extends AppCompatActivity {

    private static final String TAG = "[GNS]NativeDemoActivity";
    private Context mContext;
    private boolean loading = false;
    private GNQueue queueAds = new GNQueue(100);
    private EditText edtZoneId;
    private Button btLoadNativeAd;
    private NativeAdListViewAdapter mAdapter;
    private ArrayList<Object> cellDataList = new ArrayList<>();
    private GNNativeAdRequest mNativeAdRequest;

    private GNNativeAdRequestListener nativeListener = new GNNativeAdRequestListener() {
        @Override
        public void onNativeAdsLoaded(GNNativeAd[] gnNativeAds) {
            for (final GNNativeAd ad : gnNativeAds) {
                queueAds.enqueue(ad);
            }
            Log.i(TAG, "Native ad has been received");
        }
        @Override
        public void onNativeAdsFailedToLoad(GNSException e) {
            Log.w(TAG, "onNativeAdsFailedToLoad Err " + e.getCode() + ":" + e.getMessage());
        }
        @Override
        public boolean onShouldStartInternalBrowserWithClick(String landingURL) {
            Log.i(TAG, "onShouldStartInternalBrowserWithClick : " + landingURL);
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_demo);

        mContext = this;

        edtZoneId = findViewById(R.id.edtZoneId);
        edtZoneId.setText(SharedPreferenceManager.getInstance(mContext).getString(SharedPreferenceManager.NATIVE_AD_ZONE_ID));
        btLoadNativeAd = findViewById(R.id.btLoadNativeAd);

        mAdapter = new NativeAdListViewAdapter(mContext, cellDataList);
        ListView listView = findViewById(android.R.id.list);
        listView.setAdapter(mAdapter);
        listView.addFooterView(getLayoutInflater().inflate(R.layout.listview_footer, null));
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ListView listView = (ListView) adapterView;
                Object cell = listView.getItemAtPosition(position);
                if (cell instanceof GNNativeAd) {
                    // Report SDK click
                    ((GNNativeAd) cell).onTrackingClick();
                }
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount - 1 > 0 && totalItemCount - 1 == cellDataList.size() && !loading && totalItemCount == firstVisibleItem + visibleItemCount) {
                    mNativeAdRequest.loadAds(NativeDemoActivity.this);
                    requestCellDataListAsync();
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {

            }
        });


        btLoadNativeAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadNativeAd();
                requestCellDataListAsync();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MemCache.clear();
        System.gc();
    }

    private void loadNativeAd() {
        try {
            String zoneId = edtZoneId.getText().toString();
            // Initialize SDK GNNativeAdRequest
            mNativeAdRequest = new GNNativeAdRequest(mContext, zoneId);
            mNativeAdRequest.setAdListener(nativeListener);
            //nativeAdRequest.setGeoLocationEnable(true);
            mNativeAdRequest.setLogPriority(GNAdLogger.INFO);
            mNativeAdRequest.loadAds(NativeDemoActivity.this);
            if (zoneId.split(",").length > 1) {
                mNativeAdRequest.multiLoadAds(this);
            } else {
                mNativeAdRequest.loadAds(this);
            }
            SharedPreferenceManager.getInstance(mContext).putString(SharedPreferenceManager.NATIVE_AD_ZONE_ID, zoneId);
        } catch (Exception e) {
            edtZoneId.setError(e.getLocalizedMessage());
        }
    }

    private void requestCellDataListAsync() {
        loading = true;
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                createCellDataList();
                loading = false;
            }
        }, 1000);
    }

    private void createCellDataList() {
        for (int i = 0; i < 5; i++) {
            if (queueAds.size() > 0) {
                Object ad = queueAds.dequeue();
                cellDataList.add(ad);
            } else {
                cellDataList.add(new CellData());
            }
        }
        mAdapter.notifyDataSetChanged();
    }

}
