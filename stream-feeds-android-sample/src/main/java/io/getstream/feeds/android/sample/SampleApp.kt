/*
 * Copyright (c) 2014-2026 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-feeds-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.getstream.feeds.android.sample

import android.app.Application
import android.os.StrictMode
import coil3.SingletonImageLoader
import dagger.hilt.android.HiltAndroidApp
import io.getstream.feeds.android.sample.ui.CoilImageLoaderFactory

@HiltAndroidApp
class SampleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SingletonImageLoader.setSafe(CoilImageLoaderFactory())

        val threadPolicy = StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build()
        val vmPolicy = StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build()
        StrictMode.setThreadPolicy(threadPolicy)
        StrictMode.setVmPolicy(vmPolicy)
    }
}
