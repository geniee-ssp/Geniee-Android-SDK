package jp.co.geniee.samples.geniee.nativead

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import jp.co.geniee.samples.R
import jp.co.geniee.samples.geniee.common.CellData
import jp.co.geniee.samples.geniee.common.ImageGetTask
import jp.co.geniee.sdk.ads.nativead.GNNativeAd

internal class NativeAdListViewAdapter(context: Context, list: List<Any>) :
    ArrayAdapter<Any>(context, 0, list) {

    private val inflater = LayoutInflater.from(context)

    private class ViewHolder(view: View) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val textView: TextView = view.findViewById(R.id.textView)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvSponsor: TextView = view.findViewById(R.id.tvSponsor)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
            ?: inflater.inflate(R.layout.listview_row_item, null).also { it.tag = ViewHolder(it) }
        val holder = view.tag as ViewHolder

        when (val cell = getItem(position)) {
            // Rendering SDK NativeAd content
            is GNNativeAd -> {
                holder.tvSponsor.visibility = View.VISIBLE
                holder.tvSponsor.text = String.format("Sponsor | %s", cell.advertiser)
                holder.textView.text = cell.title
                holder.tvDescription.text = cell.description
                ImageGetTask(holder.imageView, cell.icon_url).execute()
                // Report SDK impression
                cell.onTrackingImpression()
            }
            is CellData -> {
                holder.tvSponsor.visibility = View.GONE
                holder.textView.text = cell.title
                holder.tvDescription.text = cell.content
                holder.imageView.tag = cell.imgURL
                ImageGetTask(holder.imageView, cell.imgURL).execute()
            }
        }
        return view
    }
}
