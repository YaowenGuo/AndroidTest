apply(plugin = "java-library")
apply(plugin = "kotlin")
plugins {
    id("java-library")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "$buildDir/native-libs", "include" to listOf("native-libs.jar"))))
    implementation(libs.kotlin.std)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val nativeLibsToJar by tasks.creating(Jar::class.java) {
    destinationDir = file("$buildDir/native-libs")
    baseName = "native-libs"
    from(fileTree(mapOf("dir" to "$buildDir/native-libs", "include" to listOf("**/*.so"))))
    into("lib/")
}

tasks.withType<JavaCompile> {
    dependsOn(nativeLibsToJar)
}