# Geniee Android SDK

Geniee SDK for Android provides a unified interface to display ads from Geniee SSP and major ad networks through a single integration.

## SDK & Libraries

### Core SDK

| Library | Maven                                | Description                                                                                                                         |
| :------ | :----------------------------------- | :---------------------------------------------------------------------------------------------------------------------------------- |
| GNAdSDK | `jp.co.geniee.gnadsdk:GNAdSDK:8.7.5` | Core SDK. Handles ad requests, rendering, and mediation logic. All mediation adapters below are bundled as transitive dependencies. |

### Mediation Adapters (bundled in GNAdSDK)

These adapters are automatically included when you integrate `GNAdSDK`. No additional dependency declaration is needed.

| Adapter           | Maven                                               | Ad Network Version | Supported Formats                               |
| :---------------- | :-------------------------------------------------- | :----------------- | :---------------------------------------------- |
| AdColony          | `jp.co.geniee.mediation:adcolony:4.8.0.0`           | 4.8.0              | Fullscreen Interstitial, Rewarded Video         |
| AppLovin          | `jp.co.geniee.mediation:applovin:13.6.2.0`          | 13.6.2             | Fullscreen Interstitial, Rewarded Video         |
| Google Ad Manager | `jp.co.geniee.mediation:google-ad-manager:25.2.0.0` | 25.2.0             | Banner, Fullscreen Interstitial, Rewarded Video |
| i-mobile          | `jp.co.geniee.mediation:imobile:2.3.2.0`            | 2.3.2              | Fullscreen Interstitial                         |
| InMobi            | `jp.co.geniee.mediation:inmobi:10.1.3.0`            | 10.1.3             | Fullscreen Interstitial, Rewarded Video         |
| LINE              | `jp.co.geniee.mediation:line:3.1.1.0`               | 3.1.1              | Fullscreen Interstitial, Rewarded Video         |
| maio              | `jp.co.geniee.mediation:maio:2.0.8.0`               | 2.0.8              | Fullscreen Interstitial, Rewarded Video         |
| Pangle            | `jp.co.geniee.mediation:pangle:8.0.0.4.0`           | 8.0.0.4            | Banner, Fullscreen Interstitial, Rewarded Video |
| Tapjoy            | `jp.co.geniee.mediation:tapjoy:14.4.0.0`            | 14.4.0             | Fullscreen Interstitial, Rewarded Video         |
| Unity Ads         | `jp.co.geniee.mediation:unityads:4.18.0.0`          | 4.18.0             | Fullscreen Interstitial, Rewarded Video         |
| Vungle            | `jp.co.geniee.mediation:vungle:7.7.4.0`             | 7.7.4              | Fullscreen Interstitial, Rewarded Video         |

### Google / ironSource Mediation Adapters

These adapters allow Google AdMob, Google Ad Manager, or ironSource to mediate Geniee ads. Install separately if needed.

| Library                                    | Maven                                                                                | Description                                          |
| :----------------------------------------- | :----------------------------------------------------------------------------------- | :--------------------------------------------------- |
| GNAdGoogleMediationAdapter                 | `jp.co.geniee.gnadgooglemediationadapter:GNAdGoogleMediationAdapter:25.2.0.0`        | Adapter for Google AdMob mediation                   |
| GNAdMobAdManagerMediationAdapter           | `jp.co.geniee.gnadmobadmanageradapter:GNAdMobAdManagerMediationAdapter:25.4.0.0`     | Adapter for Google Ad Manager mediation (Legacy SDK) |
| GNAdGMANextGenAdManagerMediationAdapter    | `jp.co.geniee:GNAdGMANextGenAdManagerMediationAdapter:1.3.0.0`                       | Adapter for Google Ad Manager mediation (GMA Next-Gen SDK) |
| GNAdIronSourceMediationAdapter             | `jp.co.geniee.gnadironsourcemediationadapter:GNAdIronSourceMediationAdapter:9.2.0.0` | Adapter for ironSource mediation                     |

## Requirements

| Item               | Requirement      |
| :----------------- | :--------------- |
| Android minSdk     | 23 (Android 6.0) |
| Android compileSdk | 35               |
| Android targetSdk  | 34               |
| Java               | 17+              |
| Gradle             | 8.x              |
| AGP                | 8.5+             |

## Integration

For detailed setup instructions, API references, and advanced configuration, visit the official documentation:

**https://developers.genieegroup.com/android/**

### 1. Add Maven repositories

Add the following to your `settings.gradle`:

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // GNAdSDK
        maven {
            url 'https://raw.githubusercontent.com/geniee-ssp/Geniee-Android-SDK/master/repository'
        }
        // Pangle
        maven {
            url 'https://artifact.bytedance.com/repository/pangle'
        }
        // Tapjoy
        maven {
            name "Tapjoy's maven repo"
            url 'https://sdk.tapjoy.com/'
        }
        // maio
        maven {
            url 'https://imobile-maio.github.io/maven'
        }
        // i-mobile
        maven {
            url 'https://imobile.github.io/adnw-sdk-android'
        }
    }
}
```

### 2. Add dependency

Add to your module `build.gradle`:

```groovy
dependencies {
    implementation 'jp.co.geniee.gnadsdk:GNAdSDK:8.7.5'
}
```

This single dependency includes GNAdSDK and all mediation adapters.

## Ad Formats

### Banner

```java
import jp.co.geniee.gnadsdk.banner.GNAdEventListener;
import jp.co.geniee.gnadsdk.banner.GNAdSize;
import jp.co.geniee.gnadsdk.banner.GNAdView;

