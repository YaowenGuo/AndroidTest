plugins {
    id("com.android.library")
    id("tech.yaowen.android.module")
    id("kotlin-android")
    id("kotlin-kapt")
}

dependencies {
    // EXIF Interface
    implementation(libs.exifinterface)

    api(libs.squareup.okhttp)
    implementation(libs.squareup.okhttplog)
    implementation(libs.socket.io)
}
android {
    namespace = "tech.signaling"
}
