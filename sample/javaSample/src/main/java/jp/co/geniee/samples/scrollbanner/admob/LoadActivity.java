package jp.co.geniee.samples.scrollbanner.admob;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import jp.co.geniee.samples.R;

public class LoadActivity extends AppCompatActivity {

    private final String  TAG = "LoadActivity";
    private Button mLoadRequestBtn;
    private String mAdSize = "320x50";
    private static final String default_ad_unit_id = "ca-app-pub-3940256099942544/9214589741";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrollbanner_admob_load);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[] {
                        "320x50",
                        "300x250",
                        "320x100"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.sizelist);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                Spinner spinner = (Spinner)parent;
                String adSize = (String)spinner.getSelectedItem();
                mAdSize = adSize;
                Log.d(TAG, "size=" + mAdSize);
                SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
                SharedPreferences.Editor preferencesEdit = preferences.edit();
                preferencesEdit.putInt("adSizeItemId", (int)spinner.getSelectedItemId());
                preferencesEdit.commit();
            }
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

        final EditText unitIdEdit = (EditText) findViewById(R.id.gns_sample_unitid_edit);
        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);

        String defaultZoneID = preferences.getString("unitId", default_ad_unit_id);
        unitIdEdit.setText(defaultZoneID);

        spinner.setSelection(preferences.getInt("adSizeItemId", 0));

        mLoadRequestBtn = (Button)findViewById(R.id.gns_sample_load_button);
        mLoadRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String unitId = unitIdEdit.getText().toString();
                if (unitId.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Missing unit id", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
                SharedPreferences.Editor preferencesEdit = preferences.edit();
                preferencesEdit.putString("unitId", unitId);
                preferencesEdit.commit();

                Intent intent = new Intent();
                intent.setClassName(getPackageName(), "jp.co.geniee.samples.scrollbanner.admob.GNAdAdMobSampleScrollBanner");
                intent.putExtra("unitId", unitId);
                intent.putExtra("adSize", mAdSize);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}