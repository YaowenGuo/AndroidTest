package tech.yaowen.media_tool

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.hardware.camera2.params.StreamConfigurationMap
import android.os.Handler
import android.os.HandlerThread
import android.util.ArrayMap
import android.util.Log
import android.util.Size
import android.view.Surface
import androidx.annotation.WorkerThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import tech.yaowen.media_tool.capture.cameraManager
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CameraProxy private constructor(private val context: Context) {

    private val cameraManager: CameraManager = cameraManager(context)
    val cameraIds = cameraManager.cameraIdList.filter {
        var characteristics = cameraManager.getCameraCharacteristics(it)
        val capabilities = characteristics.get(
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES
        )
        capabilities?.contains(
            CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE
        ) ?: false
    }

    var characteristics: CameraCharacteristics? = null

    lateinit var session: CameraCaptureSession

    val lensFacingMap: MutableMap<Int, String> = ArrayMap()

    fun getLensFacing(): MutableSet<Int> {
        return lensFacingMap.keys
    }

    init {
        for (id in cameraManager.cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(id)
            val orientation: Int = characteristics.get(CameraCharacteristics.LENS_FACING)!!
            if (!lensFacingMap.containsKey(orientation)) {
                lensFacingMap[orientation] = id
            }

        }
    }

    fun getCharacteristics(cameraId: String): CameraCharacteristics {
        return cameraManager.getCameraCharacteristics(cameraId)
    }


    /** [HandlerThread] where all camera operations run */
    private val cameraThread = HandlerThread("CameraThread").apply { start() }
    private val cameraHandler = Handler(cameraThread.looper)

    lateinit var camera: CameraDevice

    companion object {
        private var cameraProxy: CameraProxy? = null

        @JvmStatic
        private fun instance(context: Context): CameraProxy {
            if (cameraProxy == null) {
                synchronized(CameraProxy::class.java) {
                    if (cameraProxy == null) {
                        cameraProxy = CameraProxy(context)
                    }
                }
            }
            return cameraProxy!!
        }
    }

    /**
     * Begin all camera operations in a coroutine in the main thread. This function:
     * - Opens the camera
     * - Configures the camera session
     * - Starts the preview by dispatching a repeating capture request
     * - Sets up the still image capture listeners
     */
    protected fun initializeCamera(cameraId: String, surfaceList: List<Surface>): CameraProxy {
        MainScope().launch(Dispatchers.Main) {
            // Open the selected camera
            camera = openCamera(cameraId, cameraHandler)
            characteristics = cameraManager.getCameraCharacteristics(cameraId)

            // Start a capture session using our open camera and list of Surfaces where frames will go
            session = createCaptureSession(camera, surfaceList, cameraHandler)
            capture(surfaceList[0])
        }

        return this
    }

    /**
     * Starts a [CameraCaptureSession] and returns the configured session (as the result of the
     * suspend coroutine
     */

    protected suspend fun createCaptureSession(
        device: CameraDevice,
        targets: List<Surface>,
        handler: Handler? = null
    ): CameraCaptureSession = suspendCoroutine { cont ->
        val configCallback = object : CameraCaptureSession.StateCallback() {

            override fun onConfigured(session: CameraCaptureSession) = cont.resume(session)

            override fun onConfigureFailed(session: CameraCaptureSession) {
                val exc = RuntimeException("Camera ${device.id} session configuration failed")
                Log.e("createCaptureSession", exc.message, exc)
                cont.resumeWithException(exc)
            }
        }

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
//            val outputConfig = OutputConfiguration(targets[0])
//            val config = SessionConfiguration(
//                SessionConfiguration.SESSION_REGULAR,
//                listOf(outputConfig),
//                Executor {
//
//                }, configCallback
//            )
//
//            device.createCaptureSession(config)
//        } else {
        // Create a capture session using the predefined targets; this also involves defining the
        // session state callback to be notified of when the session is ready
        device.createCaptureSession(targets, configCallback, handler)
//        }
    }

    fun preview(target: Surface) {
        val captureRequest = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        captureRequest.addTarget(target)

        // This will keep sending the capture request as frequently as possible until the
        // session is torn down or session.stopRepeating() is called
        session.setRepeatingRequest(captureRequest.build(), null, cameraHandler)
    }

    fun capture(target: Surface) {
        val captureRequest = camera.createCaptureRequest(
            CameraDevice.TEMPLATE_PREVIEW
        ).apply { addTarget(target) }

        // This will keep sending the capture request as frequently as possible until the
        // session is torn down or session.stopRepeating() is called
        session.setRepeatingRequest(captureRequest.build(), null, cameraHandler)
    }

    fun adjust() {

    }


    /** Opens the camera and returns the opened device (as the result of the suspend coroutine) */
    @SuppressLint("MissingPermission")
    @WorkerThread
    private suspend fun openCamera(cameraId: String, handler: Handler? = null):
            CameraDevice = suspendCancellableCoroutine { cont ->
        cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(device: CameraDevice) = cont.resume(device)

            override fun onDisconnected(device: CameraDevice) {
                Log.w("camera Disconnected", "Camera $cameraId has been disconnected")
                val exc = RuntimeException("Camera $cameraId has been disconnected")
                if (cont.isActive) cont.resumeWithException(exc)
            }

            override fun onError(device: CameraDevice, error: Int) {
                val msg = when (error) {
                    ERROR_CAMERA_DEVICE -> "Fatal (device)"
                    ERROR_CAMERA_DISABLED -> "Device policy"
                    ERROR_CAMERA_IN_USE -> "Camera in use"
                    ERROR_CAMERA_SERVICE -> "Fatal (service)"
                    ERROR_MAX_CAMERAS_IN_USE -> "Maximum cameras in use"
                    else -> "Unknown"
                }
                val exc = RuntimeException("Camera $cameraId error: ($error) $msg")
                Log.e("open camera onError", exc.message, exc)
                if (cont.isActive) cont.resumeWithException(exc)
            }
        }, handler)
    }

    public class Builder(private val appContext: Context) {
        private var surfaceList: List<Surface>? = null
        private var selectLensFacing: (types: IntArray) -> Int = { CameraMetadata.LENS_FACING_BACK }
        private var lensFacing: Int = 0
        private var cameraId: String? = null

        public fun surface(surfaces: List<Surface>): Builder {
            surfaceList = surfaces
            return this
        }

        public fun camera(selectorCamera: (types: IntArray) -> Int): Builder {
            selectLensFacing = selectorCamera
            return this
        }

        public fun camera(cameraId: String): Builder {
            this.cameraId = cameraId
            return this
        }


        public fun build(): CameraProxy {
            surfaceList = if (surfaceList == null) {
                listOf(Surface(SurfaceTexture(0)))
            } else {
                surfaceList
            }
            val manager = cameraManager(appContext)
            if (cameraId == null) {
                lensFacing = selectLensFacing(getLensFacing(manager))

                for (id in manager.cameraIdList) {
                    val characteristics = manager.getCameraCharacteristics(id)
                    if (lensFacing == characteristics.get(CameraCharacteristics.LENS_FACING)) {
                        cameraId = id
                        break
                    }
                }

                if (cameraId == null) {
                    throw Exception("No camera facing to: $lensFacing")
                }
            }
            return instance(appContext)
                .initializeCamera(cameraId!!, surfaceList!!)
        }

        public fun size(selectSize: (StreamConfigurationMap) -> Size): Builder {

            return this
        }

        private fun getLensFacing(manager: CameraManager): IntArray {
            val set: MutableSet<Int> = HashSet()
            for (id in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(id)
                val orientation: Int = characteristics.get(CameraCharacteristics.LENS_FACING)!!
                set.add(orientation)
            }
            return set.toIntArray()
        }
    }


    enum class LensFacing {
        BACK,
        FRONT,
        EXTERNAL,
    }

    public fun close() {
        try {
            camera.close()
        } catch (exc: Throwable) {
            Log.e("onStop", "Error closing camera", exc)
        }
    }

    public fun release() {
        cameraThread.quitSafely()
        cameraProxy = null
    }

}



