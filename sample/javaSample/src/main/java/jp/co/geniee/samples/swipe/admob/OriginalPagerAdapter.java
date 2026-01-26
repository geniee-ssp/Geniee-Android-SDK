package jp.co.geniee.samples.swipe.admob;

import android.app.Activity;

import androidx.viewpager.widget.PagerAdapter;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class OriginalPagerAdapter extends PagerAdapter {

    private Activity mActivity;

    private ArrayList<Integer> mList;

    //広告用のリスナーを登録するAdLoader
    private AdAdapter mAdAdapter1;
    private String mUnitId = "";
    private String mAdSize = "";

    /**
     * コンストラクタ.
     */
    public OriginalPagerAdapter(Activity activity) {
        mActivity = activity;

        mList = new ArrayList<Integer>();

        //MobileAds.openDebugMenu(mActivity , mActivity.getResources().getString(R.string.ad_unit_id));
        //事前ロードを実施する
        //mAdAdapter1.adLoad();

    }

    /**
     * リストにアイテムを追加する.
     *
     * @param item アイテム
     */
    public void add(Integer item) {
        mList.add(item);
    }


    public void setUnitId(String unitId) {
        mUnitId = unitId;
    }
    public void setAdSize(String adSize) {
        mAdSize = adSize;
    }

    @Override
    public RelativeLayout instantiateItem(ViewGroup container, int position) {
        final RelativeLayout relativeLayout = new RelativeLayout(mActivity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        relativeLayout.setLayoutParams(params);
        relativeLayout.setGravity(Gravity.CENTER);

        // リストから取得
        Integer item = mList.get(getCount() - position - 1);
        if (position == 1) {
            //AdAdapterクラスを呼び出す
            //ToDO 呼び出すUnitIdを変更しています。（動画と純広告のテスト画像のみ表示するID）
            mAdAdapter1 = new AdAdapter(mActivity,
                    mUnitId,
                    mAdSize
                    );
            mAdAdapter1.addAdView(relativeLayout);
        } else {
            // TextView を生成
            TextView textView = new TextView(mActivity);
            textView.setText("Page:" + (getCount() - position));
            textView.setTextSize(30);
            textView.setTextColor(item);
            textView.setGravity(Gravity.CENTER);

            relativeLayout.addView(textView);
        }

        container.addView(relativeLayout);

        return relativeLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // コンテナから View を削除（View初期化処理）
        container.removeView((View) object);
        getItemPosition(object);
        Log.d("Page", "v: Page" + position);

        if (position == 0) {
            mAdAdapter1.destroyView();
        }
    }

    @Override
    public int getCount() {
        // リストのアイテム数を返す
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        // Object 内に View が存在するか判定する
        return view == (RelativeLayout) object;
    }

    //TODO configurationChangedメソッドを追加してください
    public void configurationChanged() {
        if (mAdAdapter1 != null) {
            mAdAdapter1.configurationChanged();
        }
    }
}

