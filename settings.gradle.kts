pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        maven {
            setUrl("https://plugins.gradle.org/m2/")
        }
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven {
            setUrl("https://plugins.gradle.org/m2/")
        }
    }
}


include(
    ":app:rtc_demo",
    ":app:rtc_native",
//    ":app:test_rust_jni",
    ":app:opengles3",
    ":app:opengles3_native",
    ":app:app",
    ":app:media",
    ":app:test_glide",

    ":android_lib:media_tool",
//    ":android_lib:androidRust",
    ":android_lib:ffmpeg",
    ":android_lib:lib_share",
    ":android_lib:media_tool",
    ":android_lib:socket_io_signaling",
    ":android_lib:theme",

    ":lib:test_rust",
    ":lib:TestIO",
    ":lib:TestRxJava",
    ":lib:algorithm",
    ":lib:test_OKHttp",
    ":lib:test_retrofit",
    ":lib:lib_annotations",
    ":lib:test_java",
    ":lib:tools",
    ":lib:test_jni",
    ":lib:jni_native"
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
includeBuild("VersionPlugin")