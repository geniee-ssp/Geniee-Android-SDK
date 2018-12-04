#!/bin/sh

# ==============================
# 環境変数の読み込み
# ==============================
SH_DIR=$(cd $(dirname $0);pwd)
ENV_FILE="${SH_DIR}/../.env"
FUNCTIONS_FILE="${SH_DIR}/file-copy-functions"
VARIABLE_FILE="${SH_DIR}/file-copy-variable"

if [ ! -e ${ENV_FILE} ]; then
    echo "${ENV_FILE} file not found"
    exit 1
fi
if [ ! -e ${FUNCTIONS_FILE} ]; then
    echo "${FUNCTIONS_FILE} file not found"
    exit 1
fi
if [ ! -e ${VARIABLE_FILE} ]; then
    echo "${VARIABLE_FILE} file not found"
    exit 1
fi


source ${ENV_FILE}
source ${FUNCTIONS_FILE}

#
## ==============================
## 各SDKバージョンの読み込み
## ==============================
fncGetSDKVersion

#
## ==============================
## 変数の読み込み
## ==============================
source ${VARIABLE_FILE}

#
## ==============================
## 変数設定
## ==============================
# README
GITHUB_README="${GITHUB_DIR}/${README}"

# GNAdSDK
GITHUB_GNAdSDK_PATH="${GITHUB_DIR}/${GNAdSDK_PATH}"

# Adapter
GITHUB_GNAdDFPBannerMediationAdapter_PATH="${GITHUB_DIR}/${GNAdDFPBannerMediationAdapter_PATH}"
GITHUB_GNAdDFPRewardMediationAdapter_PATH="${GITHUB_DIR}/${GNAdDFPRewardMediationAdapter_PATH}"
GITHUB_GNSRewardAdapter_AdColony_PATH="${GITHUB_DIR}/${GNSRewardAdapter_AdColony_PATH}"
GITHUB_GNSRewardAdapter_Amoad_PATH="${GITHUB_DIR}/${GNSRewardAdapter_Amoad_PATH}"
GITHUB_GNSRewardAdapter_AppLovin_PATH="${GITHUB_DIR}/${GNSRewardAdapter_AppLovin_PATH}"
GITHUB_GNSRewardAdapter_CAReward_PATH="${GITHUB_DIR}/${GNSRewardAdapter_CAReward_PATH}"
GITHUB_GNSRewardAdapter_Maio_PATH="${GITHUB_DIR}/${GNSRewardAdapter_Maio_PATH}"
GITHUB_GNSRewardAdapter_Nend_PATH="${GITHUB_DIR}/${GNSRewardAdapter_Nend_PATH}"
GITHUB_GNSRewardAdapter_Tapjoy_PATH="${GITHUB_DIR}/${GNSRewardAdapter_Tapjoy_PATH}"
GITHUB_GNSRewardAdapter_UnityAds_PATH="${GITHUB_DIR}/${GNSRewardAdapter_UnityAds_PATH}"
GITHUB_GNSRewardAdapter_Vungle_PATH="${GITHUB_DIR}/${GNSRewardAdapter_Vungle_PATH}"

# repository
GITHUB_REPOSITORY="${GITHUB_DIR}/${REPOSITORY}"

# sample
GITHUB_UseLocalLibrary_GNAdDFPBannerMediationSample="${GITHUB_DIR}/${UseLocalLibrary_GNAdDFPBannerMediationSample}"
GITHUB_UseLocalLibrary_GNAdDFPRewardMediationSample="${GITHUB_DIR}/${UseLocalLibrary_GNAdDFPRewardMediationSample}"
GITHUB_UseLocalLibrary_GNAdRewardVideoSample="${GITHUB_DIR}/${UseLocalLibrary_GNAdRewardVideoSample}"
GITHUB_UseLocalLibrary_GNAdSampleBanner="${GITHUB_DIR}/${UseLocalLibrary_GNAdSampleBanner}"
GITHUB_UseLocalLibrary_GNAdSampleInterstitial="${GITHUB_DIR}/${UseLocalLibrary_GNAdSampleInterstitial}"
GITHUB_UseLocalLibrary_GNAdSampleMultipleBanner="${GITHUB_DIR}/${UseLocalLibrary_GNAdSampleMultipleBanner}"
GITHUB_UseLocalLibrary_GNAdSampleNativeAd="${GITHUB_DIR}/${UseLocalLibrary_GNAdSampleNativeAd}"
GITHUB_UseLocalLibrary_GNAdSampleVideo="${GITHUB_DIR}/${UseLocalLibrary_GNAdSampleVideo}"

GITHUB_UseMaven_SAMPLE_DIR="${GITHUB_DIR}/${UseMaven_SAMPLE_DIR}"
GITHUB_UseMaven_GNAdDFPRewardMediationSample_Lib="${GITHUB_DIR}/${UseMaven_GNAdDFPRewardMediationSample}"
GITHUB_UseMaven_GNAdSampleRewardvideo_Lib="${GITHUB_DIR}/${UseMaven_GNAdSampleRewardvideo}"

