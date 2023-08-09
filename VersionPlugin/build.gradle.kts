plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

dependencies {
    implementation(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
    implementation("com.android.tools.build:gradle-api:8.0.2")
    implementation("com.android.tools.build:gradle:8.0.2")
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