# Geniee Android SDK

Geniee SDK for Android provides a unified interface to display ads from Geniee SSP and major ad networks through a single integration.

## SDK & Libraries

### Core SDK

| Library | Maven                                | Description                                                                                                                         |
| :------ | :----------------------------------- | :---------------------------------------------------------------------------------------------------------------------------------- |
| GNAdSDK | `jp.co.geniee.gnadsdk:GNAdSDK:8.9.0` | Core SDK. Handles ad requests, rendering, and mediation logic. All mediation adapters below are bundled as transitive dependencies. |

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

### Google / AppLovin MAX / ironSource Mediation Adapters

These adapters allow Google AdMob, Google Ad Manager, AppLovin MAX, or ironSource to mediate Geniee ads. Install separately if needed.

| Library                                    | Maven                                                                                | Description                                          |
| :----------------------------------------- | :----------------------------------------------------------------------------------- | :--------------------------------------------------- |
| GNAdGoogleMediationAdapter                 | `jp.co.geniee.gnadgooglemediationadapter:GNAdGoogleMediationAdapter:25.2.0.1`        | Adapter for Google AdMob mediation                   |
| GNAdMobAdManagerMediationAdapter           | `jp.co.geniee.gnadmobadmanageradapter:GNAdMobAdManagerMediationAdapter:25.4.0.0`     | Adapter for Google Ad Manager mediation (Legacy SDK) |
| GNAdGMANextGenAdManagerMediationAdapter    | `jp.co.geniee:GNAdGMANextGenAdManagerMediationAdapter:1.3.0.0`                       | Adapter for Google Ad Manager mediation (GMA Next-Gen SDK) |
| GNAdIronSourceMediationAdapter             | `jp.co.geniee.gnadironsourcemediationadapter:GNAdIronSourceMediationAdapter:9.2.0.1` | Adapter for ironSource mediation                     |
| GNAdMAXMediationAdapter                    | `jp.co.geniee.gnadmaxmediationadapter:GNAdMAXMediationAdapter:13.6.4.0`              | Adapter for AppLovin MAX mediation (see [AppLovin MAX Mediation](#applovin-max-mediation)) |

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
    implementation 'jp.co.geniee.gnadsdk:GNAdSDK:8.9.0'
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
        String network = gnAdView.getWinnerNetworkName(); // network that filled the request
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

Available ad sizes: `W320H50`, `W320H48`, `W300H250`, `W728H90`, `W468H60`, `W120H600`, `W160H600`, `W320H100`, `W57H57`, `W76H76`, `W480H32`, `W768H66`, `W1024H66`

#### Integrating in XML

You can declare a banner directly in a layout instead of building it in code:

```xml
<jp.co.geniee.gnadsdk.banner.GNAdView
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/gnAdView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:zoneId="YOUR_ZONE_ID"
    app:adSize="W320H50"
    app:useMediation="true" />
```

Then load it from code:

```java
GNAdView adView = findViewById(R.id.gnAdView);
// size / zone id / options are already applied from the XML attributes
adView.startAdLoop();
```

Supported attributes:

| Attribute | Type | Description |
|---|---|---|
| `app:zoneId` | string | Zone ID used to request the ad (can also be set with `setAppId()`). |
| `app:adSize` | string | A predefined size **name**, e.g. `W320H50`. Must be one of the names above; an unknown name throws `IllegalArgumentException` at inflation. Ignored when a custom size is given. |
| `app:adWidth` / `app:adHeight` | integer (dp) | Custom size; when both are set they take precedence over `adSize`. |
| `app:touchType` | enum | `touchDown`, `tap` or `tapAndFlick` (default). |
| `app:useMediation` | boolean | Enable SSP mediation. |
| `app:customAdNetworkEnabled` | boolean | Enable custom ad network testing. |
| `app:autoLoad` | boolean | Start loading right after inflation (requires `app:zoneId`). Default `false`. |

> `app:adSize` is a size **name** (string), not a pixel/format value — Geniee uses its own
> fixed size system, so this intentionally differs from the AdMob `adSize` attribute.
>
> An invalid `app:adSize` (a name not in the list above) is treated as a developer error and
> **throws `IllegalArgumentException`** during layout inflation — it does not silently fall back.
> For an arbitrary size use `app:adWidth` / `app:adHeight` (see [Custom size](#custom-size)).

#### Custom size

Use a custom width/height (in dp) from XML (`app:adWidth` / `app:adHeight` above) or from code:

```java
GNAdView adView = new GNAdView(context, GNAdSize.GNAdSizeCustom);
adView.setAppId("YOUR_ZONE_ID");
adView.showBannerWithSize(320, 100);  // width, height in dp
```

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
    public void fullscreenInterstitialAdDidReceiveAd(String adNetworkName) {
        // Ad loaded — ready to show. adNetworkName is the network that filled the request.
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

Lifecycle — no forwarding required. Starting with 8.9.0 the SDK observes the host
Activity's lifecycle automatically, so you no longer override `onStart`/`onResume`/
`onPause`/`onStop`/`onDestroy` to drive the ad. Just create the ad, load, and show.

> **Migration (< 8.9.0 → 8.9.0):** the `onStart()`, `onResume()`, `onPause()`,
> `onStop()`, and `onDestroy()` methods on `GNSFullscreenInterstitialAd` have been
> **removed**. Delete every `interstitialAd.onXxx()` forwarding call from your Activity;
> cleanup happens automatically when the host Activity is destroyed.

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
    public void rewardVideoAdDidReceiveAd(String adNetworkName) {
        // Ad loaded — ready to show. adNetworkName is the network that filled the request.
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

Lifecycle — no forwarding required. Starting with 8.9.0 the SDK observes the host
Activity's lifecycle automatically, so you no longer override `onStart`/`onResume`/
`onPause`/`onStop`/`onDestroy` to drive the ad. Just create the ad, load, and show.

> **Migration (< 8.9.0 → 8.9.0):** the `onStart()`, `onResume()`, `onPause()`,
> `onStop()`, and `onDestroy()` methods on `GNSRewardVideoAd` have been **removed**.
> Delete every `rewardAd.onXxx()` forwarding call from your Activity; cleanup happens
> automatically when the host Activity is destroyed.

## AppLovin MAX Mediation

`GNAdMAXMediationAdapter` lets AppLovin MAX serve Geniee ads as a custom network. Supported formats: Banner (320×50), Leader (728×90), MREC (300×250), Interstitial, and Rewarded. Requires `minSdk 24` (AppLovin MAX SDK 13.x).

### 1. Add dependency

```groovy
dependencies {
    implementation 'jp.co.geniee.gnadsdk:GNAdSDK:8.9.0'
    implementation 'jp.co.geniee.gnadmaxmediationadapter:GNAdMAXMediationAdapter:13.6.4.0'
}
```

The adapter brings in `com.applovin:applovin-sdk:13.6.4`. Keep rules for the adapter class are bundled in the AAR.

### 2. Set up the custom network in the MAX dashboard

1. **MAX → Mediation → Manage → Networks → Click here to add a Custom Network**.
2. Network Type: **SDK**, Custom Network Name: e.g. `Geniee`.
3. Android Adapter Class Name: `jp.co.geniee.gnadmaxmediationadapter.GNMaxMediationAdapter`.
4. In each ad unit, enable the Geniee network and set **Placement ID** to your Geniee zone ID.

### 3. Initialize MAX and load ads

Initialize AppLovin MAX with your SDK key, then load ads with the standard MAX APIs (`MaxAdView`, `MaxInterstitialAd`, `MaxRewardedAd`). No Geniee-specific code is needed.

```java
AppLovinSdkInitializationConfiguration initConfig =
        AppLovinSdkInitializationConfiguration.builder("YOUR_SDK_KEY", context)
                .setMediationProvider(AppLovinMediationProvider.MAX)
                .build();
AppLovinSdk.getInstance(context).initialize(initConfig, configuration -> {
    MaxInterstitialAd interstitialAd = new MaxInterstitialAd("YOUR_MAX_AD_UNIT_ID");
    interstitialAd.setListener(listener);
    interstitialAd.loadAd();
});
```

Use `AppLovinSdk.getInstance(context).showMediationDebugger()` to check that the Geniee adapter is detected. A full example is in `sample/javaSample` (`maxmediation` package).

## Ad Inspector

Ad Inspector is a debugging screen built into the SDK for verifying ad delivery during development and QA. It opens on top of your app and has two tabs:

- **Waterfall** — every entry in the zone's mediation waterfall, in order, with the ad network name, ASID, adapter class, status (`LOADED`, `NO_FILL`, `TIMEOUT`, `ADAPTER_MISSING`, `SDK_MISSING`, `ERROR`, …), load duration, and a `WIN` badge on the network that filled the request. From here you can also run a single-source test, which restricts delivery to one network so it can be checked in isolation.
- **Diagnostics** — integration check of the SDK and each mediation adapter, reporting which adapter classes and third-party SDKs are actually present in the build.

### Opening the inspector

Open it from any ad instance. The waterfall is built from the last ad request, so call `startAdLoop()` / `loadRequest()` first.

```java
import jp.co.geniee.gnadsdk.inspector.GNSAdInspectorError;
import jp.co.geniee.gnadsdk.inspector.GNSAdInspectorListener;

GNSAdInspectorListener listener = new GNSAdInspectorListener() {
    @Override
    public void onAdInspectorClosed(GNSAdInspectorError error) {
        // error is null when the inspector closed normally
        if (error != null) {
            Log.e("AdInspector", error.getMessage());
        }
    }
};

adView.openAdInspector(activity, listener);          // Banner
interstitialAd.openAdInspector(activity, listener);  // Fullscreen Interstitial
rewardAd.openAdInspector(activity, listener);        // Rewarded Video
```

Fullscreen interstitial and rewarded video also accept an explicit zone ID:

```java
interstitialAd.openAdInspector(activity, listener, "YOUR_ZONE_ID");
rewardAd.openAdInspector(activity, listener, "YOUR_ZONE_ID");
```

### Errors

`GNSAdInspectorError` is passed to `onAdInspectorClosed()` when the inspector could not be opened.

| Constant             | Code | Meaning                                                            |
| :------------------- | :--- | :----------------------------------------------------------------- |
| `ERROR_NULL_ACTIVITY` | 1    | The `Activity` passed in was `null`.                                |
| `ERROR_LAUNCH_FAILED` | 2    | The inspector Activity could not be started.                        |
| `ERROR_NO_WATERFALL`  | 3    | No ad request has been made yet — load an ad before opening.        |

### Custom ad networks

The inspector can register ad networks that are not part of the zone's configured waterfall, so a network can be tried before it is set up server-side. Enable it on the ad instance before loading:

```java
adView.setCustomAdNetworkEnabled(true);
```

Registered custom networks are shown in the waterfall alongside the configured ones, and are also requested during the actual ad load while the flag is enabled. Keep it off in production builds.

For complete implementation details, see the [official documentation](https://developers.genieegroup.com/android/).
