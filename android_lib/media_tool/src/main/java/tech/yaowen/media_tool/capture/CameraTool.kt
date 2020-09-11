package tech.yaowen.media_tool.capture

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

fun cameraManager(context: Context): CameraManager {
    return context.applicationContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
}


/** Opens the camera and returns the opened device (as the result of the suspend coroutine) */
@SuppressLint("MissingPermission")
suspend fun openCamera(
    manager: CameraManager,
    cameraId: String,
    handler: Handler? = null
): CameraDevice = suspendCancellableCoroutine { cont ->
    manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
        override fun onOpened(device: CameraDevice) = cont.resume(device) { }

        override fun onDisconnected(device: CameraDevice) {
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
            if (cont.isActive) cont.resumeWithException(exc)
        }
    }, handler)
}

/** Helper class used as a data holder for each selectable camera format item */
private data class FormatItem(val title: String, val cameraId: String, val format: Int)

/** Helper function used to list all compatible cameras and supported pixel formats */
private fun enumerateCameras(cameraManager: CameraManager): List<FormatItem> {
    val availableCameras: MutableList<FormatItem> = mutableListOf()

    // Get list of all compatible cameras
    val cameraIds = cameraManager.cameraIdList.filter {
        val characteristics = cameraManager.getCameraCharacteristics(it)
        val capabilities = characteristics.get(
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)

        val keys = characteristics.keys

        capabilities?.contains(
            CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE) ?: false
    }


    // Iterate over the list of cameras and return all the compatible ones
    cameraIds.forEach { id ->
        val characteristics = cameraManager.getCameraCharacteristics(id)
        val orientation = characteristics.get(CameraCharacteristics.LENS_FACING)
        val orientationStr = orientation?.let { lensOrientationString(it) } ?: ""
        // Query the available capabilities and output formats
        val capabilities = characteristics.get(
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)!!
        val outputFormats = characteristics.get(
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!.outputFormats

        // All cameras *must* support JPEG output so we don't need to check characteristics
        availableCameras.add(FormatItem("$orientationStr JPEG ($id)", id, ImageFormat.JPEG))

        // Return cameras that support RAW capability
        if (capabilities.contains(
                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW) &&
            outputFormats.contains(ImageFormat.RAW_SENSOR)) {
            availableCameras.add(FormatItem(
                "$orientationStr RAW ($id)", id, ImageFormat.RAW_SENSOR))
        }

        // Return cameras that support JPEG DEPTH capability
        if (capabilities.contains(
                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT) &&
            outputFormats.contains(ImageFormat.DEPTH_JPEG)) {
            availableCameras.add(FormatItem(
                "$orientationStr DEPTH ($id)", id, ImageFormat.DEPTH_JPEG))
        }
    }

    return availableCameras
}

/** Helper function used to convert a lens orientation enum into a human-readable string */
private fun lensOrientationString(value: Int) = when(value) {
    CameraCharacteristics.LENS_FACING_BACK -> "Back"
    CameraCharacteristics.LENS_FACING_FRONT -> "Front"
    CameraCharacteristics.LENS_FACING_EXTERNAL -> "External"
    else -> "Unknown"
}
