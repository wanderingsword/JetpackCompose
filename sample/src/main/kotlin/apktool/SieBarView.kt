package apktool

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
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
fun sideBarView(
  apkIconData: ByteArray,
  options: List<String>,
  apkMetaInfo: Map<String, String>,
  modifier: Modifier = Modifier,
  onClickOption: ((String) -> Unit) = {}
) {

  Column(modifier = modifier.fillMaxHeight().background(color = ContentBackground)) {
    //显示 Apk 基本信息
    sideBarHeader(
      appName = apkMetaInfo[appName].let { it ?: "" },
      packageName = apkMetaInfo[packageName].let { it ?: "" },
      versionName = apkMetaInfo[versionName].let { it ?: "" },
      versionCode = apkMetaInfo[versionCode].let { it ?: "" },
      appIcon = BitmapPainter(org.jetbrains.skia.Image.makeFromEncoded(apkIconData).toComposeImageBitmap())
    )

    //侧边栏操作选项列表
    sideBarOptionList(options, onClickOption)
  }
}

@Composable
private fun sideBarHeader(
  appName: String,
  packageName: String,
  versionName: String,
  versionCode: String,
  appIcon: BitmapPainter
) {
  Card(
    modifier = Modifier.padding(5.dp),
  ) {
    Row(
      modifier = Modifier.padding(8.dp)
    ) {
      Column {
        Image(
          painter = appIcon,
          contentDescription = "apk icon",
          modifier = Modifier.width(APK_ICON_WIDTH),
          contentScale = ContentScale.Crop,
        )

        Spacer(Modifier.height(5.dp))

        Text(
          modifier = Modifier.width(APK_ICON_WIDTH),
          text = appName,
          maxLines = 1,
          overflow = TextOverflow.Clip,
          fontSize = 12.sp,
          textAlign = TextAlign.Center
        )
      }

      Spacer(Modifier.width(10.dp))

      Column(
        Modifier.fillMaxWidth()
      ) {
        Text(
          text = packageName,
          maxLines = 1,
          overflow = TextOverflow.Clip,
          fontSize = 12.sp,
        )

        Spacer(Modifier.height(5.dp))

        Text(
          text = versionName,
          maxLines = 1,
          overflow = TextOverflow.Clip,
          fontSize = 12.sp,
        )

        Spacer(Modifier.height(5.dp))

        Text(
          text = versionCode,
          maxLines = 1,
          overflow = TextOverflow.Clip,
          fontSize = 12.sp,
        )
      }
    }

  }
}

@Composable
private fun sideBarOptionList(options: List<String>, onClickOption: (String) -> Unit) {
  if (options.isNotEmpty()) {
    Column {
      options.forEach {
        optionItemView(it, onClickOption)
      }
    }
  }
}

@Composable
fun optionItemView(title: String, onClickOption: (String) -> Unit) {
  val interactionSource = remember { MutableInteractionSource() }
  val isHovered by interactionSource.collectIsHoveredAsState()

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .hoverable(enabled = true, interactionSource = interactionSource)
        //取消文本的鼠标进入特效和按下时的特效
      .clickable(interactionSource = interactionSource, indication = null) {
        onClickOption(title)
      }
      .padding(2.dp)
      .border(BorderStroke(1.dp, ContentBackground), RoundedCornerShape(4.dp))
      .background(color = if (isHovered) ItemHoverBackgroundColor else ContentBackground)
      .padding(5.dp)

  ) {
    Text(
      text = AnnotatedString(title),
      maxLines = 1,
      color = if (isHovered) ItemHoverTextColor else SideBarItemTitleColor,
      modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 8.dp, vertical = 2.dp),
      overflow = TextOverflow.Ellipsis
    )
  }
}