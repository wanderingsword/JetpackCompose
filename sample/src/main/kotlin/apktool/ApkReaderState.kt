package apktool

internal class ApkReaderState {
  var iconList = emptyMap<String, ByteArray>()
  var apkMetaData = emptyMap<String, String>()
  var iconFileName = ""

  private var apkFilePath = ""
    set(value) {
      field = value
      val apkReader = ApkReader(value)
      iconList = mutableMapOf<String, ByteArray>().apply {
        iconFileName = apkReader.readIconFileName()
        apkReader.readAllIcon().forEach {
          put(it.key.substring(it.key.indexOf("res/"), it.key.lastIndexOf("/")), it.value)
        }
      }
      apkMetaData = apkReader.readApkMetaInfo()
    }

  constructor(apkFilePath: String) {
    this.apkFilePath = apkFilePath
  }
}





