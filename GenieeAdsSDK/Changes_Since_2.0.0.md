## Changes Since 2.0.0
This file describes significant changes made to version 2.0.0 or later.

You need to change these by updating from version 1.5.6 or earlier.

### Banner Ad
  1. change the import path for GNAdView Ad.
    ```java
    import jp.co.geniee.gnadsdk.common.GNAdLogger;
    import jp.co.geniee.gnadsdk.banner.GNAdView;
    import jp.co.geniee.gnadsdk.banner.GNAdSize;
    import jp.co.geniee.gnadsdk.banner.GNTouchType;
    ```

  2. change the GNAdWebActivity path in AndroidManifest.xml.
    ```java
    <activity android:name="jp.co.geniee.gnadsdk.banner.GNAdWebActivity"
    ```

### Interstitial Ad
  1. change the import path for GNInterstitial Ad.
    ```java
    import jp.co.geniee.gnadsdk.common.GNAdLogger;
    import jp.co.geniee.gnadsdk.interstitial.GNInterstitial;
    import jp.co.geniee.gnadsdk.interstitial.GNInterstitial.GNInterstitialDialogListener;
    import jp.co.geniee.gnadsdk.interstitial.GNInterstitial.GNInterstitialListener;
    ```

  2. change the GNInterstitialActivity path in AndroidManifest.xml.
    ```java
    <activity android:name="jp.co.geniee.gnadsdk.interstitial.GNInterstitialActivity"
    ```

### PUSH SDK
  1. change the import path for GN_LivepassPushSDK.

    ```java
    import jp.co.geniee.gnadsdk.push.GN_LivepassPushSDK;
    import jp.co.geniee.gnadsdk.push.GN_LivepassPushSDKConfig;
    import jp.co.geniee.gnadsdk.push.GN_LivepassPushSDKDelegate;
    import jp.co.geniee.gnadsdk.push.GN_LivepassPushSDKLog;
    ```
