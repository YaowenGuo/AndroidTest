plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
//    id("kotlin-android")
//    id("kotlin-kapt")
}

buildscript {
    repositories {
        google()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://maven.aliyun.com/nexus/content/groups/public/") }
        maven { setUrl("https://maven.aliyun.com/nexus/content/repositories/jcenter") }
        gradlePluginPortal()
        mavenCentral()
    }

    dependencies {
        // 因为使用的 Kotlin 需要需要添加 Kotlin 插件,需要和主工程对应，不然就出现两个版本了
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
    }
}

repositories {
    google()
    maven (url = "https://jitpack.io")
    maven { setUrl("https://maven.aliyun.com/nexus/content/repositories/releases/") }
    maven { setUrl("https://maven.aliyun.com/nexus/content/groups/public/") }
    maven { setUrl("https://maven.aliyun.com/nexus/content/repositories/jcenter") }
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
    implementation("com.android.tools.build:gradle-api:7.2.2")
    implementation("com.android.tools.build:gradle:7.2.2")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

val pluginId = "tech.yaowen.android.module"

gradlePlugin {
    plugins {
        create(pluginId) {
            // 在 app 模块需要通过 id 引用这个插件
            id = pluginId
            // 实现这个插件的类的路径
            implementationClass = "tech.yaowen.android.module.AndroidAppModulePlugin"
        }
    }
}

//buildscript {
//    repositories {
//        jcenter()
//    }
//    dependencies {
//        /* Example Dependency */
//        /* Depend on the android gradle plugin, since we want to access it in our plugin */
//        implementation("com.android.tools.build:gradle-api:7.2.2")
//        implementation("com.android.tools.build:gradle:7.2.2")
//
//        /* Example Dependency */
//        /* Depend on the kotlin plugin, since we want to access it in our plugin */
//        implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0")
//        implementation("androidx.navigation:navigation-safe-args-gradle-plugin:2.4.1")
//        /* Depend on the default Gradle API's since we want to build a custom plugin */
//        implementation(gradleApi())
//        implementation(localGroovy())
//    }
//}
//
//apply plugin : 'kotlin'
//apply plugin : 'java-gradle-plugin'
//repositories {
//    // 需要添加 jcenter 否则会提示找不到 gradlePlugin
//    jcenter()
//}
//
//gradlePlugin {
//    plugins {
//        version {
//            // 在 app 模块需要通过 id 引用这个插件
//            id = 'com.hi.dhl.plugin'
//            // 实现这个插件的类的路径
//            implementationClass = 'com.hi.dhl.plugin.Deps'
//        }
//    }
//}
//
//plugins {
//    id("java-library")
//    id("org.jetbrains.kotlin.jvm")
//    `kotlin-dsl`
//}
//
//repositories {
//    gradlePluginPortal()
//    mavenCentral()
//    google()
//}
//
//dependencies {
//    /* Example Dependency */
//    /* Depend on the android gradle plugin, since we want to access it in our plugin */
//    implementation("com.android.tools.build:gradle-api:7.2.2")
//    implementation("com.android.tools.build:gradle:7.2.2")
//
//    /* Example Dependency */
//    /* Depend on the kotlin plugin, since we want to access it in our plugin */
//    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0")
//    implementation("androidx.navigation:navigation-safe-args-gradle-plugin:2.4.1")
//    /* Depend on the default Gradle API's since we want to build a custom plugin */
//    implementation(gradleApi())
//    implementation(localGroovy())
//}
