plugins {
    id("com.android.library")
    id("tech.yaowen.android.module")
    id("kotlin-android")
    id("kotlin-kapt")
}

dependencies {
    // EXIF Interface
    implementation(libs.exifinterface)
    implementation(libs.kotlin.coroutines)

    implementation(libs.camera2)
    // If you want to additionally use the CameraX Lifecycle library
    implementation(libs.camera.lifecycle)
    // If you want to additionally use the CameraX View class
    implementation(libs.camera.view)
    // If you want to additionally use the CameraX Extensions library
    implementation(libs.camera.extensions)
}
android {
    namespace = "tech.yaowen.media_tool"
}
