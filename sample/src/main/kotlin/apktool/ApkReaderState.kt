package apktool

import androidx.compose.runtime.*

class ApkReaderState(apkReader: ApkReader) {
  var iconList = mutableStateOf(apkReader.readAllIcon())
  var apkMetaData = mutableStateOf(apkReader.readApkMetaInfo())
}

fun rememberApkReaderState(apkFilePath: String) =  remember(mutableStateOf(apkFilePath)) {
   ApkReaderState(ApkReader(apkFilePath))
}