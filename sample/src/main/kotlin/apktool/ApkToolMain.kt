package apktool

import Table1
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import apktool.xlcw.appName
import apktool.xlcw.packageName
import apktool.xlcw.versionCode
import apktool.xlcw.versionName
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent
import java.io.File

private const val OPTION_PROPERTY = "运营固定参数"
private val options = mutableListOf(OPTION_PROPERTY)
private lateinit var apkReader: ApkReader


class ApkToolMain {

  fun main() = application {
    val showSideBar by remember { mutableStateOf(true) }
    val selectOption by remember { mutableStateOf(OPTION_PROPERTY) }
    val apkPath by remember { mutableStateOf("E:\\xlcw_webpay_hsyw.apk") }
    var apkMetaInfo by remember { mutableStateOf(emptyMap<String, String>()) }

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
              SideBarView(apkMetaInfo, Modifier.width(250.dp))
            }

            Spacer(modifier = Modifier.width(1.dp).fillMaxHeight())

            optionView(selectOption)

          }

        }
      }
    }
  }


  //侧边栏
  @Composable
  fun SideBarView(apkMetaInfo: Map<String, String>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxHeight().background(color = ContentBackground)) {
      //显示 Apk 基本信息
      SideBarHeader(
        appName = apkMetaInfo[appName].let { it ?: "" },
        packageName = apkMetaInfo[packageName].let { it ?: "" },
        versionInfo = "${apkMetaInfo[versionName]}  ${apkMetaInfo[versionCode]}",
        BitmapPainter(org.jetbrains.skia.Image.makeFromEncoded(apkReader.readIcons()).toComposeImageBitmap())
      )

      //侧边栏操作选项列表
      SideBarOptionList()
    }
  }


  @Composable
  private fun SideBarHeader(appName: String, packageName: String, versionInfo: String, appIcon: BitmapPainter) {
    Card(
      modifier = Modifier.padding(horizontal = 2.dp, vertical = 8.dp),
    ) {
      Row {
        Column {
          Image(
            painter = appIcon,
            contentDescription = "apk icon",
            modifier = Modifier.width(APK_ICON_WIDTH),
            contentScale = ContentScale.Crop,
          )

          Spacer(Modifier.height(3.dp))

          SideCardTextItem(appName)
        }

        Column(
          Modifier.fillMaxWidth()
        ) {
          SideCardTextItem(packageName)

          Spacer(Modifier.height(3.dp))

          SideCardTextItem(versionInfo)
        }
      }

    }
  }

  @Composable
  private fun SideCardTextItem(content: String) {
    Text(
      text = content,
      maxLines = 1,
      overflow = TextOverflow.Clip,
      fontSize = 12.sp,
      textAlign = TextAlign.Center
    )
  }

  @Composable
  private fun SideBarOptionList() {
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
  fun optionView(title: String, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxHeight().padding(10.dp)) {
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
