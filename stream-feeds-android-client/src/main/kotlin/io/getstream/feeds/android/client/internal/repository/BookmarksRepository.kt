package io.getstream.feeds.android.client.internal.repository

import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.model.BookmarkFolderData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.state.query.BookmarkFoldersQuery
import io.getstream.feeds.android.client.api.state.query.BookmarksQuery
import io.getstream.feeds.android.core.generated.models.AddBookmarkRequest
import io.getstream.feeds.android.core.generated.models.UpdateBookmarkRequest

internal interface BookmarksRepository {

    suspend fun queryBookmarks(query: BookmarksQuery): Result<PaginationResult<BookmarkData>>

    suspend fun addBookmark(activityId: String, request: AddBookmarkRequest): Result<BookmarkData>

    suspend fun deleteBookmark(activityId: String, folderId: String?): Result<BookmarkData>

    suspend fun updateBookmark(
        activityId: String,
        request: UpdateBookmarkRequest
    ): Result<BookmarkData>

    suspend fun queryBookmarkFolders(query: BookmarkFoldersQuery): Result<PaginationResult<BookmarkFolderData>>

}