plugins {
    id("com.android.application")
    id("tech.yaowen.android.module")
    id("kotlin-android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
}

android {
    defaultConfig {
        applicationId = "tech.yaowen.media"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":android_lib:theme"))
    implementation(libs.appcompat)
    implementation(libs.kotlin.std)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle)
    implementation(libs.ktx.core)
    implementation(libs.ktx.viewmodel)
    implementation(libs.ktx.fragment)
    implementation(libs.ktx.navigation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.test.ext)
    androidTestImplementation(libs.espresso)

    implementation(libs.camera2)
    // If you want to additionally use the CameraX Lifecycle library
    implementation(libs.camera.lifecycle)
    // If you want to additionally use the CameraX View class
    implementation(libs.camera.view)
    // If you want to additionally use the CameraX Extensions library
    implementation(libs.camera.extensions)

    // Feature module Support
    implementation(libs.navigation.dynamic)

    // Testing Navigation
    androidTestImplementation(libs.navigation.test)

    implementation(project(":android_lib:media_tool"))

}
