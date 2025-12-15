package tech.yaowen.rtc_demo

import android.hardware.camera2.CameraMetadata
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import org.webrtc.*
import tech.yaowen.rtc_demo.lib.RtcEngine
import tech.yaowen.rtc_demo.ui.SingleVideoScreen

class VideoCaputreActivity : BaseActivity() {
    private var localView: org.webrtc.SurfaceViewRenderer? = null
    private val eglBaseContext = EglBase.create().eglBaseContext

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SingleVideoScreen(
                        onLocalViewCreated = { view ->
                            localView = view
                            if (localView != null) {
                                createLocalCapture()
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onHaveCameraPermission() {
        if (localView != null) {
            createLocalCapture()
        }
    }

    private fun createLocalCapture() {
        val videoCapturer = RtcEngine.INSTANCE.createCameraCapturer(this, CameraMetadata.LENS_FACING_FRONT)
        videoCapturer?.let {
            val captureId = "1"
            val peerConnectionFactory = RtcEngine.INSTANCE.createPeerConnectionFactory(this)
            val videoTrack = RtcEngine.INSTANCE.createVideoTrack(peerConnectionFactory, captureId, it, this, eglBaseContext)
            val audeoTrack = RtcEngine.INSTANCE.createAudioTrack(peerConnectionFactory, captureId)
            localView?.let { view ->
                RtcEngine.INSTANCE.displayVideo(videoTrack, view, eglBaseContext)
            }
        }
    }
}
