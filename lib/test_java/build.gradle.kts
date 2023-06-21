apply(plugin = "java-library")
apply(plugin = "kotlin")
plugins {
    id("java-library")
}

dependencies {
    implementation("org.javassist:javassist:3.29.1-GA")
}