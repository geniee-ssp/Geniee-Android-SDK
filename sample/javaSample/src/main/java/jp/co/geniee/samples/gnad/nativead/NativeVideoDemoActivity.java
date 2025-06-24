package jp.co.geniee.samples.gnad.nativead;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.co.geniee.samples.R;
import jp.co.geniee.samples.SharedPreferenceManager;
import jp.co.geniee.samples.gnad.common.CellData;
import jp.co.geniee.samples.gnad.common.GNQueue;
import jp.co.geniee.samples.gnad.common.MemCache;
import jp.co.geniee.gnadsdk.common.GNSException;
import jp.co.geniee.sdk.ads.nativead.GNNativeAd;
import jp.co.geniee.sdk.ads.nativead.GNNativeAdRequest;
import jp.co.geniee.sdk.ads.nativead.GNNativeAdRequestListener;
import jp.co.geniee.sdk.ads.nativead.GNSNativeVideoPlayerListener;
import jp.co.geniee.sdk.ads.nativead.GNSNativeVideoPlayerView;

public class NativeVideoDemoActivity extends AppCompatActivity {
    private String TAG = "[GNS]NativeVideoDemoActivity";
    private GNNativeAdRequest nativeAdRequest;
    private boolean loading = false;
    private GNQueue queueAds = new GNQueue(100);
    private EditText edtZoneId;
    private Button btLoadAd;
    private ArrayList<Object> cellDataList = new ArrayList<Object>();
    private long timeStart, timeEnd;
    private ListViewAdapter mAdapter;
    private AbsListView.OnScrollListener mScrollListener;
    private AdapterView.OnItemClickListener mClickListener;
    private View mFooter;
    private List<GNSNativeVideoPlayerView> videoViews;
    private String zoneId;
    private float scale;
    private int videoViewMaxWidth;
    private int videoViewMaxHeight;

