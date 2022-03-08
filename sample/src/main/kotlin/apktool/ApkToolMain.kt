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

private const val SIDEBAR_PROPERTY = "参数"
private const val OPTION_TABLE_TITLE = "运营固定参数"
private const val TECH_FIX_TABLE_TITLE = "技术固定参数"
private const val SIDEBAR_PACK_APP_CONFIG = "出包配置参数"
private lateinit var apkReader: ApkReader


class ApkToolMain {

  fun main() = application {
    val showSideBar by remember { mutableStateOf(true) }
    val selectOption by remember { mutableStateOf(SIDEBAR_PROPERTY) }
    val apkPath by remember { mutableStateOf("E:\\xlcw_webpay_hsyw.apk") }
    var apkMetaInfo by remember { mutableStateOf(emptyMap<String, String>()) }
    val options = mutableListOf(SIDEBAR_PROPERTY, SIDEBAR_PACK_APP_CONFIG)

    LaunchedEffect(apkPath) {
      apkReader = ApkReader(apkPath)
      apkReader.readIcons()
      apkMetaInfo = apkReader.readApkMetaInfo()
    }


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
              SideBarView(options, apkMetaInfo, Modifier.width(250.dp))
            }

            Spacer(modifier = Modifier.width(1.dp).fillMaxHeight())

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

  @Composable
  fun apkIconImage(bytes: ByteArray) {

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
