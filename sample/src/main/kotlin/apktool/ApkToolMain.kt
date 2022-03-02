package apktool

import Table1
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

//import androidx.compose.runtime.setValue

private const val OPTION_PROPERTY = "参数"
private val options = mutableListOf(OPTION_PROPERTY)

class ApkToolMain {

    fun main() = application {
        val showSideBar by remember { mutableStateOf(true) }
        val selectOption by remember { mutableStateOf(OPTION_PROPERTY) }

        Window(
            onCloseRequest = ::exitApplication
        ) {
            Surface(Modifier.fillMaxSize()) {
                ApkToolTheme {
                    Row {
                        if (showSideBar) {
                            Box(modifier = Modifier.width(200.dp).background(color = SideBarBackground)) {
                                optionsList()
                            }
                        }
                        Spacer(modifier = Modifier.width(1.dp).fillMaxHeight())
                        Box(Modifier.fillMaxHeight()) {
                            optionView(selectOption)
                        }
                    }

                }
            }

        }
    }


    @Composable
    fun optionsList() {
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
        Row {
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = AnnotatedString(title),
                maxLines = 1,
                color = SideBarItemTitleColor,
                modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 8.dp, vertical = 2.dp),
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    @Composable
    fun optionView(title: String) {
        Table1(4)
    }
}
