package jp.co.geniee.samples.scrollbanner.inview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import jp.co.geniee.samples.R;

public class TestFragment2 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // 先ほどのレイアウトをここでViewとして作成します
        return inflater.inflate(R.layout.fragment_subwindow_no_contants, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
