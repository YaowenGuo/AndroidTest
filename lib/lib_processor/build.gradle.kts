apply(plugin = "java-library")
apply(plugin = "kotlin")
plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(project(":lib:lib_annotations"))
    implementation(libs.squareup.javapoet)
}
