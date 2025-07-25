package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.BookmarkFolderData
import io.getstream.feeds.android.client.api.state.query.BookmarkFoldersQuery

/**
 * A class that manages a paginated list of bookmark folders.
 *
 * [BookmarkFolderList] provides functionality to query and paginate through bookmark folders.
 * It maintains the current state of the bookmark folder list and provides methods to load more
 * folders when available.
 */
public interface BookmarkFolderList {

    /**
     * The query used to fetch the bookmark folders.
     */
    public val query: BookmarkFoldersQuery

    /**
     * An observable object representing the current state of the bookmark list.
     */
    public val state: BookmarkFolderListState

    /**
     * Retrieves the first page of bookmark folders.
     *
     * @return A [Result] containing a list of [BookmarkFolderData] or an error if the
     * operation fails.
     */
    public suspend fun get(): Result<List<BookmarkFolderData>>

    /**
     * Retrieves the next page of bookmark folders based on the current pagination state.
     * If there are no more pages available, it returns an empty list.
     *
     * @param limit The maximum number of bookmark folders to retrieve.
     * If null, uses the default limit.
     * @return A [Result] containing a list of [BookmarkFolderData] or an error if the
     * operation fails. Returns an empty list if there are no more pages available.
     */
    public suspend fun queryMoreBookmarkFolders(limit: Int? = null): Result<List<BookmarkFolderData>>
}