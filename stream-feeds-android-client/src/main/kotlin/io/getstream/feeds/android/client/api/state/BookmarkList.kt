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

import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.state.query.BookmarksQuery

/**
 * A class that manages a paginated list of bookmarks.
 *
 * [BookmarkList] provides functionality to query and paginate through bookmarks. It maintains the
 * current state of the bookmark list and provides methods to load more bookmarks when available.
 *
 * ## Example:
 * ```kotlin
 * val query = BookmarksQuery()
 * val bookmarkList = feedsClient.bookmarkList(query)
 *
 * // Fetch initial bookmarks matching the query
 * val bookmarks = bookmarkList.get()
 *
 * // Load more bookmarks if available
 * if (bookmarkList.state.canLoadMore) {
 *     val moreBookmarks = bookmarkList.queryMoreBookmarks()
 * }
 *
 * // Observe state changes
 * bookmarkList.state.bookmarks.collect { bookmarks ->
 *     println("Updated bookmarks: ${bookmarks.size}")
 * }
 * ```
 */
public interface BookmarkList {

    /** The query used to fetch the bookmarks. */
    public val query: BookmarksQuery

    /** An observable object representing the current state of the bookmark list. */
    public val state: BookmarkListState

    /**
     * Fetches the current list of bookmarks.
     *
     * @return A result containing a list of [BookmarkData] or an error if the operation fails.
     */
    public suspend fun get(): Result<List<BookmarkData>>

    /**
     * Queries more bookmarks based on the current pagination state. If there are no more bookmarks
     * available, it returns an empty list.
     *
     * @param limit The maximum number of bookmarks to return. If null, uses the default limit.
     * @return A result containing a list of [BookmarkData] or an error if the query fails. Returns
     *   an empty list if there are no more bookmarks available.
     */
    public suspend fun queryMoreBookmarks(limit: Int? = null): Result<List<BookmarkData>>
}
