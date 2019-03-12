package jp.co.geniee.samples.gnadsamplenativead;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import jp.co.geniee.gnadsdk.common.GNAdLogger;
import jp.co.geniee.gnadsdk.common.GNSException;
import jp.co.geniee.samples.gnadsamplenativead.list.MemCache;
import jp.co.geniee.sdk.ads.nativead.GNNativeAd;
import jp.co.geniee.sdk.ads.nativead.GNNativeAdRequest;
import jp.co.geniee.sdk.ads.nativead.GNNativeAdRequestListener;
import jp.co.geniee.sdk.ads.nativead.GNSNativeVideoPlayerListener;
import jp.co.geniee.sdk.ads.nativead.GNSNativeVideoPlayerView;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NativeAdSampleSimpleVideoActivity extends AppCompatActivity {
    private  String TAG = "NativeAdSampleSimpleVideoActivity";
    private GNNativeAdRequest nativeAdRequest;
    private long timeStart, timeEnd;
    private GNAdLogger log;
    private ArrayList<GNSNativeVideoPlayerView> videoViews = new ArrayList<GNSNativeVideoPlayerView>();
    private ArrayList<String> mLogArrayList = new ArrayList<String>();
    private ArrayAdapter<String> mLogAdapter;

    private int loopCount = 0;
    private int previousHeight = 0;
    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public synchronized void run() {
            for (GNSNativeVideoPlayerView videoView : videoViews) {
                float aspect = videoView.getMediaFileAspect();
                int height = (int) (videoView.getWidth() / aspect);
                if (height != previousHeight) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
                    videoView.setLayoutParams(params);
                    previousHeight = height;
                }
            }
            if(loopCount <= 8) {
                loopCount = loopCount + 1;
                handler.postDelayed(runnable, 250);
            }
        }
    };

    public String statusMessage(String message) {
        Log.d(TAG, message);
        return String.format("%s %s", (new SimpleDateFormat("HH:mm:ss")).format(Calendar.getInstance().getTime()), message);
    }
    private GNNativeAdRequestListener nativeListener = new GNNativeAdRequestListener() {
        @Override
        public void onNativeAdsLoaded(GNNativeAd[] nativeAds) {
            timeEnd = System.currentTimeMillis();
            Log.i(TAG,"NativeAds loaded in seconds:" + (timeEnd - timeStart)/1000.0);
            mLogArrayList.add(statusMessage("Number of zoneid : " + nativeAds.length));
            mLogAdapter.notifyDataSetChanged();
            LinearLayout videoPlayerLayout = findViewById(R.id.videoPlayerLayout);
            for (final GNNativeAd ad : nativeAds) {
                if (ad.hasVideoContent()) {
                    final GNSNativeVideoPlayerView videoView = new GNSNativeVideoPlayerView(getApplicationContext());
                    videoView.setActivity(NativeAdSampleSimpleVideoActivity.this);
                    videoView.setListener(new GNSNativeVideoPlayerListener() {
                        // Sent when an video ad request succeeded.
                        @Override
                        public void onVideoReceiveSetting(GNSNativeVideoPlayerView videoView) {
                            videoView.getLayoutParams().height = (int) (videoView.getWidth() / videoView.getMediaFileAspect());
                            Log.i(TAG, "onVideoReceiveSetting videoView.isReady()=" + videoView.isReady());
                            if (videoView.isReady()) {
                                videoView.show();
                                Log.i(TAG, "onVideoReceiveSetting videoView.show");
                            }
                        }
                        // Sent when an video ad request failed.
                        // (Network Connection Unavailable, Frequency capping, Out of ad stock)
                        @Override
                        public void onVideoFailWithError(GNSNativeVideoPlayerView videoView, GNSException e) {
                            Log.e(TAG, "Err:" + e.getCode() + " " + e.getMessage());
                            mLogArrayList.add(statusMessage("Err:" + e.getCode() + " " + e.getMessage()));
                            mLogAdapter.notifyDataSetChanged();
                        }
                        // When playback of video ad is started
                        @Override
                        public void onVideoStartPlaying(GNSNativeVideoPlayerView videoView) {
                            Log.i(TAG, "Ad started playing.");
                        }
                        // When playback of video ad is completed
                        @Override
                        public void onVideoPlayComplete(GNSNativeVideoPlayerView videoView) {
                            Log.i(TAG, "Ad finished playing.");
                        }
                    });
                    videoPlayerLayout.addView(videoView, new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    videoView.load(ad);
                    videoViews.add(videoView);
                    Runnable seekRunnable = new Runnable() {
                        float previousCurrentPosition = 0f;
                        @Override
                        public synchronized void run() {
                            if (previousCurrentPosition != videoView.getCurrentPosition()) {
                                mLogArrayList.add(statusMessage("zoneID=" + ad.zoneID + " position=" + videoView.getCurrentPosition()
                                        + "/" + videoView.getDuration()));
                                mLogAdapter.notifyDataSetChanged();
                                previousCurrentPosition = videoView.getCurrentPosition();
                            }
                            if (videoView.getCurrentPosition() < videoView.getDuration()) {
                                handler.postDelayed(this, 1000);
                            }
                        }
                    };
                    handler.postDelayed(seekRunnable, 1000);
                } else {
                    ImageView imageView = new ImageView(getApplicationContext());
                    imageView.setTag(ad.screenshots_url);
                    imageView.setScaleType(ImageView.ScaleType.FIT_START);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    new ImageGetTask(imageView, ad.screenshots_url).execute();
                    videoPlayerLayout.addView(imageView, new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
                ImageView iconImage = findViewById(R.id.iconImage);
                iconImage.setTag(ad.icon_url);
                iconImage.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                new ImageGetTask(iconImage, ad.icon_url).execute();
                TextView titleText = findViewById(R.id.titleText);
                titleText.setText(ad.title);
                TextView descriptionText = findViewById(R.id.descriptionText);
                descriptionText.setText(ad.description);
                TextView advertiserText = findViewById(R.id.advertiserText);
                advertiserText.setText(ad.advertiser);
                Button clickButton = findViewById(R.id.videoPlayerClickButton);
                clickButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ad.onTrackingClick();
                    }
                });
                Button outputButton = findViewById(R.id.videoPlayerOutputButton);
                if (ad.optout_text != "") {
                    outputButton.setText(ad.optout_text);
                } else {
                    outputButton.setText("広告提供元");
                }
                outputButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                        public void onClick(View view) {
                            if (!ad.optout_url.isEmpty()) {
                                Uri uri = Uri.parse(ad.optout_url);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            } else {
                                Toast.makeText(NativeAdSampleSimpleVideoActivity.this, "ad.optout_url is empty", Toast.LENGTH_LONG).show();
                            }
                    }
                });
                mLogArrayList.add(statusMessage("nativeAd.zoneID=" + ad.zoneID));
                mLogArrayList.add(statusMessage("nativeAd.advertiser=" + ad.advertiser));
                mLogArrayList.add(statusMessage("nativeAd.title=" + ad.title));
                mLogArrayList.add(statusMessage("nativeAd.description=" + ad.description));
                mLogArrayList.add(statusMessage("nativeAd.cta=" + ad.cta));
                mLogArrayList.add(statusMessage("nativeAd.icon_aspectRatio=" + ad.icon_aspectRatio));
                mLogArrayList.add(statusMessage("nativeAd.icon_url=" + ad.icon_url));
                mLogArrayList.add(statusMessage("nativeAd.icon_height=" + ad.icon_height));
                mLogArrayList.add(statusMessage("nativeAd.icon_width=" + ad.icon_width));
                mLogArrayList.add(statusMessage("nativeAd.screenshots_aspectRatio=" + ad.screenshots_aspectRatio));
                mLogArrayList.add(statusMessage("nativeAd.screenshots_url=" + ad.screenshots_url));
                mLogArrayList.add(statusMessage("nativeAd.screenshots_height=" + ad.screenshots_height));
                mLogArrayList.add(statusMessage("nativeAd.screenshots_width=" + ad.screenshots_width));
                mLogArrayList.add(statusMessage("nativeAd.app_appName=" + ad.app_appName));
                mLogArrayList.add(statusMessage("nativeAd.app_appid=" + ad.app_appid));
                mLogArrayList.add(statusMessage("nativeAd.app_rating=" + ad.app_rating));
                mLogArrayList.add(statusMessage("nativeAd.app_storeURL=" + ad.app_storeURL));
                mLogArrayList.add(statusMessage("nativeAd.app_targetAge=" + ad.app_targetAge));
                mLogArrayList.add(statusMessage("nativeAd.optout_text=" + ad.optout_text));
                mLogArrayList.add(statusMessage("nativeAd.optout_image_url=" + ad.optout_image_url));
                mLogArrayList.add(statusMessage("nativeAd.optout_url=" + ad.optout_url));
                mLogArrayList.add(statusMessage("nativeAd.hasVideoContent()=" + ad.hasVideoContent()));
                // Report SDK impression
                ad.onTrackingImpression();
                mLogArrayList.add(statusMessage("ZoneId=" + ad.zoneID + " Issued TrackingImpression."));
                mLogAdapter.notifyDataSetChanged();
            }
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
        setContentView(R.layout.activity_native_ad_sample_simple_video);
        log = GNAdLogger.getInstance();
        log.setPriority(GNAdLogger.DEBUG);
        timeStart = System.currentTimeMillis();
        Intent intent = getIntent();
        String zoneId = intent.getStringExtra(NativeAdSamplePrefsFragment.extraZoneId);
        try {
            // Initialize SDK GNNativeAdRequest
            nativeAdRequest = new GNNativeAdRequest(getApplicationContext(), zoneId);
            nativeAdRequest.setAdListener(nativeListener);
            //nativeAdRequest.setGeoLocationEnable(true);
            if (zoneId.split(",").length > 1) {
                nativeAdRequest.multiLoadAds(NativeAdSampleSimpleVideoActivity.this);
            } else {
                nativeAdRequest.loadAds(NativeAdSampleSimpleVideoActivity.this);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Err " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (mLogAdapter == null) {
            mLogAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, mLogArrayList) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                    tv.setTextColor(Color.BLACK);
                    tv.setSingleLine(false);
                    return view;
                }
            };
        }
        ((ListView)findViewById(R.id.gns_sample_list_view)).setAdapter(mLogAdapter);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        loopCount = 0;
        handler.post(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (GNSNativeVideoPlayerView videoView : videoViews) {
            if (videoView != null) {
                videoView.resume();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (GNSNativeVideoPlayerView videoView : videoViews) {
            if (videoView != null) {
                videoView.pause();
            }
        }
    }

    @Override
    protected void onDestroy() {
        for (GNSNativeVideoPlayerView videoView : videoViews) {
            if (videoView != null) {
                videoView.remove();
            }
        }
        super.onDestroy();
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
