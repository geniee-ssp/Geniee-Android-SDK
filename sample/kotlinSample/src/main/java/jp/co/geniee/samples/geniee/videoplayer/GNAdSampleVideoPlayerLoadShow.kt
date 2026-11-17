package jp.co.geniee.samples.geniee.videoplayer

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import jp.co.geniee.gnadsdk.common.GNAdLogger
import jp.co.geniee.gnadsdk.common.GNSException
import jp.co.geniee.gnadsdk.videoplayer.GNSVideoPlayerListener
import jp.co.geniee.gnadsdk.videoplayer.GNSVideoPlayerView
import jp.co.geniee.samples.R
import java.io.IOException

class GNAdSampleVideoPlayerLoadShow : AppCompatActivity() {
    private var videoView: GNSVideoPlayerView? = null
    private lateinit var vastUrlText: EditText
    private lateinit var log: GNAdLogger

    private var loopCount = 0
    private var previousHeight = 0
    private val handler = Handler(Looper.getMainLooper())
    private val runnable: Runnable = object : Runnable {
        @Synchronized
        override fun run() {
            val videoView = videoView ?: return
            val aspect = videoView.getMediaFileAspect()
            val height = (videoView.width / aspect).toInt()
            if (height != previousHeight) {
                videoView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
                previousHeight = height
                if (height > 0) {
                    log.debug_i(TAG, "onConfigurationChanged aspect=$aspect")
                    log.debug_i(TAG, "onConfigurationChanged videoView.width=" + videoView.width)
                    log.debug_i(TAG, "onConfigurationChanged videoView.height=" + videoView.height)
                    log.debug_i(TAG, "onConfigurationChanged params.height=$height")
                }
            }
            if (loopCount <= 8) {
                loopCount += 1
                handler.postDelayed(this, 250)
            }
        }
    }

    private val listener = object : GNSVideoPlayerListener {
        // Sent when an video ad request succeeded.
        override fun onVideoReceiveSetting(view: GNSVideoPlayerView) {
            toast("Successfully loaded Ad.")
            val videoView = videoView ?: return
            val params = videoView.layoutParams
            params.height = (videoView.width / videoView.getMediaFileAspect()).toInt()
        }

        // Sent when an video ad request failed.
        // (Network Connection Unavailable, Frequency capping, Out of ad stock)
        override fun onVideoFailWithError(view: GNSVideoPlayerView, e: GNSException) {
            toast("Err:" + e.code + " " + e.message)
        }

        // When playback of video ad is started
        override fun onVideoStartPlaying(view: GNSVideoPlayerView) {
            toast("Ad started playing.")
        }

        // When playback of video ad is completed
        override fun onVideoPlayComplete(view: GNSVideoPlayerView) {
            toast("Ad finished playing.")
        }

        override fun onVideoClose(view: GNSVideoPlayerView) {}
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gnad_sample_video_normal)
        log = GNAdLogger.getInstance()
        log.setPriority(GNAdLogger.INFO)
        val layoutId = intent.getIntExtra(GNAdSampleVideoPlayer.extraLayout, R.layout.activity_gnad_sample_video_normal)
        setContentView(layoutId)
        vastUrlText = findViewById(R.id.vastUrl)
        log.debug(TAG, "url=" + intent.getStringExtra(GNAdSampleVideoPlayer.extraVasturl))
        vastUrlText.setText(intent.getStringExtra(GNAdSampleVideoPlayer.extraVasturl))

        val videoView = videoView ?: findViewById<GNSVideoPlayerView>(R.id.videoplayerView).also { videoView = it }

        findViewById<Button>(R.id.load_ad_button).setOnClickListener {
            val text = vastUrlText.text.toString()
            if (text.contains("https://")) {
                videoView.loadUrl(text)
            } else {
                try {
                    log.i(TAG, "assets xml=")
                    videoView.loadXml(getAssetsString(text))
                } catch (e: IOException) {
                    log.e(TAG, "assets IOException file=$text")
                }
            }
        }
        findViewById<Button>(R.id.show_ad_button).setOnClickListener {
            if (videoView.isReady()) {
                videoView.show()
            }
        }
        findViewById<Button>(R.id.mute_button).setOnClickListener {
            val flag = !videoView.getMuted()
            toast("muted $flag")
            videoView.setMuted(flag)
        }
        findViewById<Button>(R.id.auto_loop_button).setOnClickListener {
            val flag = !videoView.getAutoLoop()
            toast("loop $flag")
            videoView.setAutoLoop(flag)
        }
        findViewById<Button>(R.id.replay_button).setOnClickListener {
            toast("replay")
            videoView.replay()
        }
        findViewById<Button>(R.id.disp_mute_button).setOnClickListener {
            val flag = !videoView.getVisibilityMuteButton()
            toast("mute button $flag")
            videoView.setVisibilityMuteButton(flag)
        }
        findViewById<Button>(R.id.disp_bar).setOnClickListener {
            val flag = !videoView.getVisibilityProgressbar()
            toast("progressbar $flag")
            videoView.setVisibilityProgressbar(flag)
        }
        findViewById<Button>(R.id.disp_time_label).setOnClickListener {
            val flag = !videoView.getVisibilityCurrentTimeLabel()
            toast("time label $flag")
            videoView.setVisibilityCurrentTimeLabel(flag)
        }
        findViewById<Button>(R.id.disp_replay_button).setOnClickListener {
            val flag = !videoView.getVisibilityReplayButton()
            toast("replay button $flag")
            videoView.setVisibilityReplayButton(flag)
        }
        findViewById<Button>(R.id.current_position_button).setOnClickListener {
            toast("videoPosition " + videoView.getCurrentPosition() + "/" + videoView.getDuration() +
                    " playing=" + videoView.isPlaying())
        }
        findViewById<Button>(R.id.clear_cache_button).setOnClickListener {
            toast("clearcache")
            videoView.clearCache()
        }

        videoView.setActivity(this)
        videoView.setListener(listener)
        videoView.create()

        val params = videoView.layoutParams
        if (params.height <= 0) {
            params.height = 1
        }
        log.i(TAG, "params.width=" + params.width)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_gnad_sample_video, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_logs -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.log_url))))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        loopCount = 0
        handler.post(runnable)
    }

    override fun onStart() {
        super.onStart()
        videoView?.start()
    }

    override fun onResume() {
        super.onResume()
        videoView?.resume()
    }

    override fun onPause() {
        super.onPause()
        videoView?.pause()
    }

    override fun onStop() {
        super.onStop()
        videoView?.stop()
    }

    override fun onDestroy() {
        videoView?.remove()
        super.onDestroy()
    }

    @Throws(IOException::class)
    private fun getAssetsString(filepath: String): String {
        return try {
            assets.open(filepath).bufferedReader().useLines { lines ->
                lines.joinToString(separator = "") { it + "\n" }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }

    companion object {
        private const val TAG = "SampleVideoLoadShow"
    }
}
