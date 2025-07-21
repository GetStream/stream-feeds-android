package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.model.PaginationData
import kotlinx.coroutines.flow.StateFlow

public interface BookmarkListState {

    /**
     * The query used to fetch the bookmarks.
     */
    public val query: BookmarksQuery

    /**
     * All the paginated bookmarks.
     */
    public val bookmarks: StateFlow<List<BookmarkData>>

    /**
     * Last pagination information.
     */
    public val pagination: PaginationData?

    /**
     * Indicates whether there are more bookmarks available to load.
     */
    public val canLoadMore: Boolean
        get() = pagination?.next != null
}
