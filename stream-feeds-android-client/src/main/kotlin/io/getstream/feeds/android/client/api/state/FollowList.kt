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

package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.state.query.FollowsQuery

/**
 * A class that manages a paginated list of feed members.
 *
 * [FollowList] provides functionality to query and paginate through follows. It maintains the
 * current state of the follow list and provides methods to load more follows when available.
 *
 * ## Example:
 * ```kotlin
 * // Create a follow list with a specific query
 * val query = FollowsQuery()
 * val followList = feedsClient.followList(query)
 *
 * // Fetch initial follows matching the query
 * val follows = followList.get()
 *
 * // Load more follows if available
 * if (followList.state.canLoadMore) {
 *     val moreFollows = followList.queryMoreFollows()
 * }
 *
 * // Observe state changes
 * followList.state.follows.collect { follows ->
 *     println("Updated follows: ${follows.size}")
 * }
 * ```
 */
public interface FollowList {

    /**
     * The query configuration used to fetch follows.
     *
     * This contains the filters, sorting options, and pagination parameters that define which
     * follows are retrieved and how they are ordered.
     */
    public val query: FollowsQuery

    /**
     * An observable object representing the current state of the follow list.
     *
     * This property provides access to the current follows, pagination state, and other state
     * information. The state is automatically updated when new follows are loaded or when real-time
     * updates are received.
     */
    public val state: FollowListState

    /**
     * Fetches the initial list of follows based on the current query configuration.
     *
     * This method loads the first page of follows according to the query's filters, sorting, and
     * limit parameters. The results are stored in the state and can be accessed through the
     * [state.follows] property.
     *
     * @return A [Result] containing a list of [FollowData] if successful, or an error if the
     *   request fails.
     */
    public suspend fun get(): Result<List<FollowData>>

    /**
     * Loads the next page of follows if more are available.
     *
     * This method fetches additional follows using the pagination information from the previous
     * request. If no more follows are available, an empty array is returned.
     *
     * @param limit Optional limit for the number of follows to return. If not specified, the API
     *   will use its default limit.
     * @return A [Result] containing a list of [FollowData] if successful, or an error if the
     *   request fails or there are no more follows to load.
     */
    public suspend fun queryMoreFollows(limit: Int? = null): Result<List<FollowData>>
}
