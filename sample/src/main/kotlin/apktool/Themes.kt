package apktool

import androidx.compose.desktop.DesktopTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import apktool.xlcw.appName

val ContentBackground = Color(0xFFFAFAFA)
val SideBarItemTitleColor = Color(0xFF49494E)
val ItemHoverTextColor = Color(0xFF5856D5)
val ItemHoverBackgroundColor = Color(0xFFDEDDF7)
val BorderColor = Color(0xFFDFE2E5)
val TextColor = Color(0xFF929292)


val APK_ICON_WIDTH = 50.dp

@Composable
fun ApkToolTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme {
        DesktopTheme {
            content()
        }
    }
}


@Composable
fun SideCardText() {

}

/*@Composable
fun ApkToolThemeDeskTopTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalScrollbarStyle provides scrollbar,
        content = content
    )
}

val scrollbar = ScrollbarStyle(
    minimalHeight = 16.dp,
    thickness = 8.dp,
    shape = MaterialTheme.shapes.small,
    hoverDurationMillis = 300,
    unhoverColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
    hoverColor = MaterialTheme.colors.onSurface.copy(alpha = 0.50f)
)*/
