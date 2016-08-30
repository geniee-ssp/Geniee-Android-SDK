package jp.co.geniee.samples.multiplebanner;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.co.geniee.gnadsdk.banner.GNAdView;
import jp.co.geniee.gnadsdk.banner.GNAdViewRequest;
import jp.co.geniee.gnadsdk.banner.GNAdViewRequestListener;
import jp.co.geniee.gnadsdk.common.GNAdLogger;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MultipleBannerSampleActivity extends ListActivity implements GNAdViewRequestListener {
	GNAdViewRequest multiAdViewRequest;

	private boolean loading = false;
	private GNQueue queueAds = new GNQueue(100);
	private ArrayList<Object> cellDataList = new ArrayList<Object>();
	private long timeStart, timeEnd;
	private ListViewAdapter mAdapter;
	private OnScrollListener mScrollListener;
	private OnItemClickListener mClickListener;
	private View mFooter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("MultipleBannerSampleActivity", "onCreate");
		setContentView(R.layout.multi_banner_ad_sample);
		ListView listView = getListView();
		listView.addFooterView(getFooter());
		listView.setAdapter(getAdapter());
		listView.setOnScrollListener(getScrollListener());
		listView.setOnItemClickListener(getClickListener());
		timeStart = System.currentTimeMillis();

		// Initialize SDK GNAdViewRequest
		multiAdViewRequest = new GNAdViewRequest(this, "YOUR_SSP_APP_ID");
		multiAdViewRequest.setAdListener(this);
		//multiAdViewRequest.setGeoLocationEnable(true);
		//multiAdViewRequest.setLogPriority(GNAdLogger.INFO);
		multiAdViewRequest.loadAds(this);

		// Load cell list
		requestCellDataListAsync();
	}
	
	@Override
	public void onGNAdViewsLoaded(GNAdView[] adViews) {
		timeEnd = System.currentTimeMillis();
		Log.d("MultipleBannerSampleActivity","adViews loaded in seconds:" + ((timeEnd - timeStart)/1000.0));
		for(int i=0; i<adViews.length; i++) {
			queueAds.enqueue(adViews[i]);
		}
	}

	@Override
	public void onGNAdViewsFailedToLoad() {
		Log.w("MultipleBannerSampleActivity","onGNAdViewsFailedToLoad");
		
	}
	
	@Override
	public boolean onShouldStartInternalBrowserWithClick(String landingURL) {
		Log.d("MultipleBannerSampleActivity","onShouldStartInternalBrowserWithClick : " + landingURL);
		return false;
	}
	
	private void requestCellDataListAsync() {
		loading  = true;
		Handler mHandler = new Handler();
		mHandler.postDelayed(new Runnable() { 
			public void run() { 
				createCellDataList();
				loading = false;
			}
		},1000);
	}
	
	private void createCellDataList() {
		for (int i = 0; i < 20; i++) {
			if (queueAds.size() > 0) {
					Object ad = queueAds.dequeue();
					cellDataList.add(ad);
			} else {
				cellDataList.add(new MyCellData());
			}
		}
		mAdapter.notifyDataSetChanged();
	}

	private OnScrollListener getScrollListener() {
		if (mScrollListener == null) {
			mScrollListener = new OnScrollListener() {
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
					if (totalItemCount-1 > 0 && totalItemCount-1 == cellDataList.size() && !loading && 
							totalItemCount == firstVisibleItem + visibleItemCount) {
						Log.d("MultipleBannerSampleActivity", "load additional cell list");
						multiAdViewRequest.loadAds(MultipleBannerSampleActivity.this);
						requestCellDataListAsync();
					}
				}
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {}
			};
		}
		return mScrollListener;
	}
	
	private OnItemClickListener getClickListener() {
		if (mClickListener == null) {
			mClickListener = new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					ListView listView = (ListView)parent;
					Object cell = listView.getItemAtPosition(position);
					if (cell instanceof GNAdView) {
						//The GNAdView can automatically respond to click
					}
				}
				
			};
		}
		return mClickListener;
	}

	private ListAdapter getAdapter() {
		if (mAdapter == null) {
			mAdapter = new ListViewAdapter(this, cellDataList);
		}
		return mAdapter;
	}

	private View getFooter() {
		if (mFooter == null) {
			mFooter = getLayoutInflater().inflate(R.layout.listview_footer, null);
		}
		return mFooter;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d("MultipleBannerSampleActivity", "onResume");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d("MultipleBannerSampleActivity", "onPause");
	}
	
	@Override
	protected void onDestroy() {
		Log.d("MultipleBannerSampleActivity", "onDestroy");
		MemCache.clear();
		System.gc();
		super.onDestroy();
	}
	
	class ListViewAdapter extends ArrayAdapter<Object> {
		private static final int TYPE_ITEM = 0;
		private static final int TYPE_AD = 1;
		private static final int TYPE_MAX_COUNT = TYPE_AD + 1;
		
		private LayoutInflater inflater;

		public ListViewAdapter(Context context,  List<Object> list) {
			super(context, 0, list);
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public int getItemViewType(int position) {
			return (cellDataList.get(position) instanceof GNAdView) ? TYPE_AD : TYPE_ITEM;
		}
		
		@Override
		public int getViewTypeCount() {
			return TYPE_MAX_COUNT;
		}
		
		@Override
		public int getCount() {
			return cellDataList.size();
		}
		
		@Override
		public Object getItem(int position) {
			return cellDataList.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		class ViewHolder {
			TextView textView;
			ImageView imageView;
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
						holder.adLayout = (LinearLayout)convertView.findViewById(R.id.ad_layout);
						break;
					case TYPE_ITEM:
						convertView = inflater.inflate(R.layout.list_item, null);
						holder.textView = (TextView) convertView.findViewById(R.id.textView);
						holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
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
				Log.d("getView", "show GNAdView in cell Num:" + position);
				if (adView.getParent() == null) {
					holder.adLayout.addView(adView, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
					adView.showBannerWithSize(300, 100);
					adView.startAdLoop();
					Log.d("getView", "Add GNAdView : " + adView.toString());
				} else {
					Log.d("getView", "Show GNAdView : " + adView.toString());
				}
			} else {
				holder.textView.setText(((MyCellData)cell).title + "No." + position);
				holder.imageView.setTag(((MyCellData)cell).imgURL);
				new ImageGetTask(holder.imageView, ((MyCellData)cell).imgURL).execute();
			}
			return convertView;
		}
	}
}



class ImageGetTask extends AsyncTask<Void,Void,Bitmap> {
	protected String url;
	protected Context context;
	protected ImageView imageView;
	private String tag;
	public ImageGetTask(ImageView imageView, String url) {
		this.imageView = imageView;
		this.url = url;
		tag = imageView.getTag().toString();
	}

	@Override
	public void onPreExecute() {
		Bitmap bitmap = MemCache.getImage(url);
		if (bitmap != null) {
			if (tag.equals(imageView.getTag())) {
				imageView.setImageBitmap(bitmap);
			}  
			cancel(true);
			return;
		}
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
		Bitmap bitmap;
		try {
			URL imageUrl = new URL(url);
			InputStream input;
			input = imageUrl.openStream();
			bitmap = BitmapFactory.decodeStream(input);
			input.close();
			MemCache.setImage(url, bitmap);
			return bitmap;
		} catch (MalformedURLException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (tag.equals(imageView.getTag())) {
			imageView.setImageBitmap(bitmap);
		}
	}
}

