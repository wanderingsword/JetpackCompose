package apktool

import Table1
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
    SIDEBAR_PROPERTY -> {
      tableContentView(
          title,
          mapOf(OPTION_TABLE_TITLE to operFixParam, TECH_FIX_TABLE_TITLE to techFixParam),
          modifier.fillMaxHeight().padding(10.dp)
      )
    }
    SIDEBAR_PACK_APP_CONFIG -> {
      tableContentView(
          title,
          mapOf(SIDEBAR_PACK_APP_CONFIG to configParam),
          modifier.fillMaxHeight().padding(10.dp)
      )
    }
  }

}

@Composable
private fun tableContentView(title: String, tableData: Map<String, Map<String, String>>, modifier: Modifier = Modifier) {
  Column(modifier.fillMaxHeight().padding(10.dp)) {
    TopAppBar(
        title = {
          Text(title, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        },
        backgroundColor = ContentBackground,
        modifier = Modifier.height(50.dp)
    )

    tableData.entries.forEach {
      Spacer(Modifier.height(10.dp))

      Table1(it.key, 4, it.value)
    }
  }
}