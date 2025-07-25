package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.state.query.BookmarksQuery

/**
 * A class that manages a paginated list of bookmarks.
 *
 * [BookmarkList] provides functionality to query and paginate through bookmarks.
 * It maintains the current state of the bookmark list and provides methods to load more bookmarks
 * when available.
 */
public interface BookmarkList {

    /**
     * The query used to fetch the bookmarks.
     */
    public val query: BookmarksQuery

    /**
     * An observable object representing the current state of the bookmark list.
     */
    public val state: BookmarkListState

    /**
     * Fetches the current list of bookmarks.
     *
     * @return A result containing a list of [BookmarkData] or an error if the operation fails.
     */
    public suspend fun get(): Result<List<BookmarkData>>

    /**
     * Queries more bookmarks based on the current pagination state.
     * If there are no more bookmarks available, it returns an empty list.
     *
     * @param limit The maximum number of bookmarks to return. If null, uses the default limit.
     * @return A result containing a list of [BookmarkData] or an error if the query fails.
     * Returns an empty list if there are no more bookmarks available.
     */
    public suspend fun queryMoreBookmarks(limit: Int? = null): Result<List<BookmarkData>>
}
