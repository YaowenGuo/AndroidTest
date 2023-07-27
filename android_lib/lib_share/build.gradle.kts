plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("tech.yaowen.android.module")
}


dependencies {
    implementation(project(":android_lib:theme"))
    // media player
    implementation(libs.media3.player)
    implementation(libs.media3.ui)

    testImplementation(libs.junit)
}

android {
    namespace = "tech.yaowen.lib_share"

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = tech.yaowen.android.module.Versions.CMAKE_VERSION
        }
    }
}
