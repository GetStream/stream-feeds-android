package io.getstream.feeds.android.client.internal.repository

import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.model.BookmarkFolderData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.api.state.query.BookmarkFoldersQuery
import io.getstream.feeds.android.client.api.state.query.BookmarksQuery
import io.getstream.feeds.android.client.api.state.query.toRequest
import io.getstream.feeds.android.core.generated.apis.ApiService
import io.getstream.feeds.android.core.generated.models.AddBookmarkRequest
import io.getstream.feeds.android.core.generated.models.UpdateBookmarkRequest

/**
 * Default implementation of the [BookmarksRepository] interface.
 *
 * Uses the provided [ApiService] to perform network requests related to bookmarks.
 *
 * @property api The API service used to perform network requests.
 */
internal class BookmarksRepositoryImpl(private val api: ApiService) : BookmarksRepository {

    override suspend fun queryBookmarks(
        query: BookmarksQuery
    ): Result<PaginationResult<BookmarkData>> = runCatching {
        val response = api.queryBookmarks(query.toRequest())
        PaginationResult(
            models = response.bookmarks.map { it.toModel() },
            pagination = PaginationData(response.next, response.prev),
        )
    }

    override suspend fun addBookmark(
        activityId: String,
        request: AddBookmarkRequest
    ): Result<BookmarkData> = runCatching {
        api.addBookmark(activityId, request).bookmark.toModel()
    }

    override suspend fun deleteBookmark(
        activityId: String,
        folderId: String?
    ): Result<BookmarkData> = runCatching {
        api.deleteBookmark(activityId, folderId).bookmark.toModel()
    }

    override suspend fun updateBookmark(
        activityId: String,
        request: UpdateBookmarkRequest
    ): Result<BookmarkData> = runCatching {
        api.updateBookmark(activityId, request).bookmark.toModel()
    }

    override suspend fun queryBookmarkFolders(
        query: BookmarkFoldersQuery
    ): Result<PaginationResult<BookmarkFolderData>> = runCatching {
        val response = api.queryBookmarkFolders(query.toRequest())
        PaginationResult(
            models = response.bookmarkFolders.map { it.toModel() },
            pagination = PaginationData(response.next, response.prev),
        )
    }
}