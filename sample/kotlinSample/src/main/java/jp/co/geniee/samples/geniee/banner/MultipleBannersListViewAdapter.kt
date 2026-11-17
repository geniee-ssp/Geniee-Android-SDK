package jp.co.geniee.samples.geniee.banner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import jp.co.geniee.gnadsdk.banner.GNAdView
import jp.co.geniee.samples.R
import jp.co.geniee.samples.geniee.common.CellData
import jp.co.geniee.samples.geniee.common.ImageGetTask

internal class MultipleBannersListViewAdapter(
    context: Context,
    private val dataList: List<Any>,
) : ArrayAdapter<Any>(context, 0, dataList) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemViewType(position: Int): Int =
        if (dataList[position] is GNAdView) TYPE_AD else TYPE_ITEM

    override fun getViewTypeCount(): Int = TYPE_MAX_COUNT

    override fun getCount(): Int = dataList.size

    override fun getItem(position: Int): Any = dataList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    private class ViewHolder {
        var imageView: ImageView? = null
        var textView: TextView? = null
        var tvDescription: TextView? = null
        var adLayout: LinearLayout? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder
        if (convertView == null) {
            holder = ViewHolder()
            if (getItemViewType(position) == TYPE_AD) {
                view = inflater.inflate(R.layout.ad_item, null)
                holder.adLayout = view.findViewById(R.id.ad_layout)
            } else {
                view = inflater.inflate(R.layout.listview_row_item, null)
                holder.textView = view.findViewById(R.id.textView)
                holder.imageView = view.findViewById(R.id.imageView)
                holder.tvDescription = view.findViewById(R.id.tvDescription)
            }
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        when (val cell = getItem(position)) {
            // Rendering SDK GNAdView content
            is GNAdView -> {
                val adLayout = holder.adLayout!!
                adLayout.removeAllViews()
                if (cell.parent == null) {
                    adLayout.addView(cell, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                    cell.showBannerWithSize(300, 100)
                    cell.startAdLoop()
                }
            }
            is CellData -> {
                holder.textView?.text = cell.title
                holder.tvDescription?.text = cell.content
                val imageView = holder.imageView!!
                imageView.tag = cell.imgURL
                @Suppress("DEPRECATION")
                ImageGetTask(imageView, cell.imgURL).execute()
            }
        }
        return view
    }

    private companion object {
        const val TYPE_ITEM = 0
        const val TYPE_AD = 1
        const val TYPE_MAX_COUNT = TYPE_AD + 1
    }
}
