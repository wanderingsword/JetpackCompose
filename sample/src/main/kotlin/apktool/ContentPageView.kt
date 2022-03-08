package apktool

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun optionView(title: String, modifier: Modifier = Modifier) {
  when(title) {
    SIDEBAR_PROPERTY -> PropertyView(modifier, title)
    SIDEBAR_PACK_APP_CONFIG -> PropertyPackAppView()
  }

}

@Composable
private fun PropertyPackAppView() {
  TODO("Not yet implemented")
}

@Composable
private fun PropertyView(modifier: Modifier, title: String) {
  Column(modifier.fillMaxHeight().padding(10.dp)) {
    TopAppBar(
      title = {
        Text(title, fontSize = 25.sp, fontWeight = FontWeight.Bold)
      },
      backgroundColor = ContentBackground,
      modifier = Modifier.height(50.dp)
    )

    Spacer(Modifier.height(10.dp))

    Table1(OPTION_TABLE_TITLE, 4, operFixParam)

    Spacer(Modifier.height(10.dp))

    Table1(OPTION_TABLE_TITLE, 4, techFixParam)
  }
}