import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import apktool.*
import java.io.File

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
fun Table1(title: String, columnCount: Int, data: Map<String, Any>) {
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
      item {
        TableItem(entry.key, true)
      }
      item {
        TableItem(entry.value, false)
      }
    }
  }
}

@Composable
private fun TableItem(content: Any, isTitle: Boolean, modifier: Modifier = Modifier) {
  when (content) {
    is String -> {
      Text(
        text = content,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = if (isTitle) Color.Black else TextColor,
        modifier = modifier.border(BorderStroke(1.dp, BorderColor))
          .background(if (isTitle) ContentBackground else Color.White).padding(5.dp),
      )
    }
    is ByteArray -> {
      Image(
        modifier = modifier,
        painter = BitmapPainter(org.jetbrains.skia.Image.makeFromEncoded(content).toComposeImageBitmap()),
        contentDescription = ""
      )
    }
  }
}

@Composable
fun TableWithColumn(columnCount: Int, title: String, data: Map<String, String>) {
  Column {
    Text(
      text = title,
      modifier = Modifier.fillMaxWidth().background(ContentBackground).padding(PaddingValues(5.dp)),
      color = TABLE_TITLE_COLOR,
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Bold
    )

    Spacer(Modifier.height(5.dp))


    tablePerEntryTwoCol(columnCount, data)
  }
}

@Composable
fun ImageContainerWithColumn(columnCount: Int, title: String, data: Map<String, ByteArray>) {
  Column {
    Text(
      text = title,
      modifier = Modifier.fillMaxWidth().background(ContentBackground).padding(PaddingValues(5.dp)),
      color = TABLE_TITLE_COLOR,
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Bold
    )

    Spacer(Modifier.height(5.dp))


    tablePerEntryOneCol(columnCount, data)
  }
}

@Composable
fun tablePerEntryTwoCol(columnCount: Int, data: Map<String, String>) {
  val entryEachRow = columnCount / 2
  val rowCount = (data.size * 2 / columnCount) + 1
  val dataList = data.toList()

  println("data size ${data.size}, rowCount: $rowCount, entryEachRow: $entryEachRow")
  for (i in 0 until rowCount) {
    Row(modifier = Modifier.wrapContentSize()) {
      for (j in 0 until entryEachRow) {
        val index = i * entryEachRow + j
        if (index < dataList.size) {
          TableItem(dataList[index].first, true, Modifier.weight(1f))
          TableItem(dataList[index].second, false, Modifier.weight(1f))
        } else {
          Spacer(Modifier.weight(2f, fill = true))
        }
      }
    }
  }
}

@Composable
fun tablePerEntryOneCol(columnCount: Int, data: Map<String, ByteArray>) {
  val rowCount = (data.size * 2 / columnCount) + 1
  val dataList = data.toList()
  for (i in 0 until rowCount) {
    Row(Modifier.fillMaxWidth()) {
      for (j in 0 until columnCount) {
        val index = i * columnCount + j
        if (index < data.size) {
          ImageItem(dataList[index].first, dataList[index].second, Modifier.weight(1f))
        } else {
          Spacer(Modifier.weight(1f, fill = true))
        }
      }
    }
  }
}

@Composable
fun ImageItem(description: String, imageData: ByteArray, modifier: Modifier = Modifier) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
  ) {
    Image(
      painter = BitmapPainter(org.jetbrains.skia.Image.makeFromEncoded(imageData).toComposeImageBitmap()),
      contentDescription = description,
      contentScale = ContentScale.None
    )
    Text(
      text = description
    )
  }
}


@Preview
@Composable
fun imgContainerPreview() {
  val datas = mapOf(
    "mipmap-hdpi-v4" to File("E:\\xlcw_webpay_hsyw\\res\\mipmap-hdpi-v4\\app_icon.png").readBytes(),
    "mipmap-mdpi-v4" to File("E:\\xlcw_webpay_hsyw\\res\\mipmap-mdpi-v4\\app_icon.png").readBytes(),
    "mipmap-xhdpi-v4" to File("E:\\xlcw_webpay_hsyw\\res\\mipmap-xhdpi-v4\\app_icon.png").readBytes(),
    "mipmap-xxhdpi-v4" to File("E:\\xlcw_webpay_hsyw\\res\\mipmap-xxhdpi-v4\\app_icon.png").readBytes(),
    "mipmap-xxxhdpi-v4" to File("E:\\xlcw_webpay_hsyw\\res\\mipmap-xxxhdpi-v4\\app_icon.png").readBytes(),
  )
}

@Preview
@Composable
fun imageContainerWithColumnPreview() {
  TableWithColumn(4, "运营固定参数", operFixParam)
}