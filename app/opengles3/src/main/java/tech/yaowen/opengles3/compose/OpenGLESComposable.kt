package tech.yaowen.opengles3.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap

/**
 * 使用 Compose 原生方式渲染 OpenGL ES 3.0 的 Composable
 */
@Composable
fun OpenGLES3Composable(
    modifier: Modifier = Modifier
) {
    val renderer = remember { OpenGLESRenderer() }

    DisposableEffect(Unit) {
        onDispose {
            renderer.release()
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width.toInt()
        val height = size.height.toInt()

        if (width > 0 && height > 0) {
            val bitmap = renderer.render(width, height)
            bitmap?.let {
                drawImage(
                    image = it.asImageBitmap()
                )
            }
        }
    }
}