# ==============================
# ファイルコピー
# ==============================

if [ ! -d ${GITHUB_DIR} ]; then
    echo "${GITHUB_DIR} file not found"
    exit 1
fi

if [ ${GITHUB_DIR} = "/" ]; then
    echo "`GITHUB_DIR` Can not be specified"
    exit 1
fi

echo
echo "=================================================="
echo "File Copy"
echo "=================================================="
fncCopyFiles ${README} ${GITHUB_README}

fncCopyFiles ${GNAdSDK_PATH} ${GITHUB_GNAdSDK_PATH}
fncCopyFiles ${GNAdDFPBannerMediationAdapter_PATH} ${GITHUB_GNAdDFPBannerMediationAdapter_PATH}
fncCopyFiles ${GNAdDFPRewardMediationAdapter_PATH} ${GITHUB_GNAdDFPRewardMediationAdapter_PATH}
fncCopyFiles ${GNSRewardAdapter_AdColony_PATH} ${GITHUB_GNSRewardAdapter_AdColony_PATH}
fncCopyFiles ${GNSRewardAdapter_Amoad_PATH} ${GITHUB_GNSRewardAdapter_Amoad_PATH}
fncCopyFiles ${GNSRewardAdapter_AppLovin_PATH} ${GITHUB_GNSRewardAdapter_AppLovin_PATH}
fncCopyFiles ${GNSRewardAdapter_CAReward_PATH} ${GITHUB_GNSRewardAdapter_CAReward_PATH}
fncCopyFiles ${GNSRewardAdapter_Maio_PATH} ${GITHUB_GNSRewardAdapter_Maio_PATH}
fncCopyFiles ${GNSRewardAdapter_Nend_PATH} ${GITHUB_GNSRewardAdapter_Nend_PATH}
fncCopyFiles ${GNSRewardAdapter_Tapjoy_PATH} ${GITHUB_GNSRewardAdapter_Tapjoy_PATH}
fncCopyFiles ${GNSRewardAdapter_UnityAds_PATH} ${GITHUB_GNSRewardAdapter_UnityAds_PATH}
fncCopyFiles ${GNSRewardAdapter_Vungle_PATH} ${GITHUB_GNSRewardAdapter_Vungle_PATH}

fncCopyFiles ${REPOSITORY} ${GITHUB_REPOSITORY}

fncCopyFiles ${UseLocalLibrary_GNAdDFPBannerMediationSample} ${GITHUB_UseLocalLibrary_GNAdDFPBannerMediationSample}
fncCopyFiles ${UseLocalLibrary_GNAdSampleBanner} ${GITHUB_UseLocalLibrary_GNAdSampleBanner}
fncCopyFiles ${UseLocalLibrary_GNAdSampleInterstitial} ${GITHUB_UseLocalLibrary_GNAdSampleInterstitial}
fncCopyFiles ${UseLocalLibrary_GNAdSampleMultipleBanner} ${GITHUB_UseLocalLibrary_GNAdSampleMultipleBanner}
fncCopyFiles ${UseLocalLibrary_GNAdSampleNativeAd} ${GITHUB_UseLocalLibrary_GNAdSampleNativeAd}
fncCopyFiles ${UseLocalLibrary_GNAdSampleVideo} ${GITHUB_UseLocalLibrary_GNAdSampleVideo}

fncCopyFiles ${UseMaven_SAMPLE_DIR} ${GITHUB_UseMaven_SAMPLE_DIR}

# ==============================
# Localファイル削除
# ==============================

echo
echo "=================================================="
echo "Remove File"
echo "=================================================="
echo "Remove ${GITHUB_UseMaven_GNAdDFPRewardMediationSample_Lib}"
echo "Remove ${GITHUB_UseMaven_GNAdSampleRewardvideo_Lib}"
[ -d ${GITHUB_UseMaven_GNAdDFPRewardMediationSample_Lib} ] && rm -rf ${GITHUB_UseMaven_GNAdDFPRewardMediationSample_Lib}
[ -d ${GITHUB_UseMaven_GNAdSampleRewardvideo_Lib} ] && rm -rf ${GITHUB_UseMaven_GNAdSampleRewardvideo_Lib}
mkdir -p ${GITHUB_UseMaven_GNAdDFPRewardMediationSample_Lib}
mkdir -p ${GITHUB_UseMaven_GNAdSampleRewardvideo_Lib}

echo
echo "=================================================="
echo "create-github-projects.sh is complete"
echo "=================================================="
echo "* podspecはコピーしていません。リリースする前にpodspecが正しいことを確認してください。"
echo "* リリースする前に${GITHUB_PODS_SAMPLE_DIR} のpodfileが正しいことを確認してください。"
echo
echo "* podspec is not copied. Please make sure podspec is correct before releasing."
echo "* Please check that the podfile of ${GITHUB_PODS_SAMPLE_DIR} is correct before releasing."
echo
echo
