plugins {
    id("com.android.application")
    id("tech.yaowen.android.module")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    defaultConfig {
        applicationId =  "tech.yaowen.rust"
    }

    buildFeatures {
        viewBinding = true
    }
}


dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":android_lib:theme"))
    implementation(project(":android_lib:androidRust"))

    implementation(libs.appcompat)
    implementation(libs.kotlin.std)
    implementation(libs.ktx.core)
    implementation(libs.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.test.ext)
    androidTestImplementation(libs.espresso)
}
