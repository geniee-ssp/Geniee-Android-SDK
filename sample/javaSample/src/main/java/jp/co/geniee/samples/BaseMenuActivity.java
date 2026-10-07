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

import java.util.Locale;

public abstract class BaseMenuActivity extends AppCompatActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

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
                ViewHolder holder;
                if (row == null) {
                    LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row = inflater.inflate(R.layout.simple_list_item, parent, false);
                    holder = new ViewHolder(row);
                    row.setTag(holder);
                } else {
                    holder = (ViewHolder) row.getTag();
                }

                holder.bind(items[position]);
                return row;
            }
        };

        mListView.setAdapter(listAdapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = items[position].getIntent();

                if (intent != null) {
                    startActivity(intent);
                } else {
                    onMenuItemClick(position);
                }
            }
        };
        mListView.setOnItemClickListener(itemClickListener);
    }

    protected void onMenuItemClick(int position) {
    }

    protected abstract MenuItem[] getListViewContents();

    private static class ViewHolder {
        private final TextView icon;
        private final TextView title;
        private final TextView subtitle;

        ViewHolder(View row) {
            icon = row.findViewById(R.id.icon);
            title = row.findViewById(R.id.text1);
            subtitle = row.findViewById(R.id.text2);
        }

        void bind(MenuItem item) {
            String titleText = item.getTitle() != null ? item.getTitle().trim() : "";
            icon.setText(titleText.isEmpty() ? "" : titleText.substring(0, 1).toUpperCase(Locale.US));
            title.setText(titleText);

            // Subtitles are comma-separated feature lists, e.g. "Banner,Native".
            String subtitleText = item.getSubtitle() != null ? item.getSubtitle().trim() : "";
            subtitle.setText(subtitleText.replaceAll("\\s*,\\s*", " · "));
            subtitle.setVisibility(subtitleText.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }
}
