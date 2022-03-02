import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import apktool.operFixParam

@Composable
fun <T> Table(
    columnCount: Int,
    cellWidth: (index: Int) -> Dp,
    data: List<T>,
    headerCellContent: @Composable (index: Int) -> Unit,
    rowCellContent: @Composable (index: Int, item: T) -> Unit) {

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
fun Table1(columnCount: Int) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(columnCount),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(operFixParam.keys.toList()) { key ->
            Text(
                text = key,
                modifier = Modifier.background(Color.LightGray).border(border = BorderStroke(1.dp, Color.LightGray))
            )
            Text(
                text = operFixParam[key]!!,
                modifier = Modifier.border(border = BorderStroke(1.dp, Color.LightGray))
            )
        }
    }
}