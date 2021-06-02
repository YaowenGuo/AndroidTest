package tech.yaowen.rtc_demo

import android.hardware.camera2.CameraMetadata
import android.os.Bundle
import org.webrtc.*
import org.webrtc.PeerConnectionFactory.InitializationOptions
import tech.yaowen.rtc_demo.lib.RtcEngine

class VideoCaputreActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onHaveCameraPermission() {
        createLocalCapture()
    }


    private fun createLocalCapture() {
        val videoCapturer = RtcEngine.INSTANCE.createCameraCapturer(this, CameraMetadata.LENS_FACING_FRONT)
        videoCapturer?.let {
            val captureId = "1"
            // Must use same eglContext
            val eglBaseContext = EglBase.create().eglBaseContext

            val peerConnectionFactory = RtcEngine.INSTANCE.createPeerConnectionFactory(this)
            val videoTrack = RtcEngine.INSTANCE.createVideoTrack(peerConnectionFactory, captureId, it, this, eglBaseContext)
            val audeoTrack = RtcEngine.INSTANCE.createAudioTrack(peerConnectionFactory, captureId)
            RtcEngine.INSTANCE.displayVideo(videoTrack, findViewById(R.id.localView), eglBaseContext)
        }
    }
}
