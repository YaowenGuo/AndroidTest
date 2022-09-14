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
    implementation("org.javassist:javassist:3.29.1-GA'")
}
