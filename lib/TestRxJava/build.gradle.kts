plugins {
    id("java-library")
    id("kotlin")
}

dependencies {
    implementation(libs.rxjava.kotlin)
    implementation(libs.kotlin.coroutines)
}