package jp.co.geniee.samples.geniee.videoplayer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import jp.co.geniee.samples.R

class GNAdSampleVideoPlayer : AppCompatActivity(), AdapterView.OnItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gnad_sample_video_list)

        val items = resources.getStringArray(R.array.sample_string_ids).map { sampleStringId ->
            resources.getString(resources.getIdentifier(sampleStringId, "string", packageName))
        }
        val myListView = findViewById<ListView>(R.id.myListView)
        myListView.adapter = ArrayAdapter(this, R.layout.list_item, items)
        myListView.onItemClickListener = this
    }

    override fun onItemClick(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
        val intent = Intent(applicationContext, GNAdSampleVideoPlayerLoadShow::class.java)
        val sampleStringId = resources.getStringArray(R.array.sample_string_ids)[position]
        val resId = resources.getIdentifier(sampleStringId, "string", packageName)
        intent.putExtra(extraVasturl, resources.getString(resId))
        SAMPLES[resId]?.let { (vastUrlRes, layoutRes) ->
            intent.putExtra(extraVasturl, resources.getString(vastUrlRes))
            intent.putExtra(extraLayout, layoutRes)
        }
        startActivity(intent)
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

    companion object {
        const val extraVasturl = "vasturl"
        const val extraLayout = "layout"

        private val NORMAL = R.layout.activity_gnad_sample_video_normal
        private val SMALL = R.layout.activity_gnad_sample_video_small
        private val OUTVIEW = R.layout.activity_gnad_sample_video_outview

        /** Sample label string -> (VAST URL string, layout). */
        private val SAMPLES: Map<Int, Pair<Int, Int>> = mapOf(
            R.string.video_16_9 to (R.string.vasturl_16_9 to NORMAL),
            R.string.video_9_16 to (R.string.vasturl_9_16 to NORMAL),
            R.string.video_4_3 to (R.string.vasturl_4_3 to NORMAL),
            R.string.video_1_1 to (R.string.vasturl_1_1 to NORMAL),
            R.string.video_small_16_9 to (R.string.vasturl_16_9 to SMALL),
            R.string.video_small_9_16 to (R.string.vasturl_9_16 to SMALL),
            R.string.video_small_4_3 to (R.string.vasturl_4_3 to SMALL),
            R.string.video_small_1_1 to (R.string.vasturl_1_1 to SMALL),
            R.string.video_16_9_endcard_jpg to (R.string.vasturl_16_9_endcard_jpg to NORMAL),
            R.string.video_16_9_endcard_gif to (R.string.vasturl_16_9_endcard_gif to NORMAL),
            R.string.video_16_9_endcard_no to (R.string.vasturl_16_9_endcard_no to NORMAL),
            R.string.video_1_1_inview_no to (R.string.vasturl_1_1_inview_no to NORMAL),
            R.string.video_1_1_inview_50_90 to (R.string.vasturl_1_1_inview_50_90 to NORMAL),
            R.string.video_1_1_inview_30 to (R.string.vasturl_1_1_inview_30 to NORMAL),
            R.string.video_1_1_inview_20_70 to (R.string.vasturl_1_1_inview_20_70 to NORMAL),
            R.string.video_1_1_inview_110_120 to (R.string.vasturl_1_1_inview_110_120 to NORMAL),
            R.string.video_1_1_inview_m10_m20 to (R.string.vasturl_1_1_inview_m10_m20 to NORMAL),
            R.string.video_1_1_inview_110_40 to (R.string.vasturl_1_1_inview_110_40 to NORMAL),
            R.string.video_1_1_inview_110_60 to (R.string.vasturl_1_1_inview_110_60 to NORMAL),
            R.string.video_media_webm to (R.string.vasturl_media_webm to NORMAL),
            R.string.video_media_mov to (R.string.vasturl_media_mov to NORMAL),
            R.string.video_media_sizeover to (R.string.vasturl_media_sizeover to NORMAL),
            R.string.video_media_timeout to (R.string.vasturl_media_timeout to NORMAL),
            R.string.video_duration_millsec to (R.string.vasturl_duration_millsec to NORMAL),
            R.string.video_duration_null to (R.string.vasturl_duration_null to NORMAL),
            R.string.video_nend to (R.string.vasturl_nend to NORMAL),
            R.string.video_error to (R.string.vasturl_error to NORMAL),
            R.string.video_loadxml_16_9 to (R.string.vasturl_asset_16_9 to NORMAL),
            R.string.video_outview to (R.string.vasturl_16_9 to OUTVIEW),
            R.string.video_mo_test1 to (R.string.vasturl_mo_test1 to NORMAL),
            R.string.video_mo_test2 to (R.string.vasturl_mo_test2 to NORMAL),
            R.string.video_mo_test3 to (R.string.vasturl_mo_test3 to NORMAL),
            R.string.video_mo_test4 to (R.string.vasturl_mo_test4 to NORMAL),
            R.string.video_mo_test5 to (R.string.vasturl_mo_test5 to NORMAL),
            R.string.video_mo_test6 to (R.string.vasturl_mo_test6 to NORMAL),
            R.string.video_mo_test7 to (R.string.vasturl_mo_test7 to NORMAL),
            R.string.video_mo_test8 to (R.string.vasturl_mo_test8 to NORMAL),
            R.string.video_mo_test9 to (R.string.vasturl_mo_test9 to NORMAL),
            R.string.video_mo_test10 to (R.string.vasturl_mo_test10 to NORMAL),
            R.string.video_mo_test11 to (R.string.vasturl_mo_test11 to NORMAL),
            R.string.video_mo_test12 to (R.string.vasturl_mo_test12 to NORMAL),
            R.string.video_mo_test13 to (R.string.vasturl_mo_test13 to NORMAL),
            R.string.video_mo_test14 to (R.string.vasturl_mo_test14 to NORMAL),
            R.string.video_mo_test15 to (R.string.vasturl_mo_test15 to NORMAL),
            R.string.video_mo_test16 to (R.string.vasturl_mo_test16 to NORMAL),
            R.string.video_mo_test17 to (R.string.vasturl_mo_test17 to NORMAL),
        )
    }
}
