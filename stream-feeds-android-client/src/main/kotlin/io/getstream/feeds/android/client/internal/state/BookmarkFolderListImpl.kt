package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.BookmarkFolderData
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.state.BookmarkFolderList
import io.getstream.feeds.android.client.api.state.BookmarkFolderListState
import io.getstream.feeds.android.client.api.state.BookmarkFoldersQuery
import io.getstream.feeds.android.client.internal.repository.BookmarksRepository

/**
 * A class that manages a paginated list of bookmark folders.
 *
 * [BookmarkFolderList] provides functionality to query and paginate through bookmark folders.
 * It maintains the current state of the bookmark folder list and provides methods to load more
 * folders when available.
 *
 * @property query The query used to fetch the bookmark folders.
 * @property bookmarksRepository The repository used to interact with the bookmark data source.
 */
internal class BookmarkFolderListImpl(
    override val query: BookmarkFoldersQuery,
    private val bookmarksRepository: BookmarksRepository,
    // TODO: Observe events
) : BookmarkFolderList {

    private val _state: BookmarkFolderListStateImpl = BookmarkFolderListStateImpl(query)

    override val state: BookmarkFolderListState
        get() = _state

    override suspend fun get(): Result<List<BookmarkFolderData>> {
        return queryBookmarkFolders(query)
    }

    override suspend fun queryMoreBookmarkFolders(limit: Int?): Result<List<BookmarkFolderData>> {
        val next = _state.pagination?.next
        if (next == null) {
            // If there is no next cursor, return an empty list.
            return Result.success(emptyList())
        }
        val nextQuery = BookmarkFoldersQuery(
            filter = _state.queryConfig?.filter,
            sort = _state.queryConfig?.sort,
            limit = limit ?: query.limit,
            next = next,
            previous = null,
        )
        return queryBookmarkFolders(nextQuery)
    }

    private suspend fun queryBookmarkFolders(
        query: BookmarkFoldersQuery,
    ): Result<List<BookmarkFolderData>> {
        return bookmarksRepository.queryBookmarkFolders(query)
            .onSuccess {
                _state.onQueryMoreBookmarkFolders(it, QueryConfiguration(query.filter, query.sort))
            }
            .map {
                it.models
            }
    }
}