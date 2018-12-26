package jp.co.geniee.samples.gnadsamplenativead;

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import android.preference.PreferenceActivity;
import android.util.Log;

public class NativeAdSampleActivity extends PreferenceActivity {
    private static String TAG = "NativeAdSampleActivity";
    private EditTextPreference zoneIdPref;
    public static String extraZoneId = "extraZoneId";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        PreferenceScreen imagePreference = (PreferenceScreen)getPreferenceScreen().findPreference("image");
        imagePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getApplicationContext(), NativeAdSampleImageActivity.class);
                intent.putExtra(extraZoneId, zoneIdPref.getText());
                startActivity(intent);
                return true;
            }
        });
        PreferenceScreen simpleVideoPreference = (PreferenceScreen)getPreferenceScreen().findPreference("simple_video");
        simpleVideoPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getApplicationContext(), NativeAdSampleSimpleVideoActivity.class);
                intent.putExtra(extraZoneId, zoneIdPref.getText());
                startActivity(intent);
                Log.i(TAG, "zoneIdPref.getText()=" + zoneIdPref.getText());
                return true;
            }
        });
        PreferenceScreen videoPreference = (PreferenceScreen)getPreferenceScreen().findPreference("video");
        videoPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getApplicationContext(), NativeAdSampleVideoActivity.class);
                intent.putExtra(extraZoneId, zoneIdPref.getText());
                startActivity(intent);
                return true;
            }
        });
        zoneIdPref = (EditTextPreference)findPreference("zoneId");
        if (zoneIdPref.getText() == null || zoneIdPref.getText().length() < 1) {
            zoneIdPref.setSummary(getResources().getString(R.string.zone_id_hint));
        } else {
            zoneIdPref.setSummary(zoneIdPref.getText());
        }
        zoneIdPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                EditTextPreference editPref = (EditTextPreference)preference;
                if (editPref.getText() != null) {
                    editPref.getEditText().setSelection(editPref.getText().length());
                }
                return true;
            }
        });
        zoneIdPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {
                EditTextPreference editPref = (EditTextPreference)preference;
                editPref.setSummary(newValue.toString());
                return true;
            }
        });
    }
}