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
import io.getstream.feeds.android.client.api.state.UserListState
import io.getstream.feeds.android.client.api.state.query.UsersQuery
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * An observable state object that manages the current state of a user list.
 *
 * This class maintains the current list of users and offset-based pagination information.
 */
internal class UserListStateImpl(override val query: UsersQuery) : UserListMutableState {

    private val _users: MutableStateFlow<List<UserData>> = MutableStateFlow(emptyList())

    override val users: StateFlow<List<UserData>>
        get() = _users.asStateFlow()

    override var canLoadMore: Boolean = true

    internal var currentOffset: Int = query.offset ?: 0
        private set

    override fun onQueryUsers(users: List<UserData>, replace: Boolean) {
        canLoadMore = users.isNotEmpty()
        if (replace) {
            currentOffset = (query.offset ?: 0) + users.size
            _users.update { users }
        } else {
            currentOffset += users.size
            _users.update { current ->
                current.mergeSorted(users, UserData::id, query.sort.orEmpty())
            }
        }
    }
}

internal interface UserListMutableState : UserListState, UserListStateUpdates

internal interface UserListStateUpdates {
    fun onQueryUsers(users: List<UserData>, replace: Boolean = false)
}
