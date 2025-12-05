package tech.yaowen.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlin.collections.contains

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                BasicText("测试")
                CameraPermissionRequest { granted ->
                    // Handle the permission result here
                }
                BasicText("测试")
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        this.requestPermission()
    }


    fun requestPermission() {
        if (hasPermissions(this)) {

        } else {
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }

            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

    }


    fun hasPermissions(context: Context) = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED


    fun enumerateCameras(): List<FormatItem> {
        val cm = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val availableCameras: MutableList<FormatItem> = mutableListOf()

        // Get list of all compatible cameras
        val cameraIds = cm.cameraIdList.filter {
            val characteristics = cm.getCameraCharacteristics(it)
            val capabilities = characteristics.get(
                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)
            capabilities?.contains(
                CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE) ?: false
        }


        // Iterate over the list of cameras and return all the compatible ones
        cameraIds.forEach { id ->
            val characteristics = cm.getCameraCharacteristics(id)
            val orientation = lensOrientationString(
                characteristics.get(CameraCharacteristics.LENS_FACING)!!)

            // Query the available capabilities and output formats
            val capabilities = characteristics.get(
                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)!!
            val outputFormats = characteristics.get(
                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!.outputFormats

            // All cameras *must* support JPEG output so we don't need to check characteristics
            availableCameras.add(
                FormatItem(
                    "$orientation JPEG ($id)", id, ImageFormat.JPEG)
            )

            // Return cameras that support RAW capability
            if (capabilities.contains(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW)
                && outputFormats.contains(ImageFormat.RAW_SENSOR)) {
                availableCameras.add(
                    FormatItem(
                        "$orientation RAW ($id)", id, ImageFormat.RAW_SENSOR)
                )
            }

            // Return cameras that support JPEG DEPTH capability
            if (capabilities.contains(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT)
                && outputFormats.contains(ImageFormat.DEPTH_JPEG)) {
                availableCameras.add(
                    FormatItem(
                        "$orientation DEPTH ($id)", id, ImageFormat.DEPTH_JPEG)
                )
            }
        }

        return availableCameras
    }

    fun lensOrientationString(value: Int) = when (value) {
        CameraCharacteristics.LENS_FACING_BACK -> "Back"
        CameraCharacteristics.LENS_FACING_FRONT -> "Front"
        CameraCharacteristics.LENS_FACING_EXTERNAL -> "External"
        else -> "Unknown"
    }

    @Composable
    fun CameraPermissionRequest(
        onPermissionResult: (Boolean) -> Unit
    ) {
        val context = LocalContext.current
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            onPermissionResult(isGranted)
        }

        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            } else {
                onPermissionResult(true)
            }
        }
    }
}

data class FormatItem(val title: String, val cameraId: String, val format: Int)

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
//    BasicText(
//        text = "Hello $name!",
//        modifier = modifier
//    )
}
