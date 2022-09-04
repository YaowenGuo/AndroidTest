plugins {
    id("com.android.application")
    id("tech.yaowen.android.module")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    defaultConfig {
        applicationId =  "tech.yaowen.opengles3"
    }

    buildFeatures {
        viewBinding = true
    }
}


dependencies {
    implementation(project(":android_lib:theme"))
    implementation(libs.appcompat)
    implementation(libs.kotlin.std)
    implementation(libs.ktx.core)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle)
    implementation(libs.ktx.viewmodel)
    implementation(libs.ktx.fragment)
    implementation(libs.ktx.navigation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.test.ext)
    androidTestImplementation(libs.espresso)
}
