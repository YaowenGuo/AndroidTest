plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("tech.yaowen.android.module")
}

dependencies {
    implementation(project(":android_lib:theme"))

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

    testImplementation(libs.junit)
}
android {
    namespace = "tech.yaowen.media_tool"
}
