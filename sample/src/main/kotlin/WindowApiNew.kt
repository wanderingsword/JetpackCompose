import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import kotlinx.coroutines.delay

class WindowApiNew {
    fun main() = application {
        var fileName by remember { mutableStateOf("Untitled") }

        Window(onCloseRequest = ::exitApplication, title = "$fileName - Editor") {
            Button(onClick = { fileName = "note.txt" }) {
                Text("Save")
            }
        }

        var isPerformingTask by remember { mutableStateOf(true) }

        //启动一个协程，2s 之后将 isPerformingTask 置为 false
        LaunchedEffect(Unit) {
            delay(2000)
            isPerformingTask = false
        }

        if (isPerformingTask) {
            Window(onCloseRequest = ::exitApplication) {
                Text("Performing some task. Please wait!")
            }
        } else {
            Window(onCloseRequest = ::exitApplication) {
                Text("Hello World!!")
            }
        }
    }

    fun CustomCloseLogic() = application {
        //isOpen 控制 Window 的开启或关闭
        var isOpen by remember { mutableStateOf(true) }
        //isAskClose 控制 Dialog 的开启或关闭。
        var isAskClose by remember { mutableStateOf(false) }

        if (isOpen) {
            Window(onCloseRequest = { isAskClose = true }) {
                if (isAskClose) {
                    //对话框点击左上角的叉会触发 isAskClose = false，然后触发 recomposable，Dialog 就会关闭。
                    Dialog(
                        onCloseRequest = { isAskClose = false },
                        title = "Close the document without saving?"
                    ) {

                        //将 isOpen 置为 false，会触发最外层 if 条件语句的重新执行，isOpen 为 false，则不会显示 Window
                        Button(onClick = { isOpen = false }) {
                            Text("Yes")
                        }
                    }
                }
            }
        }
    }

    fun hideWindowIntoTray() = application {
        var isVisible by remember { mutableStateOf(true) }
        var isOpen by remember { mutableStateOf(true) }

        if (isOpen) {
            Window(
                onCloseRequest = { if (isTraySupported) isVisible = false else isOpen = false },
                visible = isVisible,
                title = "Counter"
            ) {
                var count by remember { mutableStateOf(0) }
                LaunchedEffect(Unit) {
                    while (true) {
                        count++
                        delay(1000)
                    }
                }
                Text(count.toString())
            }
        }

        if (!isVisible) {
            Tray(
                TrayIcon,
                tooltip = "Counter",
                onAction = { isVisible = true },
                menu = {
                    Item("Exit", onClick = ::exitApplication)
                }
            )
        }

    }

    object TrayIcon : Painter() {
        override val intrinsicSize: Size = Size(256f, 256f)

        override fun DrawScope.onDraw() {
            drawOval(Color(0xFFFFA500))
        }
    }

    fun openCloseMultiWindow() = application {
        val applicationState = remember { MyApplicationState() }

        for (window in applicationState.windows) {
            //key 用于对 Composable 中的子 Composable 进行管理
            key(window) {
                MyWindow(window)
            }
        }
    }

    @Composable
    private fun MyWindow(window: MyWindowState) = Window(
        title = window.title,
        onCloseRequest = window::close
    ) {
        //创建一个菜单栏
        MenuBar {
            //添加菜单
            Menu("File") {
                //添加菜单项
                Item("New Window", onClick = window.openNewWindow)
                Item("Exit", onClick = window.exit)
            }
        }
    }

    fun adaptiveWindowSize() = application {
        Window(
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(width = Dp.Unspecified, height = Dp.Unspecified),
            title = "Adaptive",
            resizable = false
        ) {
            Column(Modifier.background(Color(0xFFEEEEEE))) {
                Row {
                    Text("label 1", Modifier.size(100.dp, 100.dp).padding(10.dp).background(Color.White))
                    Text("label 2", Modifier.size(150.dp, 200.dp).padding(5.dp).background(Color.White))
                    Text("label 3", Modifier.size(200.dp, 300.dp).padding(25.dp).background(Color.White))
                }
            }
        }
    }
}

//管理应用程序的状态
private class MyApplicationState {
    val windows = mutableStateListOf<MyWindowState>()

    init {
        windows += MyWindowState("Init Window")
    }

    private fun MyWindowState(title: String) = MyWindowState(title, openNewWindow = ::openNewWindow, exit = ::exit, windows::remove)

    private fun openNewWindow() {
        windows += MyWindowState("Window ${windows.size}")
    }

    private fun exit() {
        windows.clear()
    }
}

//单个窗口的状态
private class MyWindowState(
    val title: String,
    val openNewWindow: () -> Unit,
    val exit: () -> Unit,
    private val close: (MyWindowState) -> Unit
) {
    fun close() = close(this)
}


