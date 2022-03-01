import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.launch

class TopAppBarSample {
    private val screens = listOf(
        DrawerScreens.Home,
        DrawerScreens.Account,
        DrawerScreens.Help
    )

    fun topAppBar() = application {
        Window(
            onCloseRequest = ::exitApplication
        ) {
            TopAppBarView()
        }
    }

    @Composable
    private fun TopAppBarView() {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        var selected = remember { "Home" }
        val openDrawer = {
            scope.launch {
                drawerState.open()
            }
        }

        Surface(color = MaterialTheme.colors.background) {
            ModalDrawer(
                drawerState = drawerState,
                gesturesEnabled = drawerState.isOpen,
                drawerContent = {
                    Drawer(
                        onDestinationClicked = { title ->
                            scope.launch { drawerState.close() }
                            selected = title
                        }
                    )
                },
                content = {
                    when (selected) {
                        "Home" -> Home(openDrawer = { openDrawer() })
                        "Help" -> Help()
                        "Account" -> Account(openDrawer = { openDrawer() })
                    }
                }
            )
        }
    }

    /**
     * 抽屉
     */
    @Composable
    private fun Drawer(
        modifier: Modifier = Modifier,
        onDestinationClicked: (route: String) -> Unit
    ) {
        Column(
            modifier.fillMaxSize().padding(start = 24.dp, top = 48.dp)
        ) {
            Image(
                painter = painterResource("sample.png"),
                contentDescription = "App icon"
            )
            screens.forEach { screen ->
                Spacer(Modifier.height(24.dp))
                Text(
                    text = screen.title,
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.clickable {
                        onDestinationClicked(screen.title)
                    }
                )
            }
        }
    }


    /**
     * 顶部应用栏
     */
    @Composable
    private fun TopBar(title: String = "", buttonIcon: ImageVector, onButtonClick: () -> Unit) {
        TopAppBar(
            title = {
                Text(text = title)
            },
            navigationIcon = {
                IconButton(onClick = onButtonClick) {
                    Icon(buttonIcon, contentDescription = "")
                }
            },
            backgroundColor = MaterialTheme.colors.primaryVariant
        )
    }


    @Composable
    private fun Home(openDrawer: () -> Unit) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                title = "Home",
                buttonIcon = Icons.Filled.Menu,
                onButtonClick = openDrawer
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "Home Page content here.")
            }
        }
    }

    @Composable
    private fun Account(openDrawer: () -> Unit) {
        Column {
            TopBar(
                title = "Account",
                buttonIcon = Icons.Filled.Menu,
                onButtonClick = openDrawer
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Account", style = MaterialTheme.typography.h4)
            }
        }
    }

    @Composable
    private fun Help() {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                title = "Help",
                buttonIcon = Icons.Filled.ArrowBack,
                onButtonClick = {}
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Help.",
                    style = MaterialTheme.typography.h4
                )
            }
        }
    }


    sealed class DrawerScreens(val title: String) {
        object Home : DrawerScreens("Home")
        object Account : DrawerScreens("Account")
        object Help : DrawerScreens("Help")
    }
}

