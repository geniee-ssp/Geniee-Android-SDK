package jp.co.geniee.gnadgooglerewardmediationsample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static String defaultUnitID = "YOUR_ADMOB_UNIT_ID";
    EditText mUnitIdEdit;
    SharedPreferences mPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUnitIdEdit = (EditText) findViewById(R.id.gns_sample_unitid_edit);
        mPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        defaultUnitID = mPreferences.getString("UnitID", defaultUnitID);
        mUnitIdEdit.setText(defaultUnitID);

        Button newButton = (Button) findViewById(R.id.reward_new_button);
        newButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveUnitId();
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), "jp.co.geniee.gnadgooglerewardmediationsample.RewardNewActivity");
                startActivity(intent);
            }
        });
        Button oldButton = (Button) findViewById(R.id.reward_old_button);
            oldButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveUnitId();
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), "jp.co.geniee.gnadgooglerewardmediationsample.RewardOldActivity");
                startActivity(intent);
            }
        });
    }
    private void saveUnitId() {
        String unitId = mUnitIdEdit.getText().toString();
        if (!unitId.isEmpty()) {
            SharedPreferences.Editor preferencesEdit = mPreferences.edit();
            preferencesEdit.putString("UnitID", unitId);
            preferencesEdit.commit();
        }
    }

}
