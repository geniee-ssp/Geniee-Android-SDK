package jp.co.geniee.samples.firebasegooglead

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import jp.co.geniee.samples.R
import jp.co.geniee.samples.firebasegooglead.enums.FormatType
import kotlinx.android.synthetic.main.activity_firebasegooglead_main.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private val adNames =
            arrayOf(
                    FormatType.BANNER.name,
                    FormatType.INTERSTITIAL.name,
                    FormatType.REWARD_VIDEO.name,
                    FormatType.REWARD_VIDEO_NEW_API.name
            )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebasegooglead_main)

        val arrayAdapter: ArrayAdapter<String> =
                ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, adNames)
        main_list.adapter = arrayAdapter

        main_list.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    val intent: Intent =
                            when (position) {
                                FormatType.BANNER.ordinal ->
                                        Intent(this, BannerActivity::class.java)
                                FormatType.INTERSTITIAL.ordinal ->
                                        Intent(this, InterstitialActivity::class.java)
                                FormatType.REWARD_VIDEO.ordinal ->
                                        Intent(this, RewardActivity::class.java)
                                else -> Intent(this, RewardNewAPIActivity::class.java)
                            }
                    startActivity(intent)
                }
    }
}