    private GNNativeAdRequestListener nativeListener = new GNNativeAdRequestListener() {
        @Override
        public void onNativeAdsLoaded(GNNativeAd[] nativeAds) {
            timeEnd = System.currentTimeMillis();
            Log.i(TAG,"NativeAds loaded in seconds:" + (timeEnd - timeStart)/1000.0);
            Log.i(TAG,"nativeAds.length=" + nativeAds.length);
            for (final GNNativeAd ad : nativeAds) {
                if (ad != null){
                    Log.i(TAG,"zoneID=" + ad.zoneID);
                    Log.i(TAG,"advertiser=" + ad.advertiser);
                    Log.i(TAG,"title=" + ad.title);
                    Log.i(TAG,"description=" + ad.description);
                    Log.i(TAG,"cta=" + ad.cta);
                    Log.i(TAG,"icon_aspectRatio=" + ad.icon_aspectRatio);
                    Log.i(TAG,"icon_url=" + ad.icon_url);
                    Log.i(TAG,"icon_height=" + ad.icon_height);
                    Log.i(TAG,"icon_width=" + ad.icon_width);
                    Log.i(TAG,"screenshots_aspectRatio=" + ad.screenshots_aspectRatio);
                    Log.i(TAG,"screenshots_url=" + ad.screenshots_url);
                    Log.i(TAG,"screenshots_height=" + ad.screenshots_height);
                    Log.i(TAG,"screenshots_width=" + ad.screenshots_width);
                    Log.i(TAG,"app_appName=" + ad.app_appName);
                    Log.i(TAG,"app_appid=" + ad.app_appid);
                    Log.i(TAG,"app_rating=" + ad.app_rating);
                    Log.i(TAG,"app_storeURL=" + ad.app_storeURL);
                    Log.i(TAG,"app_targetAge=" + ad.app_targetAge);
                    Log.i(TAG,"optout_text=" + ad.optout_text);
                    Log.i(TAG,"optout_image_url=" + ad.optout_image_url);
                    Log.i(TAG,"optout_url=" + ad.optout_url);
                    Log.i(TAG,"vast_xml=" + ad.vast_xml);

                    queueAds.enqueue(ad);
                }
            }
            if (queueAds.size() == 0) {
                Toast.makeText(NativeVideoDemoActivity.this, "There is no zone.", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_native_video_demo);
        videoViews = new ArrayList<GNSNativeVideoPlayerView>();
        ListView listView = findViewById(android.R.id.list);
        listView.addFooterView(getFooter());
        listView.setAdapter(getAdapter());
        listView.setOnScrollListener(getScrollListener());
        listView.setOnItemClickListener(getClickListener());
        timeStart = System.currentTimeMillis();
        scale = this.getResources().getDisplayMetrics().density;
        edtZoneId = findViewById(R.id.edtZoneId);
        edtZoneId.setText(SharedPreferenceManager.getInstance(this).getString(SharedPreferenceManager.NATIVE_AD_ZONE_ID));
        btLoadAd = findViewById(R.id.btLoadNativeAd);
        btLoadAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    zoneId = edtZoneId.getText().toString();
                    SharedPreferenceManager.getInstance(NativeVideoDemoActivity.this).putString(SharedPreferenceManager.NATIVE_AD_ZONE_ID,zoneId);
                    Log.i(TAG, "zoneId=" + zoneId);
                    // Initialize SDK GNNativeAdRequest
                    nativeAdRequest = new GNNativeAdRequest(NativeVideoDemoActivity.this, zoneId);
                    nativeAdRequest.setAdListener(nativeListener);
                    //nativeAdRequest.setGeoLocationEnable(true);
                    if (zoneId.split(",").length > 1) {
                        nativeAdRequest.multiLoadAds(NativeVideoDemoActivity.this);
                    } else {
                        nativeAdRequest.loadAds(NativeVideoDemoActivity.this);
                    }
                } catch (Exception e) {
                    Toast.makeText(NativeVideoDemoActivity.this, "Err " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createCellDataList() {
        int totalCnt = 20;
        int adCnt = queueAds.size();
        for (int i = 0; i < totalCnt; i++) {
            if (queueAds.size() > 0 &&  adCnt > 0 && totalCnt / adCnt > 0 && i % (totalCnt / adCnt) == 0) {
                Object ad = queueAds.dequeue();
                cellDataList.add(ad);
            } else {
                cellDataList.add(new CellData());
            }
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private AbsListView.OnScrollListener getScrollListener() {
        if (mScrollListener == null) {
            mScrollListener = new AbsListView.OnScrollListener() {
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (totalItemCount-1 > 0 && totalItemCount-1 == cellDataList.size() && !loading &&
                            totalItemCount == firstVisibleItem + visibleItemCount) {
                        Log.d(TAG, "load additional list cells");
                        try {
                            if (zoneId.split(",").length > 1) {
                                nativeAdRequest.multiLoadAds(NativeVideoDemoActivity.this);
                            } else {
                                nativeAdRequest.loadAds(NativeVideoDemoActivity.this);
                            }
                        } catch (Exception e) {
                            Toast.makeText(NativeVideoDemoActivity.this, "Err " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private AdapterView.OnItemClickListener getClickListener() {
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

        for (GNSNativeVideoPlayerView videoView : videoViews) {
            if (videoView != null) {
                videoView.resume();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        for (GNSNativeVideoPlayerView videoView : videoViews) {
            if (videoView != null) {
                videoView.pause();
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        Log.d(TAG, "videoViews.size()=" + videoViews.size());
        for (GNSNativeVideoPlayerView videoView : videoViews) {
            if (videoView != null) {
                videoView.remove();
            }
        }
        MemCache.clear();
        System.gc();
        super.onDestroy();
    }

    class ListViewAdapter extends ArrayAdapter<Object> {
        private LayoutInflater inflater;

        public ListViewAdapter(Context context, List<Object> list) {
            super(context, 0, list);
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Log.d(TAG, "ListViewAdapter");
        }

        class ViewHolder {
            LinearLayout viewGroup;
            TextView textView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ListViewAdapter.ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_video_item, null);
                holder = new ListViewAdapter.ViewHolder();
                holder.viewGroup = (LinearLayout) convertView.findViewById(R.id.viewGroup);
                holder.textView = convertView.findViewById(R.id.textView);
                convertView.setTag(holder);
            } else {
                holder = (ListViewAdapter.ViewHolder) convertView.getTag();
            }
            removeAllView(holder.viewGroup);
            final Object cell = getItem(position);
            // Rendering SDK NativeAd content
            if (cell instanceof GNNativeAd) {
                if (((GNNativeAd)cell).hasVideoContent()) {
                    Log.d(TAG, "getView: GNNativeAd(Video)" + position);
                    GNSNativeVideoPlayerView videoView = new GNSNativeVideoPlayerView(getApplicationContext());
                    videoViews.add(videoView);
                    videoView.load((GNNativeAd)cell);
                    videoView.setActivity(NativeVideoDemoActivity.this);
                    videoView.setListener(new GNSNativeVideoPlayerListener() {
                        // Sent when an video ad request succeeded.
                        @Override
                        public void onVideoReceiveSetting(GNSNativeVideoPlayerView videoView) {
                            if (videoView.isReady()) {
                                videoView.show();
                            }

                            // Decide the video size to display based on the aspect ratio
                            videoViewMaxWidth = holder.viewGroup.getWidth();
                            videoViewMaxHeight = holder.viewGroup.getHeight();
                            Log.i(TAG, "Aspect ratio =" + videoView.getMediaFileAspect() );
                            if(videoView.getMediaFileAspect() > 1) {
                                // 16:9
                                Log.i(TAG,"videoView width =" + videoViewMaxWidth);
                                Log.i(TAG,"videoView height =" + (int) (videoViewMaxWidth / videoView.getMediaFileAspect()));
                                videoView.setLayoutParams(new LinearLayout.LayoutParams(videoViewMaxWidth, (int) (videoViewMaxWidth / videoView.getMediaFileAspect())));
                            } else if(videoView.getMediaFileAspect() < 1) {
                                // 9:16
                                Log.i(TAG,"videoView width =" + (int) (videoViewMaxHeight * videoView.getMediaFileAspect()));
                                Log.i(TAG,"videoView height =" + videoViewMaxHeight);
                                videoView.setLayoutParams(new LinearLayout.LayoutParams((int) (videoViewMaxHeight * videoView.getMediaFileAspect()), videoViewMaxHeight));
                            } else {
                                // 1:1
                                Log.i(TAG,"videoView width =" + videoViewMaxHeight);
                                Log.i(TAG,"videoView height =" + videoViewMaxHeight);
                                videoView.setLayoutParams(new LinearLayout.LayoutParams(videoViewMaxHeight, videoViewMaxHeight));
                            }
                        }

                        // Sent when an video ad request failed.
                        // (Network Connection Unavailable, Frequency capping, Out of ad stock)
                        @Override
                        public void onVideoFailWithError(GNSNativeVideoPlayerView videoView, GNSException e) {
                            Log.e(TAG, "Err:" + e.getCode() + " " + e.getMessage());
                            removeAllView(holder.viewGroup);
                            createImageView(holder, (GNNativeAd)cell);
                        }
                        // When playback of video ad is started
                        @Override
                        public void onVideoStartPlaying(GNSNativeVideoPlayerView videoView) {
                            Log.i(TAG, "Ad started playing.");
                        }
                        // When playback of video ad is completed
                        @Override
                        public void onVideoPlayComplete(GNSNativeVideoPlayerView videoView) {
                            Log.i(TAG,"Ad finished playing.");
                        }

                        // When playback of video ad is closed
                        @Override
                        public void onVideoClose(GNSNativeVideoPlayerView videoView) {
                            Log.i(TAG,"Ad closed.");
                        }
                    });
                    holder.viewGroup.addView(videoView);
                } else {
                    Log.d(TAG, "getView: GNNativeAd(Image)" + position);
                    createImageView(holder, (GNNativeAd)cell);
                }

                // Report SDK impression
                ((GNNativeAd)cell).onTrackingImpression();

                holder.textView.setText(((GNNativeAd)cell).zoneID + ((GNNativeAd)cell).title + ((GNNativeAd)cell).description + " No." + position);
                holder.textView.setClickable(true);
                holder.textView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((GNNativeAd)cell).onTrackingClick();
                    }
                });
            } else {
                Log.d(TAG, "getView: MyData" + position);
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setLayoutParams(new LinearLayout.LayoutParams((int) (135 * scale + 0.5f), (int) (100 * scale + 0.5f)));
                imageView.setTag(((CellData)cell).imgURL);
                new ImageGetTask(imageView, ((CellData)cell).imgURL).execute();
                holder.viewGroup.addView(imageView);
                holder.textView.setText(((CellData)cell).title + " No." + position);
            }
            return convertView;
        }
        private void createImageView(ViewHolder holder, GNNativeAd cell) {
            ImageView imageView = new ImageView(getApplicationContext());
            imageView.setLayoutParams(new LinearLayout.LayoutParams((int) (135 * scale + 0.5f), (int) (100 * scale + 0.5f)));
            imageView.setTag(cell.icon_url);

            new ImageGetTask(imageView, cell.icon_url).execute();
            holder.viewGroup.addView(imageView);
        }
        private void removeAllView(ViewGroup viewGroup) {
            if (viewGroup == null) {
                return;
            }
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                final View view = viewGroup.getChildAt(i);
                if (view instanceof ImageView) {
                    ((ImageView) view).setImageDrawable(null);
                } else if (view instanceof GNSNativeVideoPlayerView) {
                    ((GNSNativeVideoPlayerView) view).remove();
                    removeVideoView((GNSNativeVideoPlayerView)view);
                } else if (view instanceof ViewGroup) {
                    removeAllView(((ViewGroup) view));
                }
                view.setBackground(null);
            }
            viewGroup.removeAllViews();
        }
        private void removeVideoView(GNSNativeVideoPlayerView view) {
            for (GNSNativeVideoPlayerView videoView : videoViews) {
                if (videoView == (GNSNativeVideoPlayerView) view) {
                    videoView.remove();
                    videoViews.remove(videoView);
                    break;
                }
            }
        }
    }

    class ImageGetTask extends AsyncTask<Void,Void,Bitmap> {
        protected String url;
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