// Create a GNAdView with the desired ad size
GNAdView adView = new GNAdView(context, GNAdSize.W320H50);

// Add to your layout
LinearLayout layout = findViewById(R.id.ad_container);
layout.addView(adView);

// Set the listener
adView.setListener(new GNAdEventListener() {
    @Override
    public void onReceiveAd(GNAdView gnAdView) {
        // Ad loaded successfully
    }

    @Override
    public void onFaildToReceiveAd(GNAdView gnAdView) {
        // Ad failed to load
    }

    @Override
    public void onAdHidden(GNAdView adView) {}

    @Override
    public void onStartExternalBrowser(GNAdView gnAdView) {}

    @Override
    public void onStartInternalBrowser(GNAdView gnAdView) {}

    @Override
    public void onTerminateInternalBrowser(GNAdView gnAdView) {}

    @Override
    public boolean onShouldStartInternalBrowserWithClick(String url) {
        return false;
    }
});

// Set zone ID and start ad loop
adView.setAppId("YOUR_ZONE_ID");
adView.startAdLoop();
```

Available ad sizes: `W320H50`, `W320H48`, `W300H250`, `W728H90`, `W468H60`, `W120H600`, `W320H100`, `W57H57`, `W76H76`, `W480H32`, `W768H66`, `W1024H66`

Lifecycle:

```java
@Override
protected void onPause() {
    super.onPause();
    if (adView != null) adView.stopAdLoop();
}

@Override
protected void onDestroy() {
    if (adView != null) adView.clearAdView();
    super.onDestroy();
}
```

### Fullscreen Interstitial

```java
import jp.co.geniee.gnadsdk.fullscreeninterstitial.GNSFullscreenInterstitialAd;
import jp.co.geniee.gnadsdk.fullscreeninterstitial.GNSFullscreenInterstitialAdListener;
import jp.co.geniee.gnadsdk.common.GNSException;

// Create an instance with zone ID
GNSFullscreenInterstitialAd interstitialAd =
    new GNSFullscreenInterstitialAd("YOUR_ZONE_ID", activity);

// Set the listener
interstitialAd.setFullscreenInterstitialAdListener(new GNSFullscreenInterstitialAdListener() {
    @Override
    public void fullscreenInterstitialAdDidReceiveAd() {
        // Ad loaded — ready to show
    }

    @Override
    public void didFailToLoadWithError(GNSException e) {
        // Ad failed to load
    }

    @Override
    public void fullscreenInterstitialAdWillPresentScreen(String adName) {
        // Ad is being presented
    }

    @Override
    public void fullscreenInterstitialAdDidClose(String adName) {
        // Ad was closed
    }
});

// Load the ad
interstitialAd.loadRequest();

// Show when ready
if (interstitialAd.canShow()) {
    interstitialAd.show();
}
```

Lifecycle — forward all Activity lifecycle events:

```java

@Override protected void onStart()   { super.onStart();   if (interstitialAd != null) interstitialAd.onStart(); }

@Override protected void onResume()  { super.onResume();  if (interstitialAd != null) interstitialAd.onResume(); }

@Override protected void onPause()   { if (interstitialAd != null) interstitialAd.onPause();   super.onPause(); }

@Override protected void onStop()    { if (interstitialAd != null) interstitialAd.onStop();    super.onStop(); }

@Override protected void onDestroy() { if (interstitialAd != null) interstitialAd.onDestroy(); super.onDestroy(); }

```

### Rewarded Video

```java
import jp.co.geniee.gnadsdk.rewardvideo.GNSRewardVideoAd;
import jp.co.geniee.gnadsdk.rewardvideo.GNSRewardVideoAdListener;
import jp.co.geniee.gnadsdk.rewardvideo.GNSVideoRewardData;
import jp.co.geniee.gnadsdk.rewardvideo.GNSVideoRewardException;

// Create an instance with zone ID
GNSRewardVideoAd rewardAd = new GNSRewardVideoAd("YOUR_ZONE_ID", activity);

// Set the listener
rewardAd.setRewardVideoAdListener(new GNSRewardVideoAdListener() {
    @Override
    public void rewardVideoAdDidReceiveAd() {
        // Ad loaded — ready to show
    }

    @Override
    public void rewardVideoAdDidStartPlaying(GNSVideoRewardData data) {
        // Video started playing
    }

    @Override
    public void didRewardUserWithReward(GNSVideoRewardData data) {
        // User earned a reward: data.amount + data.type
    }

    @Override
    public void rewardVideoAdDidClose(GNSVideoRewardData data) {
        // Ad was closed
    }

    @Override
    public void didFailToLoadWithError(GNSVideoRewardException e) {
        // Ad failed to load
    }
});

// Load the ad
rewardAd.loadRequest(false);

// Show when ready
if (rewardAd.canShow()) {
    rewardAd.show();
}
```

Lifecycle — forward all Activity lifecycle events:

```java

@Override protected void onStart()   { super.onStart();   if (rewardAd != null) rewardAd.onStart(); }

@Override protected void onResume()  { super.onResume();  if (rewardAd != null) rewardAd.onResume(); }

@Override protected void onPause()   { if (rewardAd != null) rewardAd.onPause();   super.onPause(); }

@Override protected void onStop()    { if (rewardAd != null) rewardAd.onStop();    super.onStop(); }

@Override protected void onDestroy() { if (rewardAd != null) rewardAd.onDestroy(); super.onDestroy(); }

```

For complete implementation details, see the [official documentation](https://developers.genieegroup.com/android/).
