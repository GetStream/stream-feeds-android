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

import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.model.BookmarkFolderData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.BookmarkListState
import io.getstream.feeds.android.client.api.state.query.BookmarksQuery
import io.getstream.feeds.android.client.api.state.query.BookmarksSort
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.state.query.BookmarksQueryConfig
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import io.getstream.feeds.android.client.internal.utils.upsertSorted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * An observable state object that manages the current state of a bookmark list.
 *
 * This class maintains the current list of bookmarks, pagination information, and provides
 * real-time updates when bookmarks or bookmark folders are added, removed, or modified. It
 * automatically handles WebSocket events to keep the bookmark list synchronized.
 *
 * @property query The query used to fetch bookmarks.
 */
internal class BookmarkListStateImpl(override val query: BookmarksQuery) :
    BookmarkListMutableState {

    private val _bookmarks: MutableStateFlow<List<BookmarkData>> = MutableStateFlow(emptyList())

    internal var queryConfig: BookmarksQueryConfig? = null
        private set

    private var _pagination: PaginationData? = null

    private val bookmarksSorting: List<BookmarksSort>
        get() = query.sort ?: BookmarksSort.Default

    override val bookmarks: StateFlow<List<BookmarkData>>
        get() = _bookmarks.asStateFlow()

    override val pagination: PaginationData?
        get() = _pagination

    override fun onQueryMoreBookmarks(
        result: PaginationResult<BookmarkData>,
        queryConfig: BookmarksQueryConfig,
    ) {
        _pagination = result.pagination
        // Update the query configuration for future queries
        this.queryConfig = queryConfig
        // Merge the new bookmarks with the existing ones (keeping the sort order)
        _bookmarks.update { current ->
            current.mergeSorted(result.models, BookmarkData::id, bookmarksSorting)
        }
    }

    override fun onBookmarkFolderRemoved(folderId: String) {
        _bookmarks.update { current ->
            current.map {
                if (it.folder?.id == folderId) {
                    // Remove the folder reference from the bookmark
                    it.copy(folder = null)
                } else {
                    it
                }
            }
        }
    }

    override fun onBookmarkFolderUpdated(folder: BookmarkFolderData) {
        _bookmarks.update { current ->
            current.map { bookmark ->
                if (bookmark.folder?.id == folder.id) {
                    // Update the folder reference in the bookmark
                    bookmark.copy(folder = folder)
                } else {
                    bookmark
                }
            }
        }
    }

    override fun onBookmarkRemoved(bookmark: BookmarkData) {
        _bookmarks.update { current -> current.filter { it.id != bookmark.id } }
    }

    override fun onBookmarkUpserted(bookmark: BookmarkData) {
        _bookmarks.update { current ->
            current.upsertSorted(bookmark, BookmarkData::id, bookmarksSorting)
        }
    }
}

/**
 * A mutable state interface for managing the bookmark list state.
 *
 * This interface combines the [BookmarkListState] for read access and [BookmarkListStateUpdates]
 * for write access, allowing for both querying and updating the bookmark list state.
 */
internal interface BookmarkListMutableState : BookmarkListState, BookmarkListStateUpdates

/** An interface that defines the methods for updating the state of a bookmark list. */
internal interface BookmarkListStateUpdates {

    fun onQueryMoreBookmarks(
        result: PaginationResult<BookmarkData>,
        queryConfig: BookmarksQueryConfig,
    )

    fun onBookmarkFolderRemoved(folderId: String)

    fun onBookmarkFolderUpdated(folder: BookmarkFolderData)

    fun onBookmarkRemoved(bookmark: BookmarkData)

    fun onBookmarkUpserted(bookmark: BookmarkData)
}
