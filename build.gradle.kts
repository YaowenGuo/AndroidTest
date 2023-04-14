plugins {
    id("com.android.application") version "7.2.2" apply false
    id("com.android.library") version "7.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.7.10" apply false
    id("org.mozilla.rust-android-gradle.rust-android") version "0.9.3" apply false
    id("androidx.navigation.safeargs") version "2.4.1" apply false
}

val clean by tasks.creating(Delete::class) {
    delete(rootProject.buildDir)
}