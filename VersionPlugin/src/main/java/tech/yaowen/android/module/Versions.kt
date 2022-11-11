package tech.yaowen.android.module/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

object Versions {
    const val COMPILE_SDK = 32
    const val TARGET_SDK = 32
    //camera2ndk only can be use above android api 24
    const val MIN_SDK = 24

    val VERSION_NAME = "1.0.15" // X.Y.Z; X = Major, Y = minor, Z = Patch level
    val VERSION_CODE = 1

    const val ANDROID_GRADLE_PLUGIN = "7.2.0"
    const val BENCHMARK = "1.1.0-rc02"
    const val COMPOSE = "1.1.1"
    const val FIREBASE_CRASHLYTICS = "2.3.0"
    const val GOOGLE_SERVICES = "4.3.3"
    const val HILT_AGP = "2.40.5"
    const val KOTLIN = "1.6.10"
    const val NAVIGATION = "2.4.1"
    const val PROFILE_INSTALLER = "1.2.0-beta01"

    // TODO: Remove this once the version for
    //  "org.threeten:threetenbp:${tech.yaowen.android.module.Versions.threetenbp}:no-tzdb" using java-platform in the
    //  depconstraints/build.gradle.kts is defined
    const val THREETENBP = "1.3.6"
    const val CMAKE_VERSION = "3.18.1"
}
