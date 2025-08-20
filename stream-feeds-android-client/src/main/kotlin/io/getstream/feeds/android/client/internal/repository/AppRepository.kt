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

import io.getstream.feeds.android.client.api.model.AppData

/**
 * An interface for a repository that provides application configuration data.
 *
 * This repository is responsible for fetching the application configuration data, which includes
 * settings like URL enrichment, translation, and file upload configurations.
 */
internal interface AppRepository {

    /**
     * Fetches the application configuration data.
     *
     * @return A [Result] containing the [AppData] if successful, or an error if the request fails.
     */
    suspend fun getApp(): Result<AppData>
}
