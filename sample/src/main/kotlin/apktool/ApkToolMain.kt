package apktool

import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent
import java.io.File

const val OPTION_TITLE = "运营固定参数"
const val TECH_FIX_TITLE = "技术固定参数"
const val ALL_ICON_TITLE = "所有图标"

const val SIDEBAR_PROPERTY = "参数"
const val SIDEBAR_PACK_APP_CONFIG = "出包配置参数"

//private lateinit var apkReader: ApkReader


class ApkToolMain {

  fun main() = application {
    val showSideBar by remember { mutableStateOf(true) }
    var selectOption by remember { mutableStateOf(SIDEBAR_PROPERTY) }

    //macOS: /Users/zhouyanxia/Downloads/z2022030101_xlcw_webpay_zt_30.1.41_202203071545_l1099.apk
    //windows: E:\xlcw_webpay_hsyw.apk
    val apkFilePath =
    val apkReaderState = rememberApkReaderState("E:\\xlcw_webpay_hsyw.apk")
    val options = mutableListOf(SIDEBAR_PROPERTY, SIDEBAR_PACK_APP_CONFIG)

    /*LaunchedEffect(apkPath) {
      //apkReader = ApkReader(apkPath)
      *//*apkIconData = apkReader.readIcon()
      apkMetaInfo = apkReader.readApkMetaInfo()*//*
    }*/


    Window(
      onCloseRequest = ::exitApplication,
      icon = apkToolsIcon(),
      title = "apktools"
    ) {

      this.window.contentPane.dropTarget = drogTarget

      Surface(Modifier.fillMaxSize()) {
        ApkToolTheme {
          Row {
            if (showSideBar) {
              sideBarView(apkReaderState.iconList.value[0], options, apkReaderState.apkMetaData.value, Modifier.width(250.dp), onClickOption = {
                selectOption = it
              })
              Spacer(modifier = Modifier.width(1.dp).fillMaxHeight())
            }
            optionView(selectOption)
          }

        }
      }
    }
  }

  @Composable
  fun apkToolsIcon(): Painter {
    return painterResource("floatball.png")
  }

  val drogTarget = object : DropTarget() {
    override fun drop(dtde: DropTargetDropEvent) {
      dtde.acceptDrop(DnDConstants.ACTION_REFERENCE)
      val droppedFiles = dtde.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<*>
      droppedFiles.forEach { file ->
        println((file as File).absolutePath)
      }
    }
  }
}
