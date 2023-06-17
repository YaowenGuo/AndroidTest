package tech.yaowen.media.ui.main


import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.yaowen.media.databinding.FragmentCamera2Binding
import tech.yaowen.media_tool.CameraProxy
import tech.yaowen.media_tool.capture.cameraManager
import tech.yaowen.media_tool.capture.getPreviewOutputSize

class Camera2Fragment : Fragment() {

    private val args: Camera2FragmentArgs by navArgs()

    companion object {
        fun newInstance() = CameraXFragment()
    }

    private lateinit var binding: FragmentCamera2Binding
    private val cameraManager: CameraManager by lazy { cameraManager(requireActivity().applicationContext) }

    private val characteristics: CameraCharacteristics by lazy {
        cameraManager.getCameraCharacteristics(args.cameraId)
    }


    /** [HandlerThread] where all buffer reading operations run */
    private val imageReaderThread = HandlerThread("imageReaderThread").apply { start() }

    /** [Handler] corresponding to [imageReaderThread] */
    private val imageReaderHandler = Handler(imageReaderThread.looper)

    /** Internal reference to the ongoing [CameraCaptureSession] configured with our parameters */
    private lateinit var session: CameraCaptureSession

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCamera2Binding.inflate(inflater)
        return binding.root
    }

    lateinit var cameraProxy: CameraProxy


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewFinder.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceDestroyed(holder: SurfaceHolder) = Unit

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
                Log.d("surfaceChanged:", "format: $format")
                Log.d("surfaceChanged:", "width: $format")
                Log.d("surfaceChanged:", "height: $format")
            }

            override fun surfaceCreated(holder: SurfaceHolder) {
                // Selects appropriate preview size and configures view finder
                val previewSize = getPreviewOutputSize(
                    binding.viewFinder.display,
                    characteristics,
                    SurfaceHolder::class.java
                )
                Log.d(
                    "surfaceCreated",
                    "View finder size: ${binding.viewFinder.width} x ${binding.viewFinder.height}"
                )
                Log.d("surfaceCreated", "Selected preview size: $previewSize")
                binding.viewFinder.setAspectRatio(previewSize.width, previewSize.height)

                // To ensure that size is set, initialize camera in the view's thread
                binding.overlay.post { initializeCamera() }
            }
        })
    }


    /**
     * Begin all camera operations in a coroutine in the main thread. This function:
     * - Opens the camera
     * - Configures the camera session
     * - Starts the preview by dispatching a repeating capture request
     * - Sets up the still image capture listeners
     */
    private fun initializeCamera() = lifecycleScope.launch(Dispatchers.Main) {
        // Creates list of Surfaces where the camera will output frames
        val targets = listOf(binding.viewFinder.holder.surface)

        cameraProxy = CameraProxy.Builder(requireContext())
            .surface(targets)
            .camera(args.cameraId)
            .size { config ->
                config.getOutputSizes(ImageFormat.YUV_420_888).maxByOrNull { it.height * it.width }!!
            }
            .build()

//        cameraProxy.capture(binding.viewFinder.holder.surface)
    }


    override fun onStop() {
        super.onStop()
        try {
            cameraProxy.close()
        } catch (exc: Throwable) {
            Log.e("onStop", "Error closing camera", exc)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        imageReaderThread.quitSafely()
        cameraProxy.release()
    }
}