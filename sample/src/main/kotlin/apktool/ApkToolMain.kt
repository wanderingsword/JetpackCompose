package apktool

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
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
const val SIDEBAR_ALL_PROPERTY = "所有属性"
//const val

//private lateinit var apkReader: ApkReader


class ApkToolMain {

  fun main() = application {
    val showSideBar by remember { mutableStateOf(true) }
    var selectOption by remember { mutableStateOf(SIDEBAR_PROPERTY) }

    //macOS: /Users/zhouyanxia/Downloads/z2022030101_xlcw_webpay_zt_30.1.41_202203071545_l1099.apk
    ///Users/zhouyanxia/Downloads/B2_ope_China2_Release_IL2CPP_xinghui_3.1.43_202203160625_xinghui_hsjg_3.1.43_202203161228_b1024.apk
    ///Users/zhouyanxia/Downloads/B2_ope_China2_Release_IL2CPP_xlcw_3.1.42_202203160204_xlcw_clksdxhsj1098_3.1.43_202203161219_l1098.apk
    ///Users/zhouyanxia/Downloads/B2_ope_China2_Release_IL2CPP_xlcw_3.1.42_202203160204_xlcw_webpay_sgyyl_3.1.43_202203191029_l1096.apk
    //windows: E:\xlcw_webpay_hsyw.apk
    var apkFilePath by remember { mutableStateOf("/Users/zhouyanxia/Downloads/B2_ope_China2_Release_IL2CPP_xinghui_3.1.43_202203160625_xinghui_hsjg_3.1.43_202203161228_b1024.apk") }
    val apkReaderState = ApkReaderState()
    val options = mutableListOf(SIDEBAR_PROPERTY, SIDEBAR_PACK_APP_CONFIG, SIDEBAR_ALL_PROPERTY)

    Window(
        state = rememberWindowState(size = DpSize(1200.dp, 800.dp)),
        onCloseRequest = ::exitApplication,
        icon = apkToolsIcon(),
        title = "apktools"
    ) {

      LaunchedEffect(apkFilePath) {
        apkReaderState.apkFilePath = apkFilePath
      }

      this.window.contentPane.dropTarget = drogTarget

      Surface(Modifier.fillMaxSize()) {
        ApkToolTheme {
          Row(modifier = Modifier.wrapContentWidth()) {
            if (showSideBar) {
              sideBarView(
                  apkIconData = if (apkReaderState.allImageData.values.isEmpty()) null else apkReaderState.allImageData.values.first(),
                  options = options,
                  apkMetaInfo = apkReaderState.allProperty,
                  modifier = Modifier.width(250.dp), onClickOption = {
                    selectOption = it
                  }
              )
              Spacer(modifier = Modifier.width(1.dp).fillMaxHeight())
            }
            contentPage(selectOption, apkReaderState.allProperty, apkReaderState.allImageData, iconFileName = apkReaderState.iconFileName)
          }
        }
      }
    }
  }

  @Composable
  fun apkToolsIcon(): Painter {
    return painterResource("floatball.png")
  }

  private val drogTarget = object : DropTarget() {
    override fun drop(dtde: DropTargetDropEvent) {
      dtde.acceptDrop(DnDConstants.ACTION_REFERENCE)
      val droppedFiles = dtde.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<*>
      droppedFiles.forEach { file ->
        println((file as File).absolutePath)
      }
    }
  }
}
