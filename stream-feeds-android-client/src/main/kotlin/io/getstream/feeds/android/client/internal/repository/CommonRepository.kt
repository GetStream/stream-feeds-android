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

package io.getstream.feeds.android.client.internal.repository

import io.getstream.feeds.android.client.api.model.AppData
import io.getstream.feeds.android.client.api.model.UserData
import io.getstream.feeds.android.client.api.state.query.UsersQuery
import io.getstream.feeds.android.network.models.GetOGResponse

/**
 * A repository for common operations that are not specific to a single domain (feeds, comments,
 * polls, etc.). This includes app configuration, Open Graph metadata, and user queries.
 */
internal interface CommonRepository {

    /**
     * Fetches the application configuration data.
     *
     * @return A [Result] containing the [AppData] if successful, or an error if the request fails.
     */
    suspend fun getApp(): Result<AppData>

    /**
     * Fetches Open Graph metadata for a URL.
     *
     * @param url The URL to fetch OG metadata for.
     * @return A [Result] containing the [GetOGResponse] if successful, or an error if the request
     *   fails.
     */
    suspend fun getOG(url: String): Result<GetOGResponse>

    /**
     * Queries users with the given query.
     *
     * @param query The query parameters for filtering and sorting users.
     * @return A [Result] containing a list of [UserData] if successful, or an error if the request
     *   fails.
     */
    suspend fun queryUsers(query: UsersQuery): Result<List<UserData>>
}
