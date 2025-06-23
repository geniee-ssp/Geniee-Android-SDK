package jp.co.geniee.samples.gnad.banner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import jp.co.geniee.samples.R
import jp.co.geniee.samples.gnad.common.CellData
import jp.co.geniee.samples.gnad.common.ImageGetTask
import jp.co.geniee.gnadsdk.banner.GNAdView
import java.util.*

internal class MultipleBannersListViewAdapter(context: Context, list: List<Any>) : ArrayAdapter<Any>(context, 0, list) {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var dataList = ArrayList<Any>()

    init {

        this.dataList = list as ArrayList<Any>
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataList[position] is GNAdView) TYPE_AD else TYPE_ITEM
    }

    override fun getViewTypeCount(): Int {
        return TYPE_MAX_COUNT
    }

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): Any? {
        return dataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    internal inner class ViewHolder {
        var imageView: ImageView? = null
        var textView: TextView? = null
        var tvDescription: TextView? = null
        var adLayout: LinearLayout? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ViewHolder
        val type = getItemViewType(position)
        if (view == null) {
            holder = ViewHolder()
            when (type) {
                TYPE_AD -> {
                    view = inflater.inflate(R.layout.ad_item, null)
                    holder.adLayout = view!!.findViewById(R.id.ad_layout)
                }
                TYPE_ITEM -> {
                    view = inflater.inflate(R.layout.listview_row_item, null)
                    holder.textView = view!!.findViewById(R.id.textView)
                    holder.imageView = view.findViewById(R.id.imageView)
                    holder.tvDescription = view.findViewById(R.id.tvDescription)
                }
            }
            view!!.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val cell = getItem(position)
        // Rendering SDK GNAdView content
        if (cell is GNAdView) {
            holder.adLayout!!.removeAllViews()
            if (cell.parent == null) {
                holder.adLayout!!.addView(cell, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                cell.showBannerWithSize(300, 100)
                cell.startAdLoop()
            }

        } else if (cell is CellData) {

            holder.textView!!.text = cell.title
            holder.tvDescription!!.text = cell.content
            holder.imageView!!.tag = cell.imgURL
            ImageGetTask(holder.imageView!!, cell.imgURL).execute()
        }
        return view
    }

    companion object {
        private val TYPE_ITEM = 0
        private val TYPE_AD = 1
        private val TYPE_MAX_COUNT = TYPE_AD + 1
    }
}
