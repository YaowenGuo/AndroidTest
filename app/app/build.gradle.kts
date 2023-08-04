plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    id("tech.yaowen.android.module")
}

android {
    defaultConfig {
        applicationId = "tech.yaowen.customview"
        externalNativeBuild {
            cmake {
                arguments += "-DANDROID_STL=c++_shared"
            }
        }
    }
    namespace = "tech.yaowen.customview"
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
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
    implementation(libs.ktx.navigation)
    implementation(libs.ktx.fragment)
    implementation(libs.ktx.work)
    implementation(libs.dynamicanimation)
    implementation(libs.concurrent)
    implementation(libs.lifecycle.process)
    implementation(libs.lifecycle.common)

    implementation(libs.androidx.compose.activity)
    implementation(libs.android.material)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso)

    implementation(project(":android_lib:lib_share"))

}
