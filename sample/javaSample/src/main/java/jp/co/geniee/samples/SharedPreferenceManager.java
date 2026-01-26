package jp.co.geniee.samples;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

public class SharedPreferenceManager {

    public final static String SINGLE_BANNER_ZONE_ID = "SINGLE_BANNER_ZONE_ID";
    public final static String MULTIPLE_BANNERS_ZONE_ID = "MULTIPLE_BANNERS_ZONE_ID";
    public final static String NATIVE_AD_ZONE_ID = "NATIVE_AD_ZONE_ID";
    public final static String INTERSTITIAL_AD_ZONE_ID = "INTERSTITIAL_AD_ZONE_ID";
    public final static String VAST_AD_ZONE_ID = "VAST_AD_ZONE_ID";
    public final static String REWARDED_VIDEO_AD_ZONE_ID = "REWARDED_VIDEO_AD_ZONE_ID";

    private static SharedPreferenceManager sprefMrgSingleton;
    private Context mContext;
    private SharedPreferences sharedPreference;
    private SharedPreferences.Editor editor;

    private SharedPreferenceManager(Context context) {
        mContext = context;
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = sharedPreference.edit();
    }

    public static SharedPreferenceManager getInstance(Context context) {
        if (sprefMrgSingleton == null) {
            sprefMrgSingleton = new SharedPreferenceManager(context);
        }
        return sprefMrgSingleton;
    }

    public void putStringSet(String key, Set<String> set) {
        editor.putStringSet(key, set);
        editor.apply();
    }

    public Set<String> getStringSet(String key) {
        return sharedPreference.getStringSet(key, null);
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return sharedPreference.getString(key, "");
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key) {
        return sharedPreference.getInt(key, 0);
    }

    public void putFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.apply();
    }

    public float getFloat(String key) {
        return sharedPreference.getFloat(key, 0);
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key) {
        return sharedPreference.getBoolean(key, false);
    }

}
