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
GOOGLE_README="${GOOGLE_DRIVE_DIR}/${README}"

# GNAdSDK
GOOGLE_GNAdSDK_PATH="${GOOGLE_DRIVE_DIR}/${GNAdSDK_PATH}"

# Adapter
GOOGLE_GNAdDFPBannerMediationAdapter_PATH="${GOOGLE_DRIVE_DIR}/${GNAdDFPBannerMediationAdapter_PATH}"
GOOGLE_GNAdDFPRewardMediationAdapter_PATH="${GOOGLE_DRIVE_DIR}/${GNAdDFPRewardMediationAdapter_PATH}"
GOOGLE_GNSRewardAdapter_AdColony_PATH="${GOOGLE_DRIVE_DIR}/${GNSRewardAdapter_AdColony_PATH}"
GOOGLE_GNSRewardAdapter_Amoad_PATH="${GOOGLE_DRIVE_DIR}/${GNSRewardAdapter_Amoad_PATH}"
GOOGLE_GNSRewardAdapter_AppLovin_PATH="${GOOGLE_DRIVE_DIR}/${GNSRewardAdapter_AppLovin_PATH}"
GOOGLE_GNSRewardAdapter_CAReward_PATH="${GOOGLE_DRIVE_DIR}/${GNSRewardAdapter_CAReward_PATH}"
GOOGLE_GNSRewardAdapter_Maio_PATH="${GOOGLE_DRIVE_DIR}/${GNSRewardAdapter_Maio_PATH}"
GOOGLE_GNSRewardAdapter_Nend_PATH="${GOOGLE_DRIVE_DIR}/${GNSRewardAdapter_Nend_PATH}"
GOOGLE_GNSRewardAdapter_Tapjoy_PATH="${GOOGLE_DRIVE_DIR}/${GNSRewardAdapter_Tapjoy_PATH}"
GOOGLE_GNSRewardAdapter_UnityAds_PATH="${GOOGLE_DRIVE_DIR}/${GNSRewardAdapter_UnityAds_PATH}"
GOOGLE_GNSRewardAdapter_Vungle_PATH="${GOOGLE_DRIVE_DIR}/${GNSRewardAdapter_Vungle_PATH}"

# REWARDAD_ADNW_SDKS
GOOGLE_REWARDAD_ADNW_SDKS="${GOOGLE_DRIVE_DIR}/${REWARDAD_ADNW_SDKS}"

# sample
GOOGLE_UseLocalLibrary_GNAdDFPRewardMediationSample="${GOOGLE_DRIVE_DIR}/${UseLocalLibrary_GNAdDFPRewardMediationSample}"
GOOGLE_UseLocalLibrary_GNAdSampleRewardvideo="${GOOGLE_DRIVE_DIR}/${UseLocalLibrary_GNAdSampleRewardvideo}"

# ==============================
# ファイルコピー
# ==============================
if [ ${GOOGLE_DRIVE_DIR} = "/" ]; then
    echo "`GOOGLE_DRIVE_DIR` Can not be specified"
    exit 1
fi

[ -d ${GOOGLE_DRIVE_DIR} ] && rm -rf ${GOOGLE_DRIVE_DIR}

mkdir -p ${GOOGLE_DRIVE_DIR}

echo
echo "=================================================="
echo "File Copy"
echo "=================================================="
fncCopyFiles ${README} ${GOOGLE_README}

fncCopyFiles ${GNAdSDK_PATH} ${GOOGLE_GNAdSDK_PATH}
fncCopyFiles ${GNAdDFPBannerMediationAdapter_PATH} ${GOOGLE_GNAdDFPBannerMediationAdapter_PATH}
fncCopyFiles ${GNAdDFPRewardMediationAdapter_PATH} ${GOOGLE_GNAdDFPRewardMediationAdapter_PATH}
fncCopyFiles ${GNSRewardAdapter_AdColony_PATH} ${GOOGLE_GNSRewardAdapter_AdColony_PATH}
fncCopyFiles ${GNSRewardAdapter_Amoad_PATH} ${GOOGLE_GNSRewardAdapter_Amoad_PATH}
fncCopyFiles ${GNSRewardAdapter_AppLovin_PATH} ${GOOGLE_GNSRewardAdapter_AppLovin_PATH}
fncCopyFiles ${GNSRewardAdapter_CAReward_PATH} ${GOOGLE_GNSRewardAdapter_CAReward_PATH}
fncCopyFiles ${GNSRewardAdapter_Maio_PATH} ${GOOGLE_GNSRewardAdapter_Maio_PATH}
fncCopyFiles ${GNSRewardAdapter_Nend_PATH} ${GOOGLE_GNSRewardAdapter_Nend_PATH}
fncCopyFiles ${GNSRewardAdapter_Tapjoy_PATH} ${GOOGLE_GNSRewardAdapter_Tapjoy_PATH}
fncCopyFiles ${GNSRewardAdapter_UnityAds_PATH} ${GOOGLE_GNSRewardAdapter_UnityAds_PATH}
fncCopyFiles ${GNSRewardAdapter_Vungle_PATH} ${GOOGLE_GNSRewardAdapter_Vungle_PATH}

fncCopyFiles ${REWARDAD_ADNW_SDKS} ${GOOGLE_REWARDAD_ADNW_SDKS}

fncCopyFiles ${UseLocalLibrary_GNAdDFPRewardMediationSample} ${GOOGLE_UseLocalLibrary_GNAdDFPRewardMediationSample}
fncCopyFiles ${UseLocalLibrary_GNAdSampleRewardvideo} ${GOOGLE_UseLocalLibrary_GNAdSampleRewardvideo}

# ==============================
# zip
# ==============================
echo
echo "=================================================="
echo "Zip Files"
echo "=================================================="
cd "${GOOGLE_DRIVE_DIR}/.."
zip -r9 "Google-Drive-Android.zip" "Google-Drive-Android"

echo
echo "=================================================="
echo "create-google-drive-projects.sh is complete"
echo "=================================================="
echo "* ${GOOGLE_DRIVE_DIR}.zip をGoogle Draiveにアップロードしてください。"
echo
echo "* Please upload ${GOOGLE_DRIVE_DIR} .zip to Google Draive."
echo
echo
