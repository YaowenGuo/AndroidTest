apply(plugin = "java-library")
apply(plugin = "kotlin")
plugins {
    id("java-library")
}

dependencies {
    implementation(libs.rxjava.kotlin)
    implementation(libs.kotlin.coroutines)
}