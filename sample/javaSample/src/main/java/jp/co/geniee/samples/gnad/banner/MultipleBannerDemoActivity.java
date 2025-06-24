package jp.co.geniee.samples.gnad.banner;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import jp.co.geniee.samples.R;
import jp.co.geniee.samples.SharedPreferenceManager;
import jp.co.geniee.samples.gnad.common.CellData;
import jp.co.geniee.samples.gnad.common.GNQueue;
import jp.co.geniee.samples.gnad.common.MemCache;
import jp.co.geniee.gnadsdk.banner.GNAdView;
import jp.co.geniee.gnadsdk.banner.GNAdViewRequest;
import jp.co.geniee.gnadsdk.banner.GNAdViewRequestListener;

public class MultipleBannerDemoActivity extends AppCompatActivity implements GNAdViewRequestListener {

    private final String TAG = "MultipleBannerDemo";

    private Context mContext;

    private boolean loading = false;
    private GNQueue queueAds = new GNQueue(100);

    private GNAdViewRequest multiAdViewRequest;

    private Button btLoadMultiBannerAd;
    private EditText edtZoneId;

    private MultipleBannersListViewAdapter mAdapter;
    private ArrayList<Object> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_banner_demo);

        mContext = this;

        edtZoneId = findViewById(R.id.edtZoneId);
        edtZoneId.setText(SharedPreferenceManager.getInstance(mContext).getString(SharedPreferenceManager.MULTIPLE_BANNERS_ZONE_ID));
        btLoadMultiBannerAd = findViewById(R.id.btLoadMultiBannerAd);

        btLoadMultiBannerAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initializeAd();
                requestCellDataListAsync();
            }
        });

        mAdapter = new MultipleBannersListViewAdapter(this, mDataList);
        ListView listView = findViewById(android.R.id.list);
        listView.setAdapter(mAdapter);
        listView.addFooterView(getLayoutInflater().inflate(R.layout.listview_footer, null));

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount - 1 > 0 && totalItemCount - 1 == mDataList.size() && !loading && totalItemCount == firstVisibleItem + visibleItemCount) {
                    multiAdViewRequest.loadAds(MultipleBannerDemoActivity.this);
                    requestCellDataListAsync();
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MemCache.clear();
        System.gc();
    }

    private void initializeAd() {

        try {
            // Initialize SDK GNAdViewRequest
            // multiple zone id can be given, separated by commas
            //multiAdViewRequest = new GNAdViewRequest(this, "ZONE_ID_1,ZONE_ID_2,ZONE_ID_3,...,ZONE_ID_10");
            multiAdViewRequest = new GNAdViewRequest(this, edtZoneId.getText().toString());
            multiAdViewRequest.setAdListener(this);
            //multiAdViewRequest.setGeoLocationEnable(true);
            //multiAdViewRequest.setLogPriority(GNAdLogger.INFO);
            multiAdViewRequest.loadAds(this);

            SharedPreferenceManager.getInstance(mContext).putString(SharedPreferenceManager.MULTIPLE_BANNERS_ZONE_ID, edtZoneId.getText().toString());

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
                mDataList.add(ad);
            } else {
                mDataList.add(new CellData());
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGNAdViewsLoaded(GNAdView[] gnAdViews) {

        if (gnAdViews != null && gnAdViews.length > 0) {
            for (GNAdView adView : gnAdViews) {
                queueAds.enqueue(adView);
            }

            Toast.makeText(this, "Multiple banner ads received, num of ads = " + gnAdViews.length, Toast.LENGTH_LONG).show();
            Log.d(TAG, "Multiple banner ads received, num of ads = " + gnAdViews.length);
        }
    }

    @Override
    public void onGNAdViewsFailedToLoad() {
        Log.d(TAG, "onGNAdViewsFailedToLoad");
    }

    @Override
    public boolean onShouldStartInternalBrowserWithClick(String s) {
        return false;
    }
}
