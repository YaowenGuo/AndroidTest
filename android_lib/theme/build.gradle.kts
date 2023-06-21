plugins {
    id("com.android.library")
    id("tech.yaowen.android.module")
}

dependencies {
    api(libs.android.material)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.googlefonts)

    api(libs.androidx.compose.runtime)//specify the version
}
android {
    namespace = "tech.yaowen.theme"
}
