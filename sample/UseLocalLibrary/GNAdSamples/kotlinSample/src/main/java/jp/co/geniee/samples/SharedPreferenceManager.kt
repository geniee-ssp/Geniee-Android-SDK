package jp.co.geniee.samples

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class SharedPreferenceManager private constructor(context: Context) {
    private val sharedPreference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val editor: SharedPreferences.Editor = sharedPreference.edit()

    fun putStringSet(key: String, set: Set<String>) {
        editor.putStringSet(key, set)
        editor.apply()
    }

    fun getStringSet(key: String): Set<String>? {
        return sharedPreference.getStringSet(key, null)
    }

    fun putString(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String): String {
        return sharedPreference.getString(key, "")
    }

    fun putInt(key: String, value: Int) {
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(key: String): Int {
        return sharedPreference.getInt(key, 0)
    }

    fun putFloat(key: String, value: Float) {
        editor.putFloat(key, value)
        editor.apply()
    }

    fun getFloat(key: String): Float {
        return sharedPreference.getFloat(key, 0f)
    }

    fun putBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(key: String): Boolean {
        return sharedPreference.getBoolean(key, false)
    }

    companion object {

        const val SINGLE_BANNER_ZONE_ID = "SINGLE_BANNER_ZONE_ID"
        const val MULTIPLE_BANNERS_ZONE_ID = "MULTIPLE_BANNERS_ZONE_ID"
        const val NATIVE_AD_ZONE_ID = "NATIVE_AD_ZONE_ID"
        const val INTERSTITIAL_AD_ZONE_ID = "INTERSTITIAL_AD_ZONE_ID"
        const val VAST_AD_ZONE_ID = "VAST_AD_ZONE_ID"
        const val REWARDED_VIDEO_AD_ZONE_ID = "REWARDED_VIDEO_AD_ZONE_ID"

        private var sprefMrgSingleton: SharedPreferenceManager? = null

        fun getInstance(context: Context): SharedPreferenceManager {
            if (sprefMrgSingleton == null) {
                sprefMrgSingleton = SharedPreferenceManager(context)
            }
            return sprefMrgSingleton!!
        }
    }

}
