plugins {
    id("com.android.application")
    id("tech.yaowen.android.module")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    defaultConfig {
        applicationId = "tech.yaowen.opengles3_native"
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.10.2"
        }
    }
    // 导致 java.lang.ClassNotFoundException: Didn't find class "androidx.lifecycle.ProcessLifecycleOwnerInitializer" on path: DexPathList ...
//    dataBinding {
//        enabled = true
//    }
}

dependencies {
    implementation(project(":android_lib:theme"))
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.test.ext)
    androidTestImplementation(libs.espresso)
}