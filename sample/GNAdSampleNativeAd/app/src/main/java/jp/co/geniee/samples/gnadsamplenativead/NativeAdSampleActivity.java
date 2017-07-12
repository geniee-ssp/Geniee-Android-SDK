package jp.co.geniee.samples.gnadsamplenativead;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.co.geniee.gnadsdk.common.GNAdLogger;
import jp.co.geniee.sdk.ads.nativead.GNNativeAd;
import jp.co.geniee.sdk.ads.nativead.GNNativeAdRequest;
import jp.co.geniee.sdk.ads.nativead.GNNativeAdRequestListener;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NativeAdSampleActivity extends ListActivity implements GNNativeAdRequestListener {
	GNNativeAdRequest nativeAdRequest;


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

		setContentView(R.layout.activity_native_ad_sample);
		Log.d("NativeAdSampleActivity", "onCreate");
		ListView listView = getListView();
		listView.addFooterView(getFooter());
		listView.setAdapter(getAdapter());
		listView.setOnScrollListener(getScrollListener());
		listView.setOnItemClickListener(getClickListener());
		timeStart = System.currentTimeMillis();

		// Initialize SDK GNNativeAdRequest
		nativeAdRequest = new GNNativeAdRequest(this, "1111");
		nativeAdRequest.setAdListener(this);
		//nativeAdRequest.setGeoLocationEnable(true);
		//nativeAdRequest.setLogPriority(GNAdLogger.INFO);
		nativeAdRequest.loadAds(this);
		
		// Load cell list
		requestCellDataListAsync();
	}

	@Override
	public void onNativeAdsLoaded(GNNativeAd[] nativeAds) {
		timeEnd = System.currentTimeMillis();
		Log.i("NativeAdSampleActivity","NativeAds loaded in seconds:" + ((timeEnd - timeStart)/1000.0));
		for(int i=0; i<nativeAds.length; i++) {
			queueAds.enqueue(nativeAds[i]);
		}
	}

	@Override
	public void onNativeAdsFailedToLoad() {
		Log.w("NativeAdSampleActivity","onNativeAdsFailedToLoad");
	}

	@Override
	public boolean onShouldStartInternalBrowserWithClick(String landingURL) {
		Log.i("NativeAdSampleActivity","onShouldStartInternalBrowserWithClick : " + landingURL);
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
						Log.d("OnScrollListener", "load additional list cells");
						nativeAdRequest.loadAds(NativeAdSampleActivity.this);
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
					if (cell instanceof GNNativeAd) {
						// Report SDK click
						((GNNativeAd)cell).onTrackingClick();
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
		Log.d("NativeAdSampleActivity", "onResume");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d("NativeAdSampleActivity", "onPause");
	}
	
	@Override
	protected void onDestroy() {
		Log.d("NativeAdSampleActivity", "onDestroy");
		MemCache.clear();
		System.gc();
		super.onDestroy();
	}
}

class ListViewAdapter extends ArrayAdapter<Object> {
	private LayoutInflater inflater;

	public ListViewAdapter(Context context,  List<Object> list) {
		super(context, 0, list);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	class ViewHolder {
		TextView textView;
		ImageView imageView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item, null);
			holder = new ViewHolder();
			holder.textView = (TextView) convertView.findViewById(R.id.textView);
			holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Object cell = getItem(position);
		// Rendering SDK NativeAd content
		if (cell instanceof GNNativeAd) {
			holder.textView.setText(((GNNativeAd)cell).title);
			holder.imageView.setTag(((GNNativeAd)cell).icon_url);
			new ImageGetTask(holder.imageView, ((GNNativeAd)cell).icon_url).execute();
			// Report SDK impression
			((GNNativeAd)cell).onTrackingImpression();
		} else {
			holder.textView.setText(((MyCellData)cell).title + "No." + position);
			holder.imageView.setTag(((MyCellData)cell).imgURL);
			new ImageGetTask(holder.imageView, ((MyCellData)cell).imgURL).execute();
		}
		return convertView;
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

