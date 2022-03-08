package apktool

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apktool.xlcw.appName
import apktool.xlcw.packageName
import apktool.xlcw.versionCode
import apktool.xlcw.versionName


//侧边栏
@Composable
fun SideBarView(options: List<String>, apkMetaInfo: Map<String, String>, modifier: Modifier = Modifier) {
  Column(modifier = modifier.fillMaxHeight().background(color = ContentBackground)) {
    //显示 Apk 基本信息
    SideBarHeader(
      appName = apkMetaInfo[appName].let { it ?: "" },
      packageName = apkMetaInfo[packageName].let { it ?: "" },
      versionInfo = "${apkMetaInfo[versionName]}  ${apkMetaInfo[versionCode]}",
      BitmapPainter(org.jetbrains.skia.Image.makeFromEncoded(apkReader.readIcons()).toComposeImageBitmap())
    )

    //侧边栏操作选项列表
    SideBarOptionList(options)
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
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
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
private fun SideBarOptionList(options: List<String>) {
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