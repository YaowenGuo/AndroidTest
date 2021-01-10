package tech.yaowen.rtc_demo

import android.hardware.camera2.CameraMetadata
import android.os.Bundle
import org.webrtc.*
import org.webrtc.PeerConnectionFactory.InitializationOptions
import tech.yaowen.rtc_demo.lib.RtcEngine

class MainActivity : BaseActivity() {

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
            // Must use save eglContext
            val eglBaseContext = EglBase.create().eglBaseContext

            val peerConnection = RtcEngine.INSTANCE.createPeerConnection(this)
            val videoTrack = RtcEngine.INSTANCE.createVideoTrack(peerConnection, captureId, it, this, eglBaseContext)
            val audeoTrack = RtcEngine.INSTANCE.createAudioTrack(peerConnection, captureId)
            RtcEngine.INSTANCE.displayVideo(videoTrack, findViewById(R.id.localView), eglBaseContext)
        }
    }


    private fun localCapture(videoCapturer: VideoCapturer) {
        // create PeerConnectionFactory
        val initializationOptions = InitializationOptions.builder(this).createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)
        val peerConnectionFactory = PeerConnectionFactory.builder().createPeerConnectionFactory()

        // create VideoTrack
        val videoSource: VideoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast)
        val videoTrack: VideoTrack = peerConnectionFactory.createVideoTrack("101", videoSource)

        // create AudioSource
        val audioSource: AudioSource = peerConnectionFactory.createAudioSource(MediaConstraints())
        val audioTrack: AudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource)

        val eglBaseContext = EglBase.create().eglBaseContext
        val localView: SurfaceViewRenderer = findViewById(R.id.localView)
        localView.init(eglBaseContext, null)
        localView.setMirror(true)
        // display in localView
        videoTrack.addSink(localView)

        val surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBaseContext)
        videoCapturer.initialize(
            surfaceTextureHelper,
            this,
            videoSource.capturerObserver
        )
        videoCapturer.startCapture(480, 640, 30)
    }
}
