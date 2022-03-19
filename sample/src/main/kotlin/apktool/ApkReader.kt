package apktool

import apktool.xlcw.*
import dex.parse.DexData
import net.dongliu.apk.parser.ApkFile
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Path
import java.io.File
import java.nio.ByteBuffer
import java.util.*
import kotlin.collections.ArrayList

private val tempDir = "tempDir/"
private const val platformConfigPath = "assets/platform_config.properties"
private const val versionBytePath = "assets/AssetBundles/Android/version.bytes"
private val androidManifestPath = "AndroidManifest.xml"

//Loading 图位置
const val loadingImagePath = "assets/Frist_Loading.png"

//闪屏图位置
const val splashImagePath = "res/drawable/unity_static_splash.png"

class ApkReader(apkPath: String) {
  private lateinit var apkFile: ApkFile
  private val paraMap = mutableMapOf<String, String>()

  init {
    initApkInfo(apkPath)
  }

  private fun initApkInfo(apkPath: String) {
    val file = File(apkPath)
    if (!file.exists()) {
      return
    }
    apkFile = ApkFile(file)
    parseApk()
    checkPlatform(apkPath)
    checkChannel()
    paraMap.putAll(checkConfigUrl(paraMap))
    paraMap[weChatPayActivityKey] = checkWeChatActivity()

    val dexDatas = parseDexFiles()
    for (i in 0 until dexDatas.size) {
      val dex = dexDatas[i]
      val k = "classes$i"
      paraMap[k] = dex.readMethodRefsCount().toString()
      if (!dexMethodCountKeys.contains(k)) {
        dexMethodCountKeys.add(k)
      }

      if (!paraMap.containsKey(svnVersionCodeKey) || paraMap[svnVersionCodeKey] == "" ) {
        paraMap[svnVersionCodeKey] = dex.readSvnVersionCode()
      }
    }
  }

  private fun parseDexFiles(): ArrayList<DexData> {
    val result = ArrayList<DexData>().apply {
      add(DexData(ByteBuffer.wrap(apkFile.getFileData("classes.dex"))))
    }

    var secondaryDexData: ByteArray? = null
    for (i in 1..30) {
      secondaryDexData = apkFile.getFileData("classes$i.dex")
      if (secondaryDexData == null) {
        break
      }
      result.add(DexData(ByteBuffer.wrap(secondaryDexData)))
    }
    return result
  }


  fun getApkProperts(): Map<String, String> {

    return paraMap
  }

  //读取图标
  fun readIcon(): ByteArray {
    apkFile.allIcons.forEach { iconFace ->
      println("iconPath: ${iconFace.path}")
    }
    return apkFile.allIcons[0].data
  }

  fun readIconFileName(): String {
    return apkFile.allIcons[0].path.substringAfterLast(Path.DIRECTORY_SEPARATOR)
  }

  fun readAllImage(): Map<String, ByteArray> {
    return mutableMapOf<String, ByteArray>().apply {
      apkFile.allIcons.forEach {
        put(it.path, it.data)
      }
      put(splashImagePath, apkFile.getFileData(splashImagePath) ?: ByteArray(0))
      put(loadingImagePath, apkFile.getFileData(loadingImagePath) ?: ByteArray(0))
    }
  }

  private fun parseApk() {
    paraMap.putAll(readPlatformConfig())
    paraMap.putAll(readVersionBytes())
    paraMap.putAll(readApkMetaInfo())
    paraMap.putAll(checkConfigUrl(paraMap))

    operFixParam.forEach { entry ->
      operFixParam[entry.key] = paraMap[entry.key].let { it ?: "" }
    }

    techFixParam.forEach { entry ->
      techFixParam[entry.key] = paraMap[entry.key].let { it ?: "" }
    }

    paraMap[apkSignKey] = readCertificationMd5()
  }

  private fun checkChannel() {
    if (paraMap.containsKey("ChannelNum")) {
      paraMap["channel"] = paraMap["ChannelNum"] ?: ""
    }
  }

  private fun checkPlatform(apkPath: String) {
    println("OsType: ${paraMap["OsType"]}")
    if (paraMap.containsKey("OsType")) {
      paraMap["platform"] = paraMap["OsType"] ?: platformFromFileSuffix(apkPath)
    }
  }

  private fun platformFromFileSuffix(apkPath: String): String {
    for (suffix in androidSuffix) {
      if (apkPath.endsWith(suffix)) {
        return "Android"
      }
    }

    for (suffix in iOSSuffix) {
      if (apkPath.endsWith(suffix)) {
        return "iOS"
      }
    }
    return ""
  }

  //读取 assets/platform_config.properties 文件
  private fun readPlatformConfig(): Map<String, String> {
    println("---------------- start read $platformConfigPath content ------------------")
    val stringContent = String(apkFile.getFileData(platformConfigPath) ?: return emptyMap())


    return mutableMapOf<String, String>().apply {
      stringContent.split("\n").filter {
        val l = it.trim()
        l != "" && !l.startsWith("#")
      }.forEach { line ->
        println(line)
        val lineWithoutSpace = line.trim()
        println("lineWithoutSpace: $lineWithoutSpace")
        val equalIndex = lineWithoutSpace.indexOf("=")
        val key = lineWithoutSpace.substring(0 until equalIndex)
        val value =
            if (equalIndex == lineWithoutSpace.length - 1) "" else lineWithoutSpace.substring(
                equalIndex + 1 until lineWithoutSpace.length
            )
        put(key, value)
      }
      println("----------------------- end read $platformConfigPath --------------------------")
    }
  }

