package apktool.xlcw

const val DEBUG = false

val androidSuffix = mutableListOf("apk", "aab")

val iOSSuffix = mutableListOf("ipa")


const val appName = "appName"
const val packageName = "packageName"
const val versionName = "versionName"
const val versionCode = "versionCode"
const val minSdkVersion = "minSdkVersion"
const val targetSdkVersion = "targetSdkVersion"
const val configUrl = "ConfigUrl"
const val channelId = "Xl_Channel"
const val ConfigUrlSalt = "ConfigUrlSalt"
const val apkSignKey = "keyStore"
const val svnVersionCodeClass = "Lcom/xlcwnet/sdk/dock/compat/BuildConfig;"
const val svnVersionCodeKey = "svnVersion"

//运营固定参数
val operationFixParamKeys = mutableListOf(
    "platform",
    "channel",
    minSdkVersion,
    targetSdkVersion,
    apkSignKey
)

val dexMethodCountKeys = mutableListOf("classes")

const val weChatPayActivityKey = "微信支付 Activity："
//技术固定参数
val techFixParamKeys = mutableListOf(
    configUrl,
    svnVersionCodeKey,
    "Xl_Game_Id",
    "XL_DATA_URL",
    "XL_DATA_AREA",
    "XL_DATA_ISIMEI",
    "XL_SDK_VERSION_CODE",
    "XL_SDK_SHOW_SPLASH",
    "XL_SDK_SIGN",
    "SDKType",
    "VersionNum",
    weChatPayActivityKey
)

/**
 * 出包配置参数
 * 1. 出包时间
 * 2. 运营负责人
 * 3. 完成时间
 * 4. 平台负责人
 * 5. 测试负责人
 * 6. 完成情况
 * 7. 备注说明
 * 8. 平台（默认为 Android）
 * 9. 子渠道名称
 * 10. 游戏名称
 * 11. 包名
 * 12. 渠道号（Xl_Channel_ID）
 * 13. 子渠道号（CCHId）
 * 14. apk 版本号（versionCode）
 * 15. 二进制版本号（versionName）
 */
val configParamKeys = mutableListOf(
    "需求时间",
    "运营负责人",
    "完成时间",
    "平台负责人",
    "测试负责人",
    "完成情况",
    "备注说明",
    "系统",
    "渠道名称",
    "appName-游戏名称",
    "packageName-包名",
    "$channelId-渠道号",
    "CCHId-子渠道号",
    "versionCode-apk 版本号",
    "versionName-二进制版本号"
)

/*
    AppId：第三方 SDK 提供的 key
    AppKey：第三方 SDK 提供的 Secret
    TouTiao_App_Id：头条 AppId
    TouTiao_App_Name：头条 appName
    ReYun_App_Id：热云 appid
    XL_BUGLY_APPID：bugly appid
 */
val sdkParam = mutableListOf(
    "AppId",
    "AppKey",
    "TouTiao_App_Id",
    "TouTiao_App_Name",
    "ReYun_App_Id",
    "XL_BUGLY_APPID"
)


const val ConfigUrlCheckTitle = "统一配置地址检查"
const val ConfigUrlKey = "configUrl"
const val ConfigUrlResqKey = "configUrl_resq"
val configUrlCheck = listOf(
    ConfigUrlKey,
    ConfigUrlResqKey
)
const val DexMethodCountTitle = "dex 方法数"
