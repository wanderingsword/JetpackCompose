package apktool

import androidx.compose.desktop.DesktopTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


val SideBarBackground = Color(0xFAFAFAFF)
val SideBarItemTitleColor = Color(0xBCBCC0FF)

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
