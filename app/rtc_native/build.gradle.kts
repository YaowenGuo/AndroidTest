plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "tech.yaowen.rtc_native"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "tech.yaowen.rtc_native"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
//            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            abiFilters += "arm64-v8a"
        }
//        externalNativeBuild {
//            cmake {
////                arguments += "-DANDROID_STL=c++_static"
//
////                cppFlags "-DANDROID_STL=c++_shared"
//                // https://developer.android.com/studio/projects/gradle-external-native-builds
//                // Passes optional arguments to CMake.
//                //arguments "-DANDROID_ARM_NEON=TRUE", "-DANDROID_TOOLCHAIN=clang"
//
//                // either -DCMAKE_FIND_DEBUG_MODE=true or --debug-find to your CMake command.
////                arguments "--debug-find"
//
//                // Sets a flag to enable format macro constants for the C compiler.
//                // cFlags "-D__STDC_FORMAT_MACROS"
//
//                // Sets optional flags for the C++ compiler.
//                // cppFlags "-fexceptions", "-frtti"
////                cppFlags "-fno-rtti"
//                cppFlags += "-fvisibility=hidden"
//                cppFlags += "-DANDROID_STL=c++_shared"
//
//            }
//        }
        multiDexEnabled = true
        multiDexKeepProguard = file("proguard-rules.pro")
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "4.1.2"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
    buildToolsVersion = "36.1.0"
}


dependencies {
    implementation(project(":android_lib:socket_io_signaling"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}