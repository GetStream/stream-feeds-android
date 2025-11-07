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

import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.state.query.PollsQuery

/**
 * A list of polls that can be queried and paginated.
 *
 * This class provides a way to fetch and manage a collection of polls with support for filtering,
 * sorting, and pagination. It maintains an observable state that can be used in UI components.
 *
 * ## Example Usage:
 * ```kotlin
 * // Create a poll list with a specific query
 * val query = PollsQuery()
 * val pollList = feedsClient.pollList(query)
 *
 * // Fetch initial polls matching the query
 * val polls = pollList.get()
 *
 * // Load more polls if available
 * if (pollList.state.canLoadMore) {
 *    val morePolls = pollList.queryMorePolls()
 * }
 *
 * // Observe state changes
 * pollList.state.polls.collect { polls ->
 *    println("Updated polls: ${polls.size}")
 * }
 * ```
 */
public interface PollList {

    /** The query used to fetch the polls. */
    public val query: PollsQuery

    /** An observable object representing the current state of the poll list. */
    public val state: PollListState

    /**
     * Fetches the initial list of polls based on the current query.
     *
     * This method retrieves the first page of polls matching the query criteria. The results are
     * automatically stored in the state and can be accessed via the [state.polls] property.
     *
     * @return A [Result] containing a list of [PollData] if successful, or an error if the request
     *   fails.
     */
    public suspend fun get(): Result<List<PollData>>

    /**
     * Loads more polls using the next page token from the previous query.
     *
     * This method fetches additional polls if there are more available based on the current query.
     * The new polls are automatically merged with the existing ones in the state.
     *
     * @param limit Optional limit for the number of polls to fetch. If not provided, the default
     *   limit will be used.
     * @return A [Result] containing a list of [PollData] if successful, or an error if the request
     *   fails. Returns an empty list if there are no more polls to fetch.
     */
    public suspend fun queryMorePolls(limit: Int? = null): Result<List<PollData>>
}
