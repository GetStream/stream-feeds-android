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

import io.getstream.feeds.android.client.api.model.BookmarkFolderData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.BookmarkFolderListState
import io.getstream.feeds.android.client.api.state.query.BookmarkFoldersQuery
import io.getstream.feeds.android.client.api.state.query.BookmarkFoldersSort
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.state.query.BookmarkFoldersQueryConfig
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * An observable state object that manages the current state of a bookmark folder list.
 *
 * This class maintains the current list of bookmark folders, pagination information, and provides
 * real-time updates when bookmark folders are added, removed, or modified. It automatically handles
 * WebSocket events to keep the bookmark folder list synchronized.
 *
 * @property query The query used to fetch bookmark folders.
 */
internal class BookmarkFolderListStateImpl(override val query: BookmarkFoldersQuery) :
    BookmarkFolderListMutableState {

    private val _folders: MutableStateFlow<List<BookmarkFolderData>> = MutableStateFlow(emptyList())

    internal var queryConfig: BookmarkFoldersQueryConfig? = null
        private set

    private var _pagination: PaginationData? = null

    private val foldersSorting: List<BookmarkFoldersSort>
        get() = query.sort ?: BookmarkFoldersSort.Default

    override val folders: StateFlow<List<BookmarkFolderData>>
        get() = _folders.asStateFlow()

    override val pagination: PaginationData?
        get() = _pagination

    override fun onQueryMoreBookmarkFolders(
        result: PaginationResult<BookmarkFolderData>,
        queryConfig: BookmarkFoldersQueryConfig,
    ) {
        _pagination = result.pagination
        // Update the query configuration for future queries
        this.queryConfig = queryConfig
        // Merge the new folders with the existing ones (keeping the sort order)
        _folders.update { current ->
            current.mergeSorted(result.models, BookmarkFolderData::id, foldersSorting)
        }
    }

    override fun onBookmarkFolderRemoved(folderId: String) {
        _folders.update { current -> current.filter { it.id != folderId } }
    }

    override fun onBookmarkFolderUpdated(folder: BookmarkFolderData) {
        _folders.update { current ->
            current.map {
                if (it.id == folder.id) {
                    // Update the folder data
                    folder
                } else {
                    it
                }
            }
        }
    }
}

/** A mutable state interface for managing the bookmark list state. */
internal interface BookmarkFolderListMutableState :
    BookmarkFolderListState, BookmarkFolderListStateUpdates

/** An interface that defines the methods for updating the state of a bookmark folder list. */
internal interface BookmarkFolderListStateUpdates {

    /**
     * Called when more bookmark folders are queried.
     *
     * @param result The result containing the new bookmark folders and pagination data.
     */
    fun onQueryMoreBookmarkFolders(
        result: PaginationResult<BookmarkFolderData>,
        queryConfig: BookmarkFoldersQueryConfig,
    )

    /**
     * Called when a bookmark folder is removed.
     *
     * @param folderId The ID of the removed bookmark folder.
     */
    fun onBookmarkFolderRemoved(folderId: String)

    /**
     * Called when a bookmark folder is updated.
     *
     * @param folder The updated bookmark folder data.
     */
    fun onBookmarkFolderUpdated(folder: BookmarkFolderData)
}
