package tech.yaowen.android.module

import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project

internal fun Project.configureAndroid() = this.extensions.getByType(BaseExtension::class.java).run {
    compileSdkVersion(Versions.COMPILE_SDK)
    defaultConfig {
        minSdk = Versions.MIN_SDK
        targetSdk = Versions.TARGET_SDK
        versionCode = Versions.VERSION_CODE
        versionName = Versions.VERSION_NAME
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }

        getByName("debug") {
            isTestCoverageEnabled = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

//    kotlinOptions {
//        jvmTarget = JavaVersion.VERSION_1_8
//    }

//    buildFeatures {
//        viewBinding = true
//    }
//
//    // Lint 分析任务就可以在各个模块中并行执行，从而显著提升 Lint 任务运行的速度
//    lintOptions {
//        checkDependencies = true
//    }
}