import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.runtime.getValue

//import androidx.compose.runtime.setValue

class ApkToolMain {
    fun main() = application {
        val showSideBar by remember { mutableStateOf(true) }

        Window(
            onCloseRequest = ::exitApplication
        ) {
            ApkToolThemeDeskTopTheme {
                Row(Modifier.fillMaxSize()) {
                    if (showSideBar) {
                        Column {
                            optionsList()
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun optionsList() {
        LazyColumn {
            VerticalScrollbar()
        }
    }
}
