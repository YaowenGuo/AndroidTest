package tech.yaowen.rtc_demo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.webrtc.SurfaceViewRenderer

@Composable
fun VideoScreen(
    modifier: Modifier = Modifier,
    onLocalViewCreated: (SurfaceViewRenderer) -> Unit,
    onRemoteViewCreated: (SurfaceViewRenderer) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Remote view (top)
        AndroidView(
            factory = { context ->
                SurfaceViewRenderer(context).also {
                    onRemoteViewCreated(it)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        
        Spacer(modifier = Modifier.height(3.dp))
        
        // Local view (bottom)
        AndroidView(
            factory = { context ->
                SurfaceViewRenderer(context).also {
                    onLocalViewCreated(it)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

@Composable
fun SingleVideoScreen(
    modifier: Modifier = Modifier,
    onLocalViewCreated: (SurfaceViewRenderer) -> Unit
) {
    AndroidView(
        factory = { context ->
            SurfaceViewRenderer(context).also {
                onLocalViewCreated(it)
            }
        },
        modifier = modifier.fillMaxSize()
    )
}
