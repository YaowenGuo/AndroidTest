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
            version = tech.yaowen.android.module.Versions.CMAKE_VERSION
        }
    }
    namespace = "tech.yaowen.opengles3_native"
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
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso)
}