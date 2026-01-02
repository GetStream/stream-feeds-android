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

import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.state.query.FeedsQuery

/**
 * Represents a list of feeds with a query and state.
 *
 * ## Example:
 * ```kotlin
 * // Create a feed list
 * val query = FeedsQuery()
 * val feedList = feedsClient.feedList(query)
 *
 * // Fetch initial feeds matching the query
 * val feeds = feedList.get()
 *
 * // Load more feeds if available
 * if (feedList.state.canLoadMore) {
 *     val moreFeeds = feedList.queryMoreFeeds()
 * }
 *
 * // Observe state changes
 * feedList.state.feeds.collect { feeds ->
 *    println("Updated feeds: ${feeds.size}")
 * }
 * ```
 */
public interface FeedList {

    /** The query used to fetch feeds. */
    public val query: FeedsQuery

    /** An observable object representing the current state of the feed list. */
    public val state: FeedListState

    /**
     * Fetches the initial list of feeds based on the current query configuration. This method loads
     * the first page of feeds according to the query's filters, sorting, and limit parameters.
     *
     * @return A [Result] containing a list of [FeedData] if successful, or an error if the request
     *   fails.
     */
    public suspend fun get(): Result<List<FeedData>>

    /**
     * Loads the next page of feeds if more are available.
     *
     * This method fetches additional feeds using the pagination information from the previous
     * request. If no more feeds are available, an empty array is returned.
     *
     * @return A [Result] containing a list of [FeedData] if successful, or an error if the request
     *   fails or there are no more feeds to load.
     */
    public suspend fun queryMoreFeeds(limit: Int? = null): Result<List<FeedData>>
}
