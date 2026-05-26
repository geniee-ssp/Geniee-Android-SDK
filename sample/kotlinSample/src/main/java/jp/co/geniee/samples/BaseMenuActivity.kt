package jp.co.geniee.samples

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView

abstract class BaseMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        setupListViewContents(getListViewContents())
    }

    private fun setupListViewContents(items: Array<MenuItem>) {
        val listAdapter = object : ArrayAdapter<MenuItem>(this, R.layout.simple_list_item, items) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                var row = convertView
                if (row == null) {
                    val inflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    row = inflater.inflate(R.layout.simple_list_item, parent, false)
                }

                val item = items[position]

                val title = row!!.findViewById<TextView>(R.id.text1)
                title.text = item.title
                val subtitle = row.findViewById<TextView>(R.id.text2)
                subtitle.text = item.subTitle

                return row
            }
        }

        val listView = findViewById<ListView>(R.id.listView)
        listView.adapter = listAdapter

        val itemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent = items[position].intent

            startActivity(intent)
        }
        listView.onItemClickListener = itemClickListener
    }

    abstract fun getListViewContents(): Array<MenuItem>

}
