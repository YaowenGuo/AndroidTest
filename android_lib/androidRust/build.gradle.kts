plugins {
    id("com.android.library")
    id("tech.yaowen.android.module")
//    id("org.mozilla.rust-android-gradle.rust-android")
    id("kotlin-android")
    id("kotlin-kapt")
}
android {
    namespace = "tech.yaowen.androidrust"
}


cargo {
    module  = "src/main/test_rust" // Or whatever directory contains your Cargo.toml
    libname = "test_rust"          // Or whatever matches Cargo.toml's [package] name.
    targets = listOf("arm", "arm64", "x86", "x86_64")  // See bellow for a longer list of options

    features {
        all()
    }

    exec = { spec, _ ->
        spec.environment("TEST", "test")
    }
}

afterEvaluate {
    // The `cargoBuild` task isn't available until after evaluation.
    android.libraryVariants.all { variant ->
        var productFlavor = ""
        variant.productFlavors.forEach {
            productFlavor += it.name.capitalize()
        }
        val buildType = variant.buildType.name.capitalize()
        tasks["generate${productFlavor}${buildType}Assets"].dependsOn(tasks["cargoBuild"])
        true
    }
}

