apply(plugin = "java-library")
apply(plugin = "kotlin")
plugins {
    id("java-library")
}

dependencies {
    implementation(libs.squareup.okio)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}