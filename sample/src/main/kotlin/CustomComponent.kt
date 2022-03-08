import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apktool.*

@Composable
fun <T> Table(
  columnCount: Int,
  cellWidth: (index: Int) -> Dp,
  data: List<T>,
  headerCellContent: @Composable (index: Int) -> Unit,
  rowCellContent: @Composable (index: Int, item: T) -> Unit
) {

  LazyRow(
    modifier = Modifier.padding(16.dp)
  ) {
    items((0 until columnCount).toList()) { columnIndex ->
      Column {
        (0..data.size).forEach { index ->
          Surface(
            border = BorderStroke(1.dp, Color.LightGray),
            contentColor = Color.Transparent,
            modifier = Modifier.width(cellWidth(columnIndex))
          ) {
            if (index == 0) {
              //填充标题栏
              headerCellContent(columnIndex)
            } else {
              //填充一行数据
              rowCellContent(columnIndex, data[index - 1])
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Table1(title: String, columnCount: Int, data: Map<String, String>) {
  Text(
    text = title,
    modifier = Modifier.fillMaxWidth().background(ContentBackground).padding(PaddingValues(5.dp)),
    color = TABLE_TITLE_COLOR,
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Bold
  )

  Spacer(Modifier.height(5.dp))

  LazyVerticalGrid(
    cells = GridCells.Fixed(columnCount),
  ) {
    data.forEach { entry ->
      tableCell(entry.key, true)
      tableCell(entry.value, false)
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
fun LazyGridScope.tableCell(text: String, isTitle: Boolean) {
  item {
    Text(
      text = text,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      color = if (isTitle) Color.Black else TextColor,
      modifier = Modifier.border(BorderStroke(1.dp, BorderColor))
        .background(if (isTitle) ContentBackground else Color.White).padding(5.dp),
    )
  }

}