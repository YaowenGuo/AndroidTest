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
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
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
    ":app:recycler",
    ":app:test_rust_jni",
    ":app:opengles3",
    ":app:opengles3_native",
    ":app:lifecycle",
    ":app:app",
    ":app:provider",
    ":app:media",
    ":app:media_player",
    ":app:test_glide",
    ":app:arm",

    ":android_lib:media_tool",
    ":android_lib:androidRust",
    ":android_lib:ffmpeg",
    ":android_lib:lib_share",
    ":android_lib:my_butter_knife",
    ":android_lib:media_tool",
    ":android_lib:socket_io_signaling",
    ":android_lib:theme",

    ":lib:test_rust",
    ":lib:test_reflection",
    ":lib:test_annotation",
    ":lib:lib_butter_knife",
    ":lib:TestIO",
    ":lib:TestRxJava",
    ":lib:algorithm",
    ":lib:test_OKHttp",
    ":lib:test_retrofit",
    ":lib:lib_processor",
    ":lib:lib_annotations",
    ":lib:test_java",
    ":lib:tools"
)

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
includeBuild("VersionPlugin")
include(":android_lib:device")
