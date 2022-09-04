plugins {
    id("com.android.application")
    id("tech.yaowen.android.module")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    defaultConfig {
        applicationId = "tech.yaowen.lifecycle"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":android_lib:theme"))
    implementation(libs.kotlin.std)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle)
    implementation(libs.ktx.core)
    implementation(libs.ktx.viewmodel)
    testImplementation(libs.junit)
    androidTestImplementation(libs.test.ext)
    androidTestImplementation(libs.espresso)
    kapt(libs.lifecycle.compiler)
}
