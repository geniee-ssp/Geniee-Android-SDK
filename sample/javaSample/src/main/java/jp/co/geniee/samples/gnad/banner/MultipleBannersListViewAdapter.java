package jp.co.geniee.samples.gnad.banner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jp.co.geniee.samples.R;
import jp.co.geniee.samples.gnad.common.CellData;
import jp.co.geniee.samples.gnad.common.ImageGetTask;
import jp.co.geniee.gnadsdk.banner.GNAdView;

class MultipleBannersListViewAdapter extends ArrayAdapter<Object> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_AD = 1;
    private static final int TYPE_MAX_COUNT = TYPE_AD + 1;

    private LayoutInflater inflater;

    private ArrayList<Object> dataList = new ArrayList<>();

    public MultipleBannersListViewAdapter(Context context, List<Object> list) {
        super(context, 0, list);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.dataList = (ArrayList<Object>) list;
    }

    @Override
    public int getItemViewType(int position) {
        return (dataList.get(position) instanceof GNAdView) ? TYPE_AD : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        ImageView imageView;
        TextView textView;
        TextView tvDescription;
        LinearLayout adLayout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        int type = getItemViewType(position);
        if (convertView == null) {
            holder = new ViewHolder();
            switch (type) {
                case TYPE_AD:
                    convertView = inflater.inflate(R.layout.ad_item, null);
                    holder.adLayout = convertView.findViewById(R.id.ad_layout);
                    break;
                case TYPE_ITEM:
                    convertView = inflater.inflate(R.layout.listview_row_item, null);
                    holder.textView = convertView.findViewById(R.id.textView);
                    holder.imageView = convertView.findViewById(R.id.imageView);
                    holder.tvDescription = convertView.findViewById(R.id.tvDescription);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Object cell = getItem(position);
        // Rendering SDK GNAdView content
        if (cell instanceof GNAdView) {
            holder.adLayout.removeAllViews();
            GNAdView adView = (GNAdView)cell;
            if (adView.getParent() == null) {

                holder.adLayout.addView(adView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                adView.showBannerWithSize(300, 100);
                adView.startAdLoop();
            }
        } else if(cell instanceof CellData){

            holder.textView.setText(((CellData) cell).title);
            holder.tvDescription.setText(((CellData) cell).content);
            holder.imageView.setTag(((CellData) cell).imgURL);
            new ImageGetTask(holder.imageView, ((CellData) cell).imgURL).execute();
        }
        return convertView;
    }
}
