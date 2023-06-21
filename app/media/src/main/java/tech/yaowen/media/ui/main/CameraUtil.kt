package tech.yaowen.media.ui.main

import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.LifecycleOwner
import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.core.content.ContextCompat

class CameraUtil {
    companion object {
        const val CAMERA_ERROR = "camera_error"
    }
    private fun startCamera(context: Context, lifecycleOwner: LifecycleOwner, surfaceProvider: Preview.SurfaceProvider) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview)

            } catch(exc: Exception) {
                //   TODO log error,
//                Loggers.cloud.error(CAMERA_ERROR, exc)
            }

        }, ContextCompat.getMainExecutor(context))
    }
}