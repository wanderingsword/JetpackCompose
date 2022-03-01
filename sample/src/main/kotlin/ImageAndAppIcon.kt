import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.application
import androidx.compose.ui.window.singleWindowApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class ImageAndAppIcon {
    fun loadImagesFromResource() = singleWindowApplication {
        Image(
            painter = painterResource("sample.png"),
            contentDescription = "Sample",
            modifier = Modifier.fillMaxSize()
        )
    }

    fun loadImageAsynchronously() = singleWindowApplication {
        val density = LocalDensity.current
    }

    @Composable
    fun <T> AsyncImage(
        load: suspend () -> T,
        painterFor: @Composable (T) -> Painter,
        contentDescription: String,
        modifier: Modifier = Modifier,
        contentScale: ContentScale = ContentScale.Fit
    ) {
        val image: T? by produceState<T?>(null) {
            value = withContext(Dispatchers.IO) {
                try {
                    //加载图片资源
                    load()
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }
            }
        }

        if (image != null) {
            Image(
                painter = painterFor(image!!),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
    }


}