
package jp.co.geniee.samples.swipe.inview;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class OriginalPagerAdapter extends PagerAdapter {

    private Activity mActivity;

    private ArrayList<Integer> mList;

    //広告用のリスナーを登録するAdLoader
    private AdAdapter mAdAdapter1;

    //AdMobのID(テスト用)
    private static final String default_ad_unit_id = "1573195";

/**
 * コンストラクタ.
 */
    public OriginalPagerAdapter(Activity activity) {
        mActivity = activity;

        mList = new ArrayList<Integer>();

    }

    /**
     * リストにアイテムを追加する.
     *
     * @param item アイテム
     */
    public void add(Integer item) {
        mList.add(item);
    }

    @Override
    public RelativeLayout instantiateItem(ViewGroup container, int position) {
        final RelativeLayout relativeLayout = new RelativeLayout(mActivity);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        relativeLayout.setGravity(Gravity.CENTER);

        // リストから取得
        Integer item = mList.get(position);
        if (position == getCount() - 1) {
            //ToDO 呼び出すUnitIdを変更しています。（動画と純広告のテスト画像のみ表示するID）
            mAdAdapter1 = new AdAdapter(mActivity, default_ad_unit_id);
            mAdAdapter1.addAdView(relativeLayout);
        } else {
            // TextView を生成
            TextView textView = new TextView(mActivity);
            textView.setText("Page:" + (position + 1));
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
        Log.d("Page", "destroyItem: Page" +position );
        mAdAdapter1.destroyView();
        //初期化後に再度LoadAdをし、広告の再読み込みをする

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
    /*
    //TODO configurationChangedメソッドを追加してください
    public void configurationChanged(){
        if(mAdAdapter1 != null){
            mAdAdapter1.configurationChanged();
        }
    }

     */

    public void destroyAdaper() {
        // コンテナから View を削除（View初期化処理）

        if(mAdAdapter1 != null) {
            mAdAdapter1.destroyView();
            //初期化後に再度LoadAdをし、広告の再読み込みをする
        }
    }
}

