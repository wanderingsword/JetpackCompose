import androidx.compose.desktop.DesktopTheme
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp


@Composable
fun ApkToolTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        content = content
    )
}

@Composable
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
)
