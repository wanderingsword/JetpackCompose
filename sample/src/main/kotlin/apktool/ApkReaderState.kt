package apktool

internal class ApkReaderState {

  var allImageData = emptyMap<String, ByteArray>()
  var iconFileName = ""
  var allProperty = emptyMap<String, String>()

  var splashImage: ByteArray? = null
  var loadingImage: ByteArray? = null


  private var apkFilePath = ""
    set(value) {
      field = value
      val apkReader = ApkReader(value)

      allImageData = mutableMapOf<String, ByteArray>().apply {
        iconFileName = apkReader.readIconFileName()
        apkReader.readAllImage().forEach {
          if(it.key.endsWith(iconFileName)) {
            put(it.key.substringAfter("res/"), it.value)
          } else {
            put(it.key, it.value)
          }
        }
      }

      allProperty = apkReader.getApkProperts()
    }

  constructor(apkFilePath: String) {
    this.apkFilePath = apkFilePath
  }
}





