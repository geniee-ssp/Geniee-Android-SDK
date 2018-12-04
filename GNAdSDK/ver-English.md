Let's keep the past version of jar so that it can be easily investigated with inquiries support etc in the future.

# 2018-11-19
GNAdSDK-3.3.0

- Fixed bugs that are being implemented even when ad rotation in banner format is not inView
- Add constructor when touchType is not set in banner format
- Supports some functions of Mraid in banner format (beta version)
	- getMaxSize
	- getCurrentPosition
	- isViewable

# 2018-09-27
GNAdSDK-3.2.1

- Fixed bug that the screen was not displayed when displaying CAReward's banner video

# 2018-08-31
GNAdSDK-3.2.0

- Deprecated code deletion (surrounding webview, wifi-carrier judgment part, latitude and longitude around, audio correspondence of video)
- google-play-services.jar to maven
- org.apache.httpcomponents to HttpURLConnection
- MinSdkVersion of GNADSDK from 11 to 14
- targetSdkVersion of GNADSDK from 23 to 28

# 2018-07-31
GNAdSDK-3.1.3

- the DefaultHttpClient ssl support

# 2018-07-25
GNAdSDK-3.1.2

- To change the http of the common class to https

# 2018-06-25
GNAdSDK-3.1.0

- Implement Maven and Proguard
- Fixed a bug that AdColony failed at the second Load
- Upgrade all latest 3rd SDKs for Android
 - Maio: 1.0.8 -> 1.1.1
 - Tapjoy: 11.11.1 -> 11.12.2
 - Vungle: 4.0.3 --> 6.2.5
 - CAReward: Keep

# 2018-05-11
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

# 2018-04-26
GNAdSDK-2.2.11


- English logging

# 2018-03-26
GNAdSDK-2.2.10

- Video Reward Mediation → Implement Amoad

# 2017-11-30
GNAdSDK-2.2.8

- Video Reward Mediation → Implement Nend


# 2017-09-12
GNAdSDK-2.2.7

- Improvement of multiLoad
- When sending multiple zones Obtaining advertisement Improved that callback will not be successful if json is empty


# 2017-08-31
GNAdSDK-2.2.6

- Improved banner clearance detection
- Change the timing of application inventory notification after detection of the empty frame
- Delete wait time of 2 seconds from detection to display
- In the first acquisition of advertisement, a plurality of advertisements are acquired and a vacant frame is detected

# GNAdSDK-2.2.4
- Improvement of initialization of GNSZoneInfoSource

# GNAdSDK-2.2.3
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

# 2017-05-31
GNAdSDK-2.2.1

- Response of video rewarding Android didFailToLoadWithError not called
redmine: 14370

# 2017-05-26
GNAdSDK - 2.2.0 (not yet released)

- Native_Ad_optout_support correspondence

# 2017-05-24
GNAdSDK-2.1.13

- RewardVideo Fixed so that the second advertisement will be displayed correctly even if loadRequest is done at Android outcome event

# 2017-05-24
GNAdSDK - 2.1.12 (not yet released)

- Fixed a bug in which the whole ADNW playback process stops if sdk.key of ADNW part of video reward Android is wrong.

# 2017-05-24
GNAdSDK - 2.1.11 (not yet released)

- Movie Rewards ADNW_TEST_MODE Add & APIURL can be changed on Android
- Movie Rewards isTestMode method deleted on Android so that Logger and AdnwTestMode can be registered from AndroidManifest.xml

# 2017-05-23
GNAdSDK-2.1.10

- Movie Rewards Android's crash support redmine: 14260
- Movie Rewards Base class of Android GNSAdaptee.isTestMode (); isTestMode () implementation of Tapjoy Vungle adapter by deletion Deletion supported

# 2017-04-17
GNAdSDK-2.1.9

- Video Reward Add Vungle to Android

# 2017-04-03
GNAdSDK-2.1.8

- Video Reward Add Tapjoy to Android

