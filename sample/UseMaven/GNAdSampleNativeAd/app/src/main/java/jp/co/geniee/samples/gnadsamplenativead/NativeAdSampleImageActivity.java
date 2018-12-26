package jp.co.geniee.samples.gnadsamplenativead;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.co.geniee.gnadsdk.common.GNSException;
import jp.co.geniee.samples.gnadsamplenativead.list.GNQueue;
import jp.co.geniee.samples.gnadsamplenativead.list.MemCache;
import jp.co.geniee.samples.gnadsamplenativead.list.MyCellData;
import jp.co.geniee.sdk.ads.nativead.GNNativeAd;
import jp.co.geniee.sdk.ads.nativead.GNNativeAdRequest;
import jp.co.geniee.sdk.ads.nativead.GNNativeAdRequestListener;

public class NativeAdSampleImageActivity extends ListActivity {
    private  String TAG = "NativeAdSampleImageActivity";
    private GNNativeAdRequest nativeAdRequest;
    private boolean loading = false;
    private GNQueue queueAds = new GNQueue(100);
    private ArrayList<Object> cellDataList = new ArrayList<Object>();
    private long timeStart, timeEnd;
    private ListViewAdapter mAdapter;
    private OnScrollListener mScrollListener;
    private OnItemClickListener mClickListener;
    private View mFooter;
    private String zoneId;

    private GNNativeAdRequestListener nativeListener = new GNNativeAdRequestListener() {
        @Override
        public void onNativeAdsLoaded(GNNativeAd[] nativeAds) {
            timeEnd = System.currentTimeMillis();
            Log.i(TAG,"NativeAds loaded in seconds:" + ((timeEnd - timeStart)/1000.0));
            for (GNNativeAd ad : nativeAds) {
                if (ad.hasVideoContent()) continue;
                queueAds.enqueue(ad);
            }
            if (queueAds.size() == 0) {
                Toast.makeText(getApplicationContext(), "There is no zone for still images.", Toast.LENGTH_SHORT).show();
            }
            createCellDataList();
        }

        @Override
        public void onNativeAdsFailedToLoad(GNSException e) {
            Log.w(TAG,"onNativeAdsFailedToLoad Err " + e.getCode() + ":" + e.getMessage());
            Toast.makeText(getApplicationContext(), "Err " + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onShouldStartInternalBrowserWithClick(String landingURL) {
            Log.i(TAG,"onShouldStartInternalBrowserWithClick : " + landingURL);
            return false;
        }

    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ad_sample);
        Log.d(TAG, "onCreate");
        ListView listView = getListView();
        listView.addFooterView(getFooter());
        listView.setAdapter(getAdapter());
        listView.setOnScrollListener(getScrollListener());
        listView.setOnItemClickListener(getClickListener());
        timeStart = System.currentTimeMillis();
        Intent intent = getIntent();
        zoneId = intent.getStringExtra(NativeAdSampleActivity.extraZoneId);
        Log.i(TAG, "zoneId=" + zoneId);
        try {
            // Initialize SDK GNNativeAdRequest
            nativeAdRequest = new GNNativeAdRequest(this, zoneId);
            nativeAdRequest.setAdListener(nativeListener);
            //nativeAdRequest.setGeoLocationEnable(true);
            if (zoneId.split(",").length > 1) {
                nativeAdRequest.multiLoadAds(this);
            } else {
                nativeAdRequest.loadAds(this);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Err " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void createCellDataList() {
        int totalCnt = 20;
        int adCnt = queueAds.size();
        for (int i = 0; i < totalCnt; i++) {
            if (queueAds.size() > 0 &&  adCnt > 0 && totalCnt / adCnt > 0 && i % (totalCnt / adCnt) == 0) {
                Object ad = queueAds.dequeue();
                cellDataList.add(ad);
            } else {
                cellDataList.add(new MyCellData());
            }
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private OnScrollListener getScrollListener() {
        if (mScrollListener == null) {
            mScrollListener = new OnScrollListener() {
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (totalItemCount-1 > 0 && totalItemCount-1 == cellDataList.size() && !loading && 
                            totalItemCount == firstVisibleItem + visibleItemCount) {
                        Log.d(TAG, "load additional list cells");
                        try {
                            if (zoneId.split(",").length > 1) {
                                nativeAdRequest.multiLoadAds(NativeAdSampleImageActivity.this);
                            } else {
                                nativeAdRequest.loadAds(NativeAdSampleImageActivity.this);
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Err " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        nativeAdRequest.setAdListener(nativeListener);
                    }
                }
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {}
            };
        }
        return mScrollListener;
    }
    
    private OnItemClickListener getClickListener() {
        if (mClickListener == null) {
            mClickListener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListView listView = (ListView)parent;
                    Object cell = listView.getItemAtPosition(position);
                    if (cell instanceof GNNativeAd) {
                        // Report SDK click
                        ((GNNativeAd)cell).onTrackingClick();
                    }
                }
                
            };
        }
        return mClickListener;
    }

    private ListAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new ListViewAdapter(this, cellDataList);
        }
        return mAdapter;
    }

    private View getFooter() {
        if (mFooter == null) {
            mFooter = getLayoutInflater().inflate(R.layout.listview_footer, null);
        }
        return mFooter;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }
    
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        MemCache.clear();
        System.gc();
        super.onDestroy();
    }
    class ListViewAdapter extends ArrayAdapter<Object> {
        private LayoutInflater inflater;

        public ListViewAdapter(Context context,  List<Object> list) {
            super(context, 0, list);
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        class ViewHolder {
            TextView textView;
            ImageView imageView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListViewAdapter.ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item, null);
                holder = new ListViewAdapter.ViewHolder();
                holder.textView = (TextView) convertView.findViewById(R.id.textView);
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                convertView.setTag(holder);
            } else {
                holder = (ListViewAdapter.ViewHolder) convertView.getTag();
            }
            Log.i(TAG, "position=" + position);
            Object cell = getItem(position);
            // Rendering SDK NativeAd content
            if (cell instanceof GNNativeAd) {
                holder.textView.setText(((GNNativeAd)cell).title);
                holder.imageView.setTag(((GNNativeAd)cell).icon_url);
                new ImageGetTask(holder.imageView, ((GNNativeAd)cell).icon_url).execute();
                // Report SDK impression
                ((GNNativeAd)cell).onTrackingImpression();
            } else {
                holder.textView.setText(((MyCellData)cell).title + "No." + position);
                holder.imageView.setTag(((MyCellData)cell).imgURL);
                new ImageGetTask(holder.imageView, ((MyCellData)cell).imgURL).execute();
            }
            return convertView;
        }
    }

    class ImageGetTask extends AsyncTask<Void,Void,Bitmap> {
        protected String url;
        protected Context context;
        protected ImageView imageView;
        private String tag;
        public ImageGetTask(ImageView imageView, String url) {
            this.imageView = imageView;
            this.url = url;
            tag = imageView.getTag().toString();
        }

        @Override
        public void onPreExecute() {
            Bitmap bitmap = MemCache.getImage(url);
            if (bitmap != null) {
                if (tag.equals(imageView.getTag())) {
                    imageView.setImageBitmap(bitmap);
                }
                cancel(true);
                return;
            }
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap;
            try {
                URL imageUrl = new URL(url);
                InputStream input;
                input = imageUrl.openStream();
                bitmap = BitmapFactory.decodeStream(input);
                input.close();
                MemCache.setImage(url, bitmap);
                return bitmap;
            } catch (MalformedURLException e) {
                return null;
            } catch (IOException e) {
                return null;
            }
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (tag.equals(imageView.getTag())) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

}

