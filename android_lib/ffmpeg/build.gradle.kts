plugins {
    id("com.android.library")
    id("tech.yaowen.android.module")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    defaultConfig {
        /*ndk {
            moduleName "ffmpeg"  // 指定连接时 c 动态链接库的名字
            // 指定链接哪些架构的库，会根据用于不同架构的apk链接不同的类型
            abiFilters "armeabi-v7a","arm64-v8a", "x86", "x86_64"
        }
        sourceSets.main {
            jni.srcDirs = []
            // 指定so库的位置，用于生成apk时链接时进apk，这里指定了两个。
            jniLibs.srcDirs = ["src/main/jniLibs", "src/main/libs"]

        }*/

        externalNativeBuild() {
            cmake {
                abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64") //指定编译为 armeabi-v7a
            }
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.10.2"
        }
    }
}