Let's keep the past version of jar so that it can be easily investigated with inquiries support etc in the future.
# GNSRewardAdapter-2.3.1
GNAdSDK-3.2.1
- Vungle: 6.2.5 --> 6.3.17

# GNSRewardAdapter-2.3.0
GNAdSDK-3.2.0
- targetSdkVersionを26から28へ

# GNSRewardAdapter-2.2.0
GNAdSDK-3.1.3
 - CAReward: 2.0.0 -> 2.4.0

# GNSRewardAdapter-2.1.0
GNAdSDK-3.1.0

- MavenとProguardを実装<br>(Implement Maven and Proguard)
- AdColonyが2回目のLoadで失敗する不具合を修正<br>(Fixed a bug that AdColony failed at the second Load)
- 最新のすべての3番目のAndroid用SDKをアップグレードする<br>(Upgrade all latest 3rd SDKs for Android)
 - Maio: 1.0.8 -> 1.1.1
 - Tapjoy: 11.11.1 -> 11.12.2
 - Vungle: 4.0.3 --> 6.2.5
 - CAReward: Keep


# GNSRewardAdapter-2.0.0
GNAdSDK-3.0.0

- 最新のすべての3番目のAndroid用SDKをアップグレードする<br>(Upgrade all latest 3rd SDKs for Android)

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
- Amoadの動画リワード実装

# GNSRewardAdapter-1.3.9
- Implement UnityAdsの動画リワード実装

# GNSRewardAdapter-1.3.8
- Nendの動画リワード実装

# GNSRewardAdapter-1.3.7
- Tapjoy 初期化改善

# GNSRewardAdapter-1.3.6
- CARewardの1.5.1バージョンアップ伴い、GNSAdapterCARewardRewardVideoAd修正

# GNSRewardAdapter-1.3.5
- GNAdSDK-2.2.2の対応版
- 動画リワードAndroidのReqImp処理をwebviewからhttp処理に
- 動画リワードAndroidの前回ロード済みのADNWは次回ロード時初期化しないように修正
- 動画リワードAndroidのタイムアウト追加によるwifi/carrierメディエーションロジック全面改修
- 動画リワードAndroidのADNW在庫判定コールバック時にバックグラウンドであればロードリクエスト処理を一旦停止し、復帰後、再リトライするように
- 動画リワードAndroidのエラーメッセージの英語化
- 動画リワードAndroidの2重ロードリクエストを防止するように修正
- 動画リワードAndroidのアプリへの成否コールバック通知を排他制御するように修正
- 動画リワードAndroidのAdColonyのリワード付与タイミングを再生終了から閉じるタイミングに変更
<br>https://geniee.atlassian.net/wiki/pages/viewpage.action?pageId=105469337

# 2017-06-07
GNSRewardAdapter - 1.3.4

- 動画リワードAndroidのMaio,TapjoyのReqImpが少なすぎる件のbugfix
redmine:14455

# 2017-05-30
GNSRewardAdapter-1.3.3

- 動画リワードAndroidのdidFailToLoadWithErrorが呼ばれない件の対応
redmine:14370

# 2017-05-24
GNSRewardAdapter-1.3.2

- 動画リワードAndroidの一部ADNWのsdk.keyが間違っていると全体のADNWの再生処理が停止する不具合の修正

# 2017-05-24
GNSRewardAdapter-1.3.1 (not yet released)

- 動画リワードAndroidのtapjoyログレベル修正
- 動画リワードAndroidのtapjoy,careward,vungleのdidFailToLoadWithErrorが呼ばれないように対応

# 2017-04-17
GNSRewardAdapter-1.3.0

- 動画リワードAndroidにVungle追加

# 2017-04-03
GNSRewardAdapter-1.2.0

- 動画リワードAndroidにTapjoy追加


