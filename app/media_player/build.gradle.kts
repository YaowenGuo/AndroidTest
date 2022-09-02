plugins {
    id("com.android.application")
    id("tech.yaowen.android.module")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    defaultConfig {
        applicationId =  "tech.yaowen.media_player"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":android_lib:theme"))
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.kotlin.std)
    implementation(libs.ktx.core)
    implementation(libs.ktx.viewmodel)
    implementation(libs.exoplayer)

    testImplementation(libs.junit)
    androidTestImplementation(libs.test.ext)
    androidTestImplementation(libs.espresso)

    implementation(project(":android_lib:ffmpeg"))
}