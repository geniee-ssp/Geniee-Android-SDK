package jp.co.geniee.samples.scrollbanner.inview;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import jp.co.geniee.samples.R;

public class GNAdSampleScrollBanner extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragmentbase);

        // Fragmentを作成します
        TestFragment fragment1 = new TestFragment();
        TestFragment2 fragment2 = new TestFragment2();
        TestFragment2 fragment3 = new TestFragment2();

        // Fragmentの追加や削除といった変更を行う際は、Transactionを利用します
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragmentlayout1,fragment2);
        transaction.add(R.id.fragmentlayout2,fragment1);
        transaction.add(R.id.fragmentlayout3,fragment3);
        transaction.commit();
    }
}
