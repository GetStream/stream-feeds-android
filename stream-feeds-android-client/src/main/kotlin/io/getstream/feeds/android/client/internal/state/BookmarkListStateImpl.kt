package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.model.BookmarkFolderData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.state.BookmarkListState
import io.getstream.feeds.android.client.api.state.BookmarksQuery
import io.getstream.feeds.android.client.api.state.BookmarksSort
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * An observable state object that manages the current state of a bookmark list.
 *
 * This class maintains the current list of bookmarks, pagination information, and provides
 * real-time updates when bookmarks or bookmark folders are added, removed, or modified.
 * It automatically handles WebSocket events to keep the bookmark list synchronized.
 */
internal class BookmarkListStateImpl(
    override val query: BookmarksQuery,
): BookmarkListMutableState {

    private val _bookmarks: MutableStateFlow<List<BookmarkData>> = MutableStateFlow(emptyList())

    internal var queryConfig : QueryConfiguration<BookmarksSort>? = null
        private set

    private var _pagination: PaginationData? = null

    private val bookmarksSorting: List<BookmarksSort>
        get() = query.sort ?: BookmarksSort.Default

    override val bookmarks: StateFlow<List<BookmarkData>>
        get() = _bookmarks

    override val pagination: PaginationData?
        get() = _pagination

    override fun onQueryMoreMoreBookmarks(
        result: PaginationResult<BookmarkData>,
        queryConfig: QueryConfiguration<BookmarksSort>
    ) {
        _pagination = result.pagination
        // Update the query configuration for future queries
        this.queryConfig = queryConfig
        // Merge the new bookmarks with the existing ones (keeping the sort order)
        _bookmarks.value =
            _bookmarks.value.mergeSorted(result.models, BookmarkData::id, bookmarksSorting)
    }

    override fun onBookmarkFolderRemoved(folderId: String) {
        _bookmarks.value = _bookmarks.value.map {
            if (it.folder?.id == folderId) {
                // Remove the folder reference from the bookmark
                it.copy(folder = null)
            } else {
                it
            }
        }
    }

    override fun onBookmarkFolderUpdated(folder: BookmarkFolderData) {
        _bookmarks.value = _bookmarks.value.map { bookmark ->
            if (bookmark.folder?.id == folder.id) {
                // Update the folder reference in the bookmark
                bookmark.copy(folder = folder)
            } else {
                bookmark
            }
        }
    }

    override fun onBookmarkUpdated(bookmark: BookmarkData) {
        _bookmarks.value = _bookmarks.value.map {
            if (it.id == bookmark.id) {
                // Update the bookmark with the new data
                bookmark
            } else {
                it
            }
        }
    }
}

/**
 * A mutable state interface for managing the bookmark list state.
 *
 * This interface combines the [BookmarkListState] for read access and [BookmarkListStateUpdates]
 * for write access, allowing for both querying and updating the bookmark list state.
 */
internal interface BookmarkListMutableState: BookmarkListState, BookmarkListStateUpdates

/**
 * Interface for handling updates to the bookmark list state.
 */
internal interface BookmarkListStateUpdates {

    fun onQueryMoreMoreBookmarks(
        result: PaginationResult<BookmarkData>,
        queryConfig: QueryConfiguration<BookmarksSort>
    )

    fun onBookmarkFolderRemoved(folderId: String)

    fun onBookmarkFolderUpdated(folder: BookmarkFolderData)

    fun onBookmarkUpdated(bookmark: BookmarkData)
}