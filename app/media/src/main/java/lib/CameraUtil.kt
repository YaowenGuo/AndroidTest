package lib

import android.graphics.Bitmap
import android.graphics.Matrix
import android.hardware.camera2.CameraCharacteristics
import android.media.ExifInterface
import android.net.Uri
import android.view.OrientationEventListener
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig


fun getCameraXConfig(): CameraXConfig {
    return Camera2Config.defaultConfig()
}


fun rotateImageIfRequired(img: Bitmap, selectedImage: Uri): Bitmap? {
    val ei = ExifInterface(selectedImage.path.toString())
    val orientation: Int =
        ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270f)
        else -> img
    }
}

private fun rotateImage(img: Bitmap, degree: Float): Bitmap? {
    val matrix = Matrix()
    matrix.postRotate(degree)
    val rotatedImg: Bitmap = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
    img.recycle()
    return rotatedImg
}

fun getJpegOrientation(c: CameraCharacteristics, deviceOrientation: Int): Int {
    var deviceOrientation = deviceOrientation
    if (deviceOrientation == OrientationEventListener.ORIENTATION_UNKNOWN) return 0
    val sensorOrientation: Int = c.get(CameraCharacteristics.SENSOR_ORIENTATION)!!

    // Round device orientation to a multiple of 90
    deviceOrientation = (deviceOrientation + 45) / 90 * 90

    // Reverse device orientation for front-facing cameras
    val facingFront = c.get(CameraCharacteristics.LENS_FACING) === CameraCharacteristics.LENS_FACING_FRONT
    if (facingFront) deviceOrientation = -deviceOrientation

    // Calculate desired JPEG orientation relative to camera orientation to make
    // the image upright relative to the device orientation
    return (sensorOrientation + deviceOrientation + 360) % 360
}