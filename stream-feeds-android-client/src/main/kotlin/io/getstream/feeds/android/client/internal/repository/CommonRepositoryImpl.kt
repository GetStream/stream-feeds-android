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

import io.getstream.android.core.result.runSafely
import io.getstream.feeds.android.client.api.model.AppData
import io.getstream.feeds.android.client.api.model.UserData
import io.getstream.feeds.android.client.api.state.query.UsersQuery
import io.getstream.feeds.android.client.internal.model.toModel
import io.getstream.feeds.android.client.internal.state.query.toRequest
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.FullUserResponse
import io.getstream.feeds.android.network.models.GetOGResponse

/**
 * Default implementation of [CommonRepository].
 *
 * @property api The API service used to execute requests.
 */
internal class CommonRepositoryImpl(private val api: FeedsApi) : CommonRepository {

    private var cachedAppData: AppData? = null

    override suspend fun getApp(): Result<AppData> = runSafely {
        cachedAppData?.let {
            return Result.success(it)
        }
        api.getApp().app.toModel().also { cachedAppData = it }
    }

    override suspend fun getOG(url: String): Result<GetOGResponse> = runSafely { api.getOG(url) }

    override suspend fun queryUsers(query: UsersQuery): Result<List<UserData>> = runSafely {
        api.queryUsers(query.toRequest()).users.map(FullUserResponse::toModel)
    }
}
