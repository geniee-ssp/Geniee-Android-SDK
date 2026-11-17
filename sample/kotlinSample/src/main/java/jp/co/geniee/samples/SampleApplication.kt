package jp.co.geniee.samples

import android.app.Application
import android.provider.Settings
import android.util.Log
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.common.RequestConfiguration
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import java.security.MessageDigest
import java.util.Locale

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeMobileAds()
    }

    // GMA Next-Gen SDK must be initialized once, at app launch, on a background thread,
    // before loading any ad (including Geniee ads mediated through Google Ad Manager).
    private fun initializeMobileAds() {
        Thread {
            // For debugging/testing only: register this device as a test device.
            // Remove this setting in production.
            val requestConfiguration = RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(testDeviceId()))
                .build()
            MobileAds.initialize(
                this,
                InitializationConfig.Builder(GMA_APP_ID)
                    .setRequestConfiguration(requestConfiguration)
                    .build()
            )
            Log.d(TAG, "GMA Next-Gen SDK initialized: " + MobileAds.isInitialized)
        }.start()
    }

    private fun testDeviceId(): String {
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).orEmpty()
        return md5(androidId).uppercase(Locale.US)
    }

    private fun md5(s: String): String =
        MessageDigest.getInstance("MD5").digest(s.toByteArray())
            .joinToString("") { String.format("%02x", it.toInt() and 0xFF) }

    companion object {
        private const val TAG = "SampleApplication"

        // AdMob app ID of the Geniee sample app (Google Mediation screens).
        const val GMA_APP_ID = "ca-app-pub-2963406282877816~5372623241"
    }
}
