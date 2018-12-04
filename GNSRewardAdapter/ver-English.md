Let's keep the past version of jar so that it can be easily investigated with inquiries support etc in the future.
# GNSRewardAdapter-2.3.1
GNAdSDK-3.2.1
- Vungle: 6.2.5 --> 6.3.17

# GNSRewardAdapter-2.3.0
GNAdSDK-3.2.0
- targetSdkVersion from 26 to 28

# GNSRewardAdapter-2.2.0
GNAdSDK-3.1.3
 - CAReward: 2.0.0 -> 2.4.0

# GNSRewardAdapter-2.1.0
GNAdSDK-3.1.0

- Implement Maven and Proguard
- Fixed a bug that AdColony failed at the second Load
- Upgrade all latest 3rd SDKs for Android
 - Maio: 1.0.8 -> 1.1.1
 - Tapjoy: 11.11.1 -> 11.12.2
 - Vungle: 4.0.3 --> 6.2.5
 - CAReward: Keep


# GNSRewardAdapter-2.0.0
GNAdSDK-3.0.0

- Upgrade all latest 3rd SDKs for Android
 - Maio: 1.0.5 -> 1.0.8
 - AppLovin: 6.3.2 -> 8.0.1
 - UnitAds: 2.0.8 -> 2.2.0
 - Adcolony: 3.1.2 -> 3.3.3
 - Tapjoy: 11.11.0 -> 11.12.1
 - Vungle: Keep 4.0.3
 - Nend: 4.0.1 -> 4.0.4
 - AMoAd: Current -> 5.2.1
 - CAReward: Keep

# GNSRewardAdapter-1.3.10
- Video Reward Mediation → Implement Amoad

# GNSRewardAdapter-1.3.9
- Video Reward Mediation → Implement AmoadVideo Reward Mediation → Implement UnityAds

# GNSRewardAdapter-1.3.8
- Video Reward Mediation → Implement Nend

# GNSRewardAdapter-1.3.7
- Video Reward Mediation → Implement Tapjoy

# GNSRewardAdapter-1.3.6
- With 1.5.1 version upgrade of CAReward, GNSAdapterCARewardRewardVideoAd correction

# GNSRewardAdapter-1.3.5
- Corresponding version of GNAdSDK-2.2.2
- Video Rewards Android's ReqImp processing from webview to http processing
- Movie Rewards ADNW loaded last time on Android was fixed so that it will not be initialized the next time it loads
- Video renewal wifi / carrier complete addition of Android timeout Full repair of mediation logic
- Video Rewards Android's ADNW Stock Inventory Judgment Stop the load request process temporarily if it is in the background during callback, then retry again after returning
- Video Rewards Englishizing Android Error Messages
- Fixed video prevention to prevent Android's double load request
- Fixed to exclusively control success / failure call back notification to Video Reward Android application
- Movie Rewards Change timing of reward for Android AdColony to the timing to close from playback end
<br> https://geniee.atlassian.net/wiki/pages/viewpage.action?pageId=105469337


# 2017-06-07
GNSRewardAdapter - 1.3.4

- RewardVideo Android's Maio, Tapjoy's ReqImp too little bugfix
redmine: 14455

# 2017-05-30
GNSRewardAdapter-1.3.3

- Response of video rewarding Android didFailToLoadWithError not called
redmine: 14370

# 2017-05-24
GNSRewardAdapter-1.3.2

- Fixed a bug in which the whole ADNW playback process stops if sdk.key of ADNW part of video reward Android is incorrect

# 2017-05-24
GNSRewardAdapter-1.3.1 (not yet released)

- Video Rewards Android's tapjoy Log Level Correction
- Video Rewards Do not call tapjoy, careward, vungle didFailToLoadWithError on Android

# 2017-04-17
GNSRewardAdapter-1.3.0

- Video Reward Add Vungle to Android

# 2017-04-03
GNSRewardAdapter-1.2.0

- Video Reward Add Tapjoy to Android


