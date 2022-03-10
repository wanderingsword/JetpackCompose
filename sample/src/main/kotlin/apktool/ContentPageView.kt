package apktool

import ImageContainerWithColumn
import TableWithColumn
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


@Composable
fun optionView(title: String, iconList: Map<String, ByteArray>, modifier: Modifier = Modifier, iconFileName: String = "") {
  when (title) {
    SIDEBAR_PROPERTY -> {
      OptionContentViewContainer(
        title,
        modifier.fillMaxHeight().padding(10.dp)
      ) {
        Spacer(Modifier.height(10.dp))
        TableWithColumn(4, OPTION_TITLE, operFixParam)
        Spacer(Modifier.height(10.dp))
        TableWithColumn(4, TECH_FIX_TITLE, techFixParam)
        Spacer(Modifier.height(10.dp))
        ImageContainerWithColumn(2, "$ALL_ICON_TITLE（文件名：${iconFileName}）", iconList)
      }
    }
    SIDEBAR_PACK_APP_CONFIG -> {
      OptionContentViewContainer(
        title,
        modifier.fillMaxHeight().padding(10.dp)
      ) {
        Spacer(Modifier.height(10.dp))
        TableWithColumn(4, SIDEBAR_PACK_APP_CONFIG, configParam)
      }
    }
  }
}

@Composable
private fun OptionContentViewContainer(title: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  Column (modifier.fillMaxHeight().padding(10.dp).verticalScroll(rememberScrollState())) {
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
