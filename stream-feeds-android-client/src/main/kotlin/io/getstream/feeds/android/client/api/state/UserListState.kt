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

package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.UserData
import io.getstream.feeds.android.client.api.state.query.UsersQuery
import kotlinx.coroutines.flow.StateFlow

/**
 * An observable state object that manages the users list.
 *
 * [UserListState] provides a reactive interface for observing changes to the users list, including
 * pagination state. It automatically handles sorting and merging users as more pages are loaded.
 *
 * Example usage:
 * ```kotlin
 * val userList = client.userList(query)
 * userList.state.users.collectLatest { users ->
 *   // Update UI with new users
 * }
 * ```
 */
public interface UserListState {

    /**
     * The query configuration used for fetching users.
     *
     * This property contains the filtering, sorting, and pagination parameters that define how
     * users should be fetched and displayed.
     */
    public val query: UsersQuery

    /**
     * All the paginated users in the current list.
     *
     * This property contains all users that have been fetched and merged, maintaining the proper
     * sort order. It automatically updates when new users are loaded.
     */
    public val users: StateFlow<List<UserData>>

    /** Indicates whether there are more users available to load. */
    public val canLoadMore: Boolean
}
