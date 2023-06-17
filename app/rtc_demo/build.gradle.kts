plugins {
    id("com.android.application")
    id("tech.yaowen.android.module")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    defaultConfig {
        applicationId =  "tech.yaowen.rtc_demo"
    }

    buildFeatures {
        viewBinding = true
    }
    namespace = "tech.yaowen.rtc_demo"
}


dependencies {
    implementation(project(":android_lib:theme"))
    implementation(project(":android_lib:socket_io_signaling"))
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.kotlin.std)
    implementation(libs.ktx.core)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))
//    implementation(libs.webrtc)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso)
}
