package tech.yaowen.android.module

import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidAppModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.configureAndroid()
    }
}
