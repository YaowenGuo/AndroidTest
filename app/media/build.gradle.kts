plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    id("tech.yaowen.android.module")
    id("androidx.navigation.safeargs")
}


android {
    defaultConfig {
        applicationId = "tech.yaowen.media"
    }

    buildFeatures {
        viewBinding = true
    }
    namespace = "tech.yaowen.media"
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
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso)

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
