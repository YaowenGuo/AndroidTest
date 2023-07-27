plugins {
  application
  kotlin("jvm")
}

dependencies { testImplementation(kotlin("test")) }

application {
  applicationDefaultJvmArgs =
      listOf(
          "-Djava.library.path=${file("${project(":lib:jni_native").buildDir}/lib/main/debug").absolutePath}")
}

tasks.getByName("classes").dependsOn(":lib:jni_native:linkDebug")

tasks.test {
  useJUnitPlatform()
  systemProperties =
      mapOf(
          "java.library.path" to
              file("${project(":lib:jni_native").buildDir}/lib/main/debug").absolutePath)
}
