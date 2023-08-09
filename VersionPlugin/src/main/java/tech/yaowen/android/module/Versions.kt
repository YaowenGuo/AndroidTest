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
    const val COMPILE_SDK = 34
    const val TARGET_SDK = 34
    //camera2ndk only can be use above android api 24
    const val MIN_SDK = 24

    val VERSION_NAME = "1.0.15" // X.Y.Z; X = Major, Y = minor, Z = Patch level
    val VERSION_CODE = 1
    const val CMAKE_VERSION = "3.22.1"
}
