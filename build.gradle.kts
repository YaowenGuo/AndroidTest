plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.androidx.navigation) apply false

//    id("org.mozilla.rust-android-gradle.rust-android") version "0.9.3" apply false
}

val clean by tasks.creating(Delete::class) {
    delete(rootProject.buildDir)
}