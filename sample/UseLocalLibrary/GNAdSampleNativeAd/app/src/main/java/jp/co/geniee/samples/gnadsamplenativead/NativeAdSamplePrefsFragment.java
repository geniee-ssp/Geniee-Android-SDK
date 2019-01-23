package jp.co.geniee.samples.gnadsamplenativead;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;

public class NativeAdSamplePrefsFragment extends PreferenceFragmentCompat {

    private static String TAG = "NativeAdSamplePrefsFragment";
    private EditTextPreference zoneIdPref;
    public static String extraZoneId = "extraZoneId";
    public static NativeAdSamplePrefsFragment newInstance() {
        return new NativeAdSamplePrefsFragment();
    }
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.pref_general, s);
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
                    editPref.setText(editPref.getText());
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
        PreferenceScreen imagePreference = (PreferenceScreen)getPreferenceScreen().findPreference("image");
        imagePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getContext(), NativeAdSampleImageActivity.class);
                intent.putExtra(extraZoneId, zoneIdPref.getText());
                startActivity(intent);
                return true;
            }
        });
        PreferenceScreen simpleVideoPreference = (PreferenceScreen)getPreferenceScreen().findPreference("simple_video");
        simpleVideoPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getContext(), NativeAdSampleSimpleVideoActivity.class);
                intent.putExtra(extraZoneId, zoneIdPref.getText());
                startActivity(intent);
                Log.i(TAG, "zoneIdPref.getText()=" + zoneIdPref.getText());
                return true;
            }
        });
        PreferenceScreen videoPreference = (PreferenceScreen)getPreferenceScreen().findPreference("video");
        videoPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getContext(), NativeAdSampleVideoActivity.class);
                intent.putExtra(extraZoneId, zoneIdPref.getText());
                startActivity(intent);
                return true;
            }
        });
    }

}
