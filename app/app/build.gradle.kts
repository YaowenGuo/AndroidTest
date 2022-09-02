plugins {
    id("com.android.application")
    id("tech.yaowen.android.module")
    id("kotlin-android")
    id("kotlin-kapt")
}

//apply plugin: "org.jetbrains.kotlin.android"

android {
    defaultConfig {
        applicationId = "tech.yaowen.customview"
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":android_lib:theme"))
    implementation(libs.kotlin.std)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle)
    implementation(libs.ktx.core)
    implementation(libs.ktx.viewmodel)
    implementation(libs.ktx.navigation)
    implementation(libs.ktx.fragment)
    implementation(libs.ktx.work)
    implementation(libs.dynamicanimation)
    implementation(libs.concurrent)
    implementation(libs.lifecycle.process)
    implementation(libs.lifecycle.common)

    testImplementation(libs.junit)
    androidTestImplementation(libs.runner)
    androidTestImplementation(libs.espresso)
    implementation(project(":lib:lib_annotations"))

//    implementation project(path: ":test_annotation")
//    annotationProcessor project(path: ":lib_butter_knife")
    implementation(project(":android_lib:lib_share"))
    kapt(project(":lib:lib_processor"))

}
