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

import io.getstream.feeds.android.client.api.model.BookmarkFolderData
import io.getstream.feeds.android.client.api.state.query.BookmarkFoldersQuery

/**
 * A class that manages a paginated list of bookmark folders.
 *
 * [BookmarkFolderList] provides functionality to query and paginate through bookmark folders. It
 * maintains the current st ate of the bookmark folder list and provides methods to load more
 * folders when available.
 *
 * ## Example:
 * ```kotlin
 * val query = BookmarkFoldersQuery()
 * val bookmarkFolderList = feedsClient.bookmarkFolderList(query)
 *
 * // Fetch initial bookmark folders matching the query
 * val bookmarkFolders = bookmarkFolderList.get()
 *
 * // Load more bookmark folders if available
 * if (bookmarkFolderList.state.canLoadMore) {
 *     val moreBookmarkFolders = bookmarkFolderList.queryMoreBookmarkFolders()
 * }
 *
 * // Observe state changes
 * bookmarkFolderList.state.folders.collect { folders ->
 *     println("Updated bookmark folders: ${folders.size}")
 * }
 * ```
 */
public interface BookmarkFolderList {

    /** The query used to fetch the bookmark folders. */
    public val query: BookmarkFoldersQuery

    /** An observable object representing the current state of the bookmark list. */
    public val state: BookmarkFolderListState

    /**
     * Retrieves the first page of bookmark folders.
     *
     * @return A [Result] containing a list of [BookmarkFolderData] or an error if the operation
     *   fails.
     */
    public suspend fun get(): Result<List<BookmarkFolderData>>

    /**
     * Retrieves the next page of bookmark folders based on the current pagination state. If there
     * are no more pages available, it returns an empty list.
     *
     * @param limit The maximum number of bookmark folders to retrieve. If null, uses the default
     *   limit.
     * @return A [Result] containing a list of [BookmarkFolderData] or an error if the operation
     *   fails. Returns an empty list if there are no more pages available.
     */
    public suspend fun queryMoreBookmarkFolders(
        limit: Int? = null
    ): Result<List<BookmarkFolderData>>
}
