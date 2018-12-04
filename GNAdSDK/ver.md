過去バージョンのjarも今後問い合わせサポートなどで調査しやすいように残しておきましょう。

# 2018-11-19
GNAdSDK-3.3.0

- バナーフォーマットの広告のローテーションが、inViewでない場合も実施されている不具合を修正
- バナーフォーマットでtouchTypeを設定しない場合のコンストラクタを追加
- バナーフォーマットでMraidの一部機能に対応(β版)  
	- getMaxSize
	- getCurrentPosition
	- isViewable


# 2018-09-27
GNAdSDK-3.2.1

- CARewardのバナー動画を表示した際に、画面が表示されない不具合を修正

# 2018-08-31
GNAdSDK-3.2.0

- 非推奨コード削除(webview周り,wifi-carrier判定部分,緯度経度周り,videoのaudio対応)
- google-play-services.jarをmavenへ
- org.apache.httpcomponentsをHttpURLConnectionへ
- GNADSDKのminSdkVersionを11から14へ
- GNADSDKのtargetSdkVersionを23から28へ

# 2018-07-31
GNAdSDK-3.1.3

- DefaultHttpClientをssl対応

# 2018-07-25
GNAdSDK-3.1.2

- commonクラスのhttpをhttpsへ変更する

# 2018-06-25
GNAdSDK-3.1.0

- MavenとProguardを実装<br>(Implement Maven and Proguard)
- AdColonyが2回目のLoadで失敗する不具合を修正<br>(Fixed a bug that AdColony failed at the second Load)
- 最新のすべての3番目のAndroid用SDKをアップグレードする<br>(Upgrade all latest 3rd SDKs for Android)
 - Maio: 1.0.8 -> 1.1.1
 - Tapjoy: 11.11.1 -> 11.12.2
 - Vungle: 4.0.3 --> 6.2.5
 - CAReward: Keep

# 2018-05-11
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

# 2018-04-26
GNAdSDK-2.2.11

- ログの英語化

# 2018-03-26
GNAdSDK-2.2.10
動画リワードメディエーション　→ Amoadの実装

# 2017-11-30
GNAdSDK-2.2.8
動画リワードメディエーション　→ Nendの実装

# 2017-09-12
GNAdSDK-2.2.7
multiLoadの改善
- 複数zoneを送った場合広告を取得するjsonが空のケースで正常にコールバックがされないのを改善


# 2017-08-31
GNAdSDK-2.2.6
バナー空き枠検知の改善
- アプリ在庫通知のタイミングは、空き枠検知後に変更
- 検知から表示まで２秒待ち時間を削除
- １回目広告取得には、複数広告取得してから空き枠検知処理する

# GNAdSDK-2.2.4

- GNSZoneInfoSourceの初期化改善

# GNAdSDK-2.2.3

- 動画リワードAndroidのReqImp処理をwebviewからhttp処理に
- 動画リワードAndroidの前回ロード済みのADNWは次回ロード時初期化しないように修正
- 動画リワードAndroidのタイムアウト追加によるwifi/carrierメディエーションロジック全面改修
- 動画リワードAndroidのADNW在庫判定コールバック時にバックグラウンドであればロードリクエスト処理を一旦停止し、復帰後、再リトライするように
- 動画リワードAndroidのエラーメッセージの英語化
- 動画リワードAndroidの2重ロードリクエストを防止するように修正
- 動画リワードAndroidのアプリへの成否コールバック通知を排他制御するように修正
- 動画リワードAndroidのAdColonyのリワード付与タイミングを再生終了から閉じるタイミングに変更
<br>https://geniee.atlassian.net/wiki/pages/viewpage.action?pageId=105469337

# 2017-05-31
GNAdSDK-2.2.1

- 動画リワードAndroidのdidFailToLoadWithErrorが呼ばれない件の対応
redmine:14370

# 2017-05-26
GNAdSDK-2.2.0(未リリース)

- Native_Ad_optout_support対応

# 2017-05-24
GNAdSDK-2.1.13

- 動画リワードAndroidの成果イベント時にloadRequestしても2回目の広告が正常に表示されるように修正

# 2017-05-24
GNAdSDK-2.1.12(未リリース)

- 動画リワードAndroidの一部ADNWのsdk.keyが間違っていると全体のADNWの再生処理が停止する不具合の修正。

# 2017-05-24
GNAdSDK-2.1.11(未リリース)

- 動画リワードAndroidでADNW_TEST_MODE追加 ＆ APIURLを変更できるように
- 動画リワードAndroidでisTestModeメソッド削除,LoggerとAdnwTestModeをAndroidManifest.xmlから登録できるように

# 2017-05-23
GNAdSDK-2.1.10

- 動画リワードAndroidのcrash対応 redmine:14260
- 動画リワードAndroidの基底クラスGNSAdaptee.isTestMode();削除によるTapjoy VungleアダプタのisTestMode()実装削除対応

# 2017-04-17
GNAdSDK-2.1.9

- 動画リワードAndroidにVungle追加

# 2017-04-03
GNAdSDK-2.1.8

- 動画リワードAndroidにTapjoy追加
