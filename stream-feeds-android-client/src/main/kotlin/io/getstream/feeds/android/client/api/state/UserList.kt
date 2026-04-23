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

/**
 * A paginated list of users that supports filtering and sorting.
 *
 * `UserList` provides a convenient way to fetch, paginate, and observe users. It manages the state
 * of users and provides methods for loading more users as needed.
 *
 * ## Example:
 * ```kotlin
 * val query = UsersQuery(
 *     filter = UsersFilterField.role.equal("admin"),
 *     sort = listOf(UsersSort(UsersSortField.CreatedAt, SortDirection.REVERSE)),
 *     limit = 10,
 * )
 * val userList = feedsClient.userList(query)
 *
 * // Fetch initial users matching the query
 * val users = userList.get()
 *
 * // Load more users if available
 * if (userList.state.canLoadMore) {
 *     val moreUsers = userList.queryMoreUsers()
 * }
 *
 * // Observe state changes
 * userList.state.users.collect { users ->
 *     println("Updated users: ${users.size}")
 * }
 * ```
 */
public interface UserList {

    /**
     * The query configuration used for fetching users.
     *
     * This property contains the filtering, sorting, and pagination parameters that define how
     * users should be fetched and displayed.
     */
    public val query: UsersQuery

    /** An observable object representing the current state of the user list. */
    public val state: UserListState

    /**
     * Fetches the initial set of users based on the current query configuration.
     *
     * This method retrieves the first page of users using the filtering and sorting parameters
     * defined in the query. The results are automatically stored in the state and can be observed
     * through the [state.users] property.
     *
     * @return A [Result] containing a list of [UserData] if the fetch is successful, or an error if
     *   the fetch fails.
     */
    public suspend fun get(): Result<List<UserData>>

    /**
     * Fetches the next page of users if available.
     *
     * This method retrieves additional users by advancing the offset from the previous request. The
     * new users are automatically merged with the existing users in the state, maintaining the
     * proper sort order.
     *
     * @param limit Optional limit for the number of users to fetch. If not specified, the default
     *   limit from the query will be used.
     * @return A [Result] containing a list of [UserData] if the fetch is successful, or an error if
     *   the fetch fails. Returns an empty list if there are no more users to fetch.
     */
    public suspend fun queryMoreUsers(limit: Int? = null): Result<List<UserData>>
}
