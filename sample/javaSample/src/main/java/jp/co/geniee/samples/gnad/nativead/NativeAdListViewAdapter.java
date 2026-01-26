package jp.co.geniee.samples.gnad.nativead;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import jp.co.geniee.samples.R;
import jp.co.geniee.samples.gnad.common.CellData;
import jp.co.geniee.samples.gnad.common.ImageGetTask;
import jp.co.geniee.sdk.ads.nativead.GNNativeAd;

class NativeAdListViewAdapter extends ArrayAdapter<Object> {
    private LayoutInflater inflater;

    public NativeAdListViewAdapter(Context context, List<Object> list) {
        super(context, 0, list);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    class ViewHolder {
        ImageView imageView;
        TextView textView;
        TextView tvDescription;
        TextView tvSponsor;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_row_item, null);
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.textView);
            holder.imageView = convertView.findViewById(R.id.imageView);
            holder.tvDescription = convertView.findViewById(R.id.tvDescription);
            holder.tvSponsor = convertView.findViewById(R.id.tvSponsor);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Object cell = getItem(position);
        // Rendering SDK NativeAd content
        if (cell instanceof GNNativeAd) {
            holder.tvSponsor.setVisibility(View.VISIBLE);
            holder.tvSponsor.setText(String.format("Sponsor | %s", ((GNNativeAd) cell).advertiser));
            holder.textView.setText(((GNNativeAd) cell).title);
            holder.tvDescription.setText(((GNNativeAd) cell).description);
            new ImageGetTask(holder.imageView, ((GNNativeAd) cell).icon_url).execute();
            // Report SDK impression
            ((GNNativeAd) cell).onTrackingImpression();
        } else if (cell instanceof CellData) {
            holder.tvSponsor.setVisibility(View.GONE);
            holder.textView.setText(((CellData) cell).title);
            holder.tvDescription.setText(((CellData) cell).content);
            holder.imageView.setTag(((CellData) cell).imgURL);
            new ImageGetTask(holder.imageView, ((CellData) cell).imgURL).execute();
        }
        return convertView;
    }

}
