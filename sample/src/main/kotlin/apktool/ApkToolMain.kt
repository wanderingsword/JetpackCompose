package apktool

import Table1
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent
import java.io.File

//import androidx.compose.runtime.setValue

private const val OPTION_PROPERTY = "运营固定参数"
private val options = mutableListOf(OPTION_PROPERTY)

class ApkToolMain {

  fun main() = application {
    val showSideBar by remember { mutableStateOf(true) }
    val selectOption by remember { mutableStateOf(OPTION_PROPERTY) }

    val apkPath by remember { mutableStateOf("/Users/zhouyanxia/Downloads/xlcw_webpay_hsyw.apk") }

    LaunchedEffect(apkPath) {
      val apkReader = ApkReader(apkPath)
      apkReader.readIcons()
    }

    Window(
        onCloseRequest = ::exitApplication,
        icon = apkToolsIcon(),
        title = "apktools"
    ) {
      Surface(Modifier.fillMaxSize()) {
        ApkToolTheme {
          Row {
            if (showSideBar) {
              Box(
                  modifier = Modifier.width(200.dp).fillMaxHeight().background(color = ContentBackground)
              ) {
                SideBarView()
              }
            }
            Spacer(modifier = Modifier.width(1.dp).fillMaxHeight())
            Column(
                Modifier.fillMaxHeight().padding(10.dp)
            ) {
              optionView(selectOption)
            }
          }

        }
      }
    }
  }


  //侧边栏
  @Composable
  fun SideBarView() {
    //Apk 基本信息
    ApkMetaInfo()

    //侧边栏操作选项列表
    optionList()
  }


  @Composable
  private fun ApkMetaInfo() {
    Card {

    }
  }

  @Composable
  private fun optionList() {
    if (options.isNotEmpty()) {
      Column {
        options.forEach {
          optionItemView(it)
        }
      }
    }
  }


  @Composable
  fun optionItemView(title: String) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .hoverable(interactionSource = interactionSource)
            .background(color = if (isHovered) ItemHoverBackgroundColor else ContentBackground),
    ) {
      Spacer(modifier = Modifier.width(8.dp))

      Text(
          text = AnnotatedString(title),
          maxLines = 1,
          color = if (isHovered) ItemHoverTextColor else SideBarItemTitleColor,
          modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 8.dp, vertical = 2.dp),
          overflow = TextOverflow.Ellipsis
      )
    }
  }

  @Composable
  fun optionView(title: String) {
    TopAppBar(
        title = {
          Text(title)
        },
        backgroundColor = ContentBackground,
        modifier = Modifier.height(50.dp)
    )

    Spacer(Modifier.height(5.dp))

    Table1(4, operFixParam)
  }

  @Composable
  fun apkToolsIcon(): Painter {
    return painterResource("floatball.png")
  }

  @Composable
  fun apkIconImage() {

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
