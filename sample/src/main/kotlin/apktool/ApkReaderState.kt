package apktool

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

internal class ApkReaderState {

  var allImageData by mutableStateOf(emptyMap<String, ByteArray>())
  var iconFileName by mutableStateOf("")
  var allProperty by mutableStateOf(emptyMap<String, String>())

  var splashImage by mutableStateOf<ByteArray?>(null)
  var loadingImageby by mutableStateOf<ByteArray?>(null)


  var apkFilePath = ""
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

  constructor()

  constructor(apkFilePath: String) {
    this.apkFilePath = apkFilePath
  }
}





