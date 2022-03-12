package apktool

import imageContainerWithColumn
import tableWithColumn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apktool.xlcw.operationFixParamKeys


@Composable
fun optionView(title: String, datas: Map<String, String>, iconList: Map<String, ByteArray>, modifier: Modifier = Modifier, iconFileName: String = "") {
  when (title) {
    SIDEBAR_PROPERTY -> {
      optionContentViewContainer(
        title,
        modifier.fillMaxHeight().padding(10.dp)
      ) {
        Spacer(Modifier.height(15.dp))
        tableWithColumn(4, OPTION_TITLE, datas, operationFixParamKeys)
        Spacer(Modifier.height(15.dp))
        tableWithColumn(4, TECH_FIX_TITLE, datas, apktool.xlcw.techFixParamKeys)
        Spacer(Modifier.height(15.dp))
        imageContainerWithColumn(3, "$ALL_ICON_TITLE（文件名：${iconFileName}）", iconList.filter { entry ->
          entry.key.endsWith(iconFileName) })
        Spacer(Modifier.height(15.dp))
        imageContainerWithColumn(1, "闪屏图（文件名：${splashImagePath}）", iconList.filter { entry ->
          entry.key.endsWith(splashImagePath)
        }, 400.dp)
        Spacer(Modifier.height(15.dp))
        imageContainerWithColumn(1, "loading 图（文件名：${loadingImagePath}）", iconList.filter { entry ->
          entry.key.endsWith(loadingImagePath)
        }, 400.dp)
      }
    }
    SIDEBAR_PACK_APP_CONFIG -> {
      optionContentViewContainer(
        title,
        modifier.fillMaxHeight().padding(10.dp)
      ) {
        Spacer(Modifier.height(10.dp))
        tableWithColumn(4, SIDEBAR_PACK_APP_CONFIG, datas, apktool.xlcw.configParamKeys)
      }
    }
  }
}

@Composable
private fun optionContentViewContainer(title: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  Column (modifier.wrapContentWidth().padding(10.dp).verticalScroll(rememberScrollState())) {
    TopAppBar(
      title = {
        Text(title, fontSize = 25.sp, fontWeight = FontWeight.Bold)
      },
      backgroundColor = ContentBackground,
      modifier = Modifier.height(50.dp)
    )

    content()
  }
}
