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

package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.UserData
import io.getstream.feeds.android.client.api.state.UserList
import io.getstream.feeds.android.client.api.state.query.UsersQuery
import io.getstream.feeds.android.client.internal.repository.CommonRepository

/**
 * An implementation of [UserList] that provides methods to query and manage a paginated list of
 * users.
 *
 * @property query The query configuration used for fetching users.
 * @property commonRepository The repository used to perform network requests for users.
 */
internal class UserListImpl(
    override val query: UsersQuery,
    private val commonRepository: CommonRepository,
) : UserList {

    override val state = UserListStateImpl(query)

    override suspend fun get(): Result<List<UserData>> {
        return commonRepository.queryUsers(query).onSuccess(state::onQueryUsers)
    }

    override suspend fun queryMoreUsers(limit: Int?): Result<List<UserData>> {
        if (!state.canLoadMore) {
            return Result.success(emptyList())
        }
        val nextQuery =
            UsersQuery(
                filter = query.filter,
                sort = query.sort,
                limit = limit ?: query.limit,
                offset = state.currentOffset,
                includeDeactivatedUsers = query.includeDeactivatedUsers,
            )
        return queryUsers(nextQuery)
    }

    private suspend fun queryUsers(query: UsersQuery): Result<List<UserData>> {
        return commonRepository.queryUsers(query).onSuccess(state::onQueryMoreUsers)
    }
}