  //读取 assets/AssetBundles/Android/version.bytes 文件中的 com.example.utilclass.apk.parse.xlcw.VersionNum 属性值
  private fun readVersionBytes(): Map<String, String> {
    println("---------------- start read $versionBytePath content ------------------")
    val stringContent = String(apkFile.getFileData(versionBytePath) ?: return emptyMap()).apply {
      substring(1 until length)
    }

    println("$versionBytePath content: $stringContent")

    return mutableMapOf<String, String>().apply {
      stringContent
          .substring(1, stringContent.length - 1)
          .replace("\"", "")
          .split(",")
          .forEach {
            val propertyString = it.split(":")

            if (propertyString.size == 2)
              put(propertyString[0], propertyString[1])
          }

      println("---------------- end read $versionBytePath ------------------")
    }
  }

  //读取 apk 版本相关信息
  private fun readApkMetaInfo(): Map<String, String> {
    return mapOf(
        appName to apkFile.apkMeta.name,
        packageName to apkFile.apkMeta.packageName,
        versionName to apkFile.apkMeta.versionName,
        versionCode to apkFile.apkMeta.versionCode.toString(),
        minSdkVersion to apkFile.apkMeta.minSdkVersion,
        targetSdkVersion to apkFile.apkMeta.targetSdkVersion
    )
  }

  //获取 B2 工程 Svn 版本号
  private fun readPlatformCodeSvnVersion() {
    //return com.xlcwnet.sdk.dock.compat.BuildConfig.B2_SVN_VERSION_CODE
  }

  //检查 dex 方法数
  private fun readMethodRefs(): Int {
    apkFile.dexClasses
    return 0
  }

  //检查微信支付 Activity 是否存在
  private fun checkWeChatActivity(): String {
    val sb = StringBuilder("")
    apkFile.dexClasses.forEach {
//      println("classType: ${it.classType}, packageName: ${it.packageName}")
      if (it.classType.substring(1, it.classType.length - 1).endsWith("/WXPayEntryActivity")) {
        val weChatActivityPackage = it.classType.substring(1, it.classType.length - 1).replace("/", ".")
        sb.append(weChatActivityPackage)
        if (weChatActivityPackage == "${paraMap[packageName]}.wxapi.WXPayEntryActivity") {
          sb.append("（微信支付已配置）")
        }
        sb.append("\n")
      }
    }
    return sb.toString()
  }

  //检查统一配置地址是否有效
  private fun checkConfigUrl(paraMap: Map<String, String>): Map<String, String> {
    val configUrl = paraMap[configUrl]
    val paramString: String?

    val params = TreeMap<String, String>()
    params["bin_ver"] = paraMap[versionName]!!
    params["bin_name"] = paraMap[packageName]!!
    params["uuid"] = "00000000-0000-0000-0000-000000000000"
    if (!paraMap.containsKey(ConfigUrlSalt) || paraMap[ConfigUrlSalt]?.isEmpty() == true) {
      params["channel"] = paraMap[channelId]!!
      paramString = signParamWithSalt(params, "")
    } else {
      params["channel_id"] = paraMap[channelId]!!
      params["timestamp"] = (System.currentTimeMillis() / 100).toString()
      paramString = signParamWithSalt(params, paraMap[ConfigUrlSalt]!!)
    }

    val client = OkHttpClient()
    val request = Request.Builder()
        .url("${configUrl}?${paramString}")
        .build()

    showAlert("configUrl: ${request.url}")
    showAlert("configUrl_resq: ${client.newCall(request).execute().body?.string()}")
    return mapOf(
        ConfigUrlKey to "${request.url}",
        ConfigUrlResqKey to "${client.newCall(request).execute().body?.string()}"
    )
  }

  private fun readCertificationMd5(): String {
    val sb: StringBuilder = StringBuilder()
    apkFile.apkSingers.forEach { signer ->
      signer.certificateMetas.forEach { certificateMeta ->
        println(
            "v1 signer - ${signer.path} ---> " +
                "\n\t signAlgorithm: ${certificateMeta.signAlgorithm}" +
                "\n\t signAlgorithmOID: ${certificateMeta.signAlgorithmOID}" +
                "\n\t startDate: ${certificateMeta.startDate}" +
                "\n\t endDate ${certificateMeta.endDate}" +
                "\n\t data ${certificateMeta.data}" +
                "\n\t certBase64Md5: ${certificateMeta.certBase64Md5}" +
                "\n\t certMd5: ${certificateMeta.certMd5}"
        )
        sb.append("${signer.path} : ${certificateMeta.certMd5}")
      }
    }
    return sb.toString()
    /*apkFile.apkV2Singers?.forEach { signer ->
      signer.certificateMetas.forEach { certificateMeta ->
        println(
            "apkV2Signer - certificateMeta: " +
                "\n\t signAlgorithm: ${certificateMeta.signAlgorithm}" +
                "\n\t signAlgorithmOID: ${certificateMeta.signAlgorithmOID}" +
                "\n\t startDate: ${certificateMeta.startDate}" +
                "\n\t endDate ${certificateMeta.endDate}" +
                "\n\t data ${certificateMeta.data}" +
                "\n\t certBase64Md5: ${certificateMeta.certBase64Md5}" +
                "\n\t certMd5: ${certificateMeta.certMd5}"
        )
      }
    }*/
  }
}