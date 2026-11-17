package jp.co.geniee.samples.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import jp.co.geniee.samples.R
import java.util.Locale

abstract class BaseMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        setupListViewContents(findViewById(R.id.listView), getListViewContents())
    }

    private fun setupListViewContents(listView: ListView, items: Array<MenuItem>) {
        listView.adapter = object : ArrayAdapter<MenuItem>(this, R.layout.simple_list_item, items) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val row = convertView
                    ?: LayoutInflater.from(context).inflate(R.layout.simple_list_item, parent, false)
                        .also { it.tag = ViewHolder(it) }
                (row.tag as ViewHolder).bind(items[position])
                return row
            }
        }
        listView.setOnItemClickListener { _, _, position, _ ->
            val intent = items[position].intent
            if (intent != null) {
                startActivity(intent)
            } else {
                onMenuItemClick(position)
            }
        }
    }

    protected open fun onMenuItemClick(position: Int) {}

    protected abstract fun getListViewContents(): Array<MenuItem>

    private class ViewHolder(row: View) {
        private val icon: TextView = row.findViewById(R.id.icon)
        private val title: TextView = row.findViewById(R.id.text1)
        private val subtitle: TextView = row.findViewById(R.id.text2)

        fun bind(item: MenuItem) {
            val titleText = item.title?.trim().orEmpty()
            icon.text = if (titleText.isEmpty()) "" else titleText.substring(0, 1).uppercase(Locale.US)
            title.text = titleText

            // Subtitles are comma-separated feature lists, e.g. "Banner,Native".
            val subtitleText = item.subtitle?.trim().orEmpty()
            subtitle.text = subtitleText.replace(Regex("\\s*,\\s*"), " · ")
            subtitle.visibility = if (subtitleText.isEmpty()) View.GONE else View.VISIBLE
        }
    }
}
