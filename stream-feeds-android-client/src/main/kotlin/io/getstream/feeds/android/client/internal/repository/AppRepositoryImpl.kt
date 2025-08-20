/*
 * Copyright (c) 2014-2025 Stream.io Inc. All rights reserved.
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
package io.getstream.feeds.android.client.internal.repository

import io.getstream.android.core.result.runSafely
import io.getstream.feeds.android.client.api.model.AppData
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.core.generated.apis.ApiService

/**
 * Default implementation of the [AppRepository]. Uses the [ApiService] to fetch application
 * configuration data. Caches the result to avoid multiple network calls for the same data.
 *
 * @property api The API service used to fetch application data.
 */
internal class AppRepositoryImpl(private val api: ApiService) : AppRepository {

    private var cachedAppData: AppData? = null

    override suspend fun getApp(): Result<AppData> = runSafely {
        cachedAppData?.let {
            return Result.success(it)
        }
        api.getApp().app.toModel().also { cachedAppData = it }
    }
}
