package apktool

import androidx.compose.desktop.DesktopTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ContentBackground = Color(0xFFFAFAFA)
val SideBarItemTitleColor = Color(0xFF49494E)
val ItemHoverTextColor = Color(0xFF5856D5)
val ItemHoverBackgroundColor = Color(0xFFDEDDF7)
val BorderColor = Color(0xFFDFE2E5)
val TextColor = Color(0xFF929292)


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
