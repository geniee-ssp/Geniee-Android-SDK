package jp.co.geniee.samples.videoplayer;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jp.co.geniee.gnadsdk.common.GNAdConstants;
import jp.co.geniee.gnadsdk.common.GNAdLogger;
import jp.co.geniee.gnadsdk.common.GNSException;
import jp.co.geniee.gnadsdk.videoplayer.GNSVideoPlayerListener;
import jp.co.geniee.gnadsdk.videoplayer.GNSVideoPlayerView;
import jp.co.geniee.samples.R;


public class GNAdSampleVideoPlayerLoadShow extends AppCompatActivity {
    private static final String TAG = "SampleVideoLoadShow";
    private GNSVideoPlayerView  videoView;
    private EditText vastUrlText;
    private GNAdLogger log;

    private int loopCount = 0;
    private int previousHeight = 0;
    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public synchronized void run() {
            float aspect = videoView.getMediaFileAspect();
            int height = (int) (videoView.getWidth() / aspect);
            if (height != previousHeight) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height);
                videoView.setLayoutParams(params);
                previousHeight = height;
                if (height > 0) {
                    log.debug_i(TAG, "onConfigurationChanged aspect=" + aspect);
                    log.debug_i(TAG, "onConfigurationChanged videoView.width=" + videoView.getWidth());
                    log.debug_i(TAG, "onConfigurationChanged videoView.height=" + videoView.getHeight());
                    log.debug_i(TAG, "onConfigurationChanged params.height=" + height);
                }
            }
            if(loopCount <= 8) {
                loopCount = loopCount + 1;
                handler.postDelayed(runnable, 250);
            }
        }
    };

    private GNSVideoPlayerListener mListener = new GNSVideoPlayerListener() {
        // Sent when an video ad request succeeded.
        @Override
        public void onVideoReceiveSetting(GNSVideoPlayerView view) {
            Toast.makeText(GNAdSampleVideoPlayerLoadShow.this, "Successfully loaded Ad.", Toast.LENGTH_SHORT).show();
            ViewGroup.LayoutParams params = videoView.getLayoutParams();
            float aspect = videoView.getMediaFileAspect();
            int height = (int) (videoView.getWidth() / aspect);
            params.height = height;
       }
        // Sent when an video ad request failed.
        // (Network Connection Unavailable, Frequency capping, Out of ad stock)
        @Override
        public void onVideoFailWithError(GNSVideoPlayerView view, GNSException e) {
            Toast.makeText(GNAdSampleVideoPlayerLoadShow.this, "Err:" + e.getCode() + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        // When playback of video ad is started
        @Override
        public void onVideoStartPlaying(GNSVideoPlayerView view) {
            Toast.makeText(GNAdSampleVideoPlayerLoadShow.this, "Ad started playing.", Toast.LENGTH_SHORT).show();
        }
        // When playback of video ad is completed
        @Override
        public void onVideoPlayComplete(GNSVideoPlayerView view) {
            Toast.makeText(GNAdSampleVideoPlayerLoadShow.this, "Ad finished playing.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVideoClose(GNSVideoPlayerView view) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gnad_sample_video_normal);
        log = GNAdLogger.getInstance();
        log.setPriority(GNAdLogger.INFO);
        Intent intent = getIntent();
        Integer layoutId = intent.getIntExtra(GNAdSampleVideoPlayer.extraLayout, R.layout.activity_gnad_sample_video_normal);
        setContentView(layoutId);
        vastUrlText = (EditText) findViewById(R.id.vastUrl);
        log.debug(TAG, "url=" + intent.getStringExtra(GNAdSampleVideoPlayer.extraVasturl));
        vastUrlText.setText(intent.getStringExtra(GNAdSampleVideoPlayer.extraVasturl));
        Button loadAdButton = (Button) findViewById(R.id.load_ad_button);
        loadAdButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vastUrlText.getText().toString().indexOf("https://") > -1) {
                    videoView.loadUrl(vastUrlText.getText().toString());
                } else {
                    try {
                        log.i(TAG, "assets xml=");
                        videoView.loadXml(getAssetsString(vastUrlText.getText().toString()));
                    } catch (IOException e) {
                        log.e(TAG, "assets IOException file=" + vastUrlText.getText().toString());
                    }
                }
            }
        });
        Button showAdButton = (Button) findViewById(R.id.show_ad_button);
        showAdButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoView.isReady()) {
                    videoView.show();
                }
            }
        });

        Button mutedButton = (Button) findViewById(R.id.mute_button);
        mutedButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = (videoView.getMuted()) ? false : true;
                Toast.makeText(GNAdSampleVideoPlayerLoadShow.this, "muted " + flag, Toast.LENGTH_SHORT).show();
                videoView.setMuted(flag);
            }
        });
        Button autoLoopButton = (Button) findViewById(R.id.auto_loop_button);
        autoLoopButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = (videoView.getAutoLoop()) ? false : true;
                Toast.makeText(GNAdSampleVideoPlayerLoadShow.this, "loop " + flag, Toast.LENGTH_SHORT).show();
                videoView.setAutoLoop(flag);
            }
        });
        Button replayButton = (Button) findViewById(R.id.replay_button);
        replayButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GNAdSampleVideoPlayerLoadShow.this, "replay", Toast.LENGTH_SHORT).show();
                videoView.replay();
            }
        });
        Button dispMuteButton = (Button) findViewById(R.id.disp_mute_button);
        dispMuteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = (videoView.getVisibilityMuteButton()) ? false : true;
                Toast.makeText(GNAdSampleVideoPlayerLoadShow.this, "mute button " + flag, Toast.LENGTH_SHORT).show();
                videoView.setVisibilityMuteButton(flag);
            }
        });
        Button dispBar = (Button) findViewById(R.id.disp_bar);
        dispBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = (videoView.getVisibilityProgressbar()) ? false : true;
                Toast.makeText(GNAdSampleVideoPlayerLoadShow.this, "progressbar " + flag, Toast.LENGTH_SHORT).show();
                videoView.setVisibilityProgressbar(flag);
            }
        });
        Button dispTimeLabel = (Button) findViewById(R.id.disp_time_label);
        dispTimeLabel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = (videoView.getVisibilityCurrentTimeLabel()) ? false : true;
                Toast.makeText(GNAdSampleVideoPlayerLoadShow.this, "time label " + flag, Toast.LENGTH_SHORT).show();
                videoView.setVisibilityCurrentTimeLabel(flag);
            }
        });
        Button dispReplayButton = (Button) findViewById(R.id.disp_replay_button);
        dispReplayButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = (videoView.getVisibilityReplayButton()) ? false : true;
                Toast.makeText(GNAdSampleVideoPlayerLoadShow.this, "replay button " + flag, Toast.LENGTH_SHORT).show();
                videoView.setVisibilityReplayButton(flag);
            }
        });

        Button currentPositionButton = (Button) findViewById(R.id.current_position_button);
        currentPositionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GNAdSampleVideoPlayerLoadShow.this,
                        "videoPosition " + videoView.getCurrentPosition() + "/" + videoView.getDuration() + " playing=" + videoView.isPlaying(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        Button clearCacheButton = (Button) findViewById(R.id.clear_cache_button);
        clearCacheButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GNAdSampleVideoPlayerLoadShow.this, "clearcache", Toast.LENGTH_SHORT).show();
                videoView.clearCache();
            }
        });

        if (videoView == null) {
            videoView = findViewById(R.id.videoplayerView);
        }
        videoView.setActivity(this);
        videoView.setListener(mListener);
        videoView.create();

        ViewGroup.LayoutParams params = videoView.getLayoutParams();
        if (params.height <= 0) {
            params.height = 1;
        }
        log.i(TAG, "params.width=" + params.width);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gnad_sample_video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logs) {
            Uri uri = Uri.parse(getResources().getString(R.string.log_url));
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        loopCount = 0;
        handler.post(runnable);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (videoView != null) {
            videoView.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView != null) {
            videoView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null) {
            videoView.pause();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (videoView != null) {
            videoView.stop();
        }
    }

    @Override
    protected void onDestroy() {
        if (videoView != null) {
            videoView.remove();
        }
        super.onDestroy();
    }

    private String getAssetsString(String filepath) throws IOException {
        InputStream is = null;
        BufferedReader br = null;
        String ret = "";
        try {
            is = this.getAssets().open(filepath);
            br = new BufferedReader(new InputStreamReader(is));
            String str;
            while ((str = br.readLine()) != null) {
                ret += str + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
        } finally {
            if (is != null) is.close();
            if (br != null) br.close();
        }
        return ret;
    }
}
