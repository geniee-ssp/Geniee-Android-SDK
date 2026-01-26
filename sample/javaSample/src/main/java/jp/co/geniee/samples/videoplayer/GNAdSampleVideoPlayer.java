package jp.co.geniee.samples.videoplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import jp.co.geniee.samples.R;

public class GNAdSampleVideoPlayer extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static final String extraVasturl = "vasturl";
    public static final String extraLayout = "layout";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gnad_sample_video_list);

        ListView myListView = (ListView) findViewById(R.id.myListView);
        ArrayList<String> items = new ArrayList<>();
        String samples[] = getResources().getStringArray(R.array.sample_string_ids);
        for (String sample_string_id : samples) {
            items.add(getResources().getString(getResources().getIdentifier(sample_string_id, "string", getPackageName())));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.list_item,
                items
        );
        myListView.setAdapter(adapter);
        myListView.setOnItemClickListener(this);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        Intent intent = new Intent(this.getApplicationContext(), GNAdSampleVideoPlayerLoadShow.class);
        String samples[] = getResources().getStringArray(R.array.sample_string_ids);
        String sample_string_id = samples[position];
        int res_id = getResources().getIdentifier(sample_string_id,"string", getPackageName());
        intent.putExtra(extraVasturl, getResources().getString(res_id));
        if (res_id == R.string.video_16_9) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_16_9));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_9_16) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_9_16));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_4_3) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_4_3));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_1_1) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_1_1));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_small_16_9) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_16_9));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_small);
        } else if (res_id == R.string.video_small_9_16) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_9_16));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_small);
        } else if (res_id == R.string.video_small_4_3) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_4_3));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_small);
        } else if (res_id == R.string.video_small_1_1) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_1_1));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_small);
        } else if (res_id == R.string.video_16_9_endcard_jpg) {
                intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_16_9_endcard_jpg));
                intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_16_9_endcard_gif) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_16_9_endcard_gif));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_16_9_endcard_no) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_16_9_endcard_no));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_1_1_inview_no) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_1_1_inview_no));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_1_1_inview_50_90) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_1_1_inview_50_90));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_1_1_inview_30) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_1_1_inview_30));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_1_1_inview_20_70) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_1_1_inview_20_70));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_1_1_inview_110_120) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_1_1_inview_110_120));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_1_1_inview_m10_m20) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_1_1_inview_m10_m20));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_1_1_inview_110_40) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_1_1_inview_110_40));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_1_1_inview_110_60) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_1_1_inview_110_60));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_media_webm) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_media_webm));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_media_mov) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_media_mov));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_media_sizeover) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_media_sizeover));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_media_timeout) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_media_timeout));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_duration_millsec) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_duration_millsec));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_duration_null) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_duration_null));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_nend) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_nend));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_error) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_error));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_loadxml_16_9) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_asset_16_9));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_outview) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_16_9));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_outview);
        } else if (res_id == R.string.video_mo_test1) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_mo_test1));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_mo_test2) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_mo_test2));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_mo_test3) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_mo_test3));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_mo_test4) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_mo_test4));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_mo_test5) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_mo_test5));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_mo_test6) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_mo_test6));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_mo_test7) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_mo_test7));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_mo_test8) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_mo_test8));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_mo_test9) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_mo_test9));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_mo_test10) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_mo_test10));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_mo_test11) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_mo_test11));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_mo_test12) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_mo_test12));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_mo_test13) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_mo_test13));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_mo_test14) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_mo_test14));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_mo_test15) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_mo_test15));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_mo_test16) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_mo_test16));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        } else if (res_id == R.string.video_mo_test17) {
            intent.putExtra(extraVasturl, getResources().getString(R.string.vasturl_mo_test17));
            intent.putExtra(extraLayout, R.layout.activity_gnad_sample_video_normal);
        }
        startActivity(intent);
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

        //noinspection SimplifiableIfStatement
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
}
