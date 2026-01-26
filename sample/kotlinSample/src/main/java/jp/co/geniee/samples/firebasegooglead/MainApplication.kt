package jp.co.geniee.samples.firebasegooglead

import com.google.android.gms.ads.MobileAds

class MainApplication : android.app.Application() {

    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this)
    }
}
