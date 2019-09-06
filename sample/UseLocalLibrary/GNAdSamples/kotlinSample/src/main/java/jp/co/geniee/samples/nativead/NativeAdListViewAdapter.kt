package jp.co.geniee.samples.nativead

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import jp.co.geniee.samples.R
import jp.co.geniee.samples.common.CellData
import jp.co.geniee.samples.common.ImageGetTask
import jp.co.geniee.sdk.ads.nativead.GNNativeAd

internal class NativeAdListViewAdapter(context: Context, list: List<Any>) : ArrayAdapter<Any>(context, 0, list) {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    internal inner class ViewHolder {
        var imageView: ImageView? = null
        var textView: TextView? = null
        var tvDescription: TextView? = null
        var tvSponsor: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ViewHolder
        if (view == null) {
            view = inflater.inflate(R.layout.listview_row_item, null)
            holder = ViewHolder()
            holder.textView = view!!.findViewById(R.id.textView)
            holder.imageView = view.findViewById(R.id.imageView)
            holder.tvDescription = view.findViewById(R.id.tvDescription)
            holder.tvSponsor = view.findViewById(R.id.tvSponsor)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val cell = getItem(position)
        // Rendering SDK NativeAd content
        if (cell is GNNativeAd) {
            holder.tvSponsor!!.visibility = View.VISIBLE
            holder.tvSponsor!!.text = String.format("Sponsor | %s", cell.advertiser)
            holder.textView!!.text = cell.title
            holder.tvDescription!!.text = cell.description
            ImageGetTask(holder.imageView!!, cell.icon_url).execute()
            // Report SDK impression
            cell.onTrackingImpression()
        } else if (cell is CellData) {
            holder.tvSponsor!!.visibility = View.GONE
            holder.textView!!.text = cell.title
            holder.tvDescription!!.text = cell.content
            holder.imageView!!.tag = cell.imgURL
            ImageGetTask(holder.imageView!!, cell.imgURL).execute()
        }
        return view
    }

}
