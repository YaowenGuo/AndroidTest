plugins {
    id("com.android.application")
    id("tech.yaowen.android.module")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    defaultConfig {
        applicationId = "tech.yaowen.rtc_native"
        //camera2ndk only can be use above android api 24
        minSdk = 24
        ndk {
//            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            abiFilters += "arm64-v8a"
        }
        externalNativeBuild {
            cmake {
                arguments += "-DANDROID_STL=c++_static"
                // https://developer.android.com/studio/projects/gradle-external-native-builds
                // Passes optional arguments to CMake.
                //arguments "-DANDROID_ARM_NEON=TRUE", "-DANDROID_TOOLCHAIN=clang"

                // either -DCMAKE_FIND_DEBUG_MODE=true or --debug-find to your CMake command.
//                arguments "--debug-find"

                // Sets a flag to enable format macro constants for the C compiler.
                // cFlags "-D__STDC_FORMAT_MACROS"

                // Sets optional flags for the C++ compiler.
                // cppFlags "-fexceptions", "-frtti"
//                cppFlags "-fno-rtti"
                cppFlags += "-fvisibility=hidden"
            }
        }
        multiDexEnabled = true
        multiDexKeepProguard = file("proguard-rules.pro")
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.18.1"
        }
    }

    buildFeatures {
        viewBinding = true
    }
}


dependencies {
    implementation(project(":android_lib:theme"))
    implementation(project(":android_lib:socket_io_signaling"))

    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.test.ext)
    androidTestImplementation(libs.espresso)
}