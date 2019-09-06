package jp.co.geniee.samples;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public abstract class BaseMenuActivity extends AppCompatActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initValues();

        setupListViewContents(getListViewContents());
    }

    private void initValues() {
        mListView = findViewById(R.id.listView);
    }

    private void setupListViewContents(final MenuItem[] items) {
        ArrayAdapter<MenuItem> listAdapter = new ArrayAdapter<MenuItem>(this, R.layout.simple_list_item, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = convertView;
                if (row == null) {
                    LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row = inflater.inflate(R.layout.simple_list_item, parent, false);
                }

                MenuItem item = items[position];

                TextView title = row.findViewById(R.id.text1);
                title.setText(item.getTitle());
                TextView subtitle = row.findViewById(R.id.text2);
                subtitle.setText(item.getSubtitle());

                return row;
            }
        };

        mListView.setAdapter(listAdapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = items[position].getIntent();

                if (intent != null) startActivity(intent);
            }
        };
        mListView.setOnItemClickListener(itemClickListener);
    }

    protected abstract MenuItem[] getListViewContents();
}
