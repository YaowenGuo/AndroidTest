apply(plugin = "java-library")
apply(plugin = "kotlin")
plugins {
    id("java-library")
}


dependencies {
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.okhttp)
    implementation(libs.rxjava.kotlin)
    implementation(libs.retrofit.rx.adapter)
    implementation(libs.retrofit.json)
    implementation(libs.squareup.mockwebserver)
}
