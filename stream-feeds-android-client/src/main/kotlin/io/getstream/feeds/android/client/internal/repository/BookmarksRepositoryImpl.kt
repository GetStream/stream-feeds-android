/*
 * Copyright (c) 2014-2025 Stream.io Inc. All rights reserved.
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

package io.getstream.feeds.android.client.internal.repository

import io.getstream.android.core.result.runSafely
import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.model.BookmarkFolderData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.BookmarkFoldersQuery
import io.getstream.feeds.android.client.api.state.query.BookmarksQuery
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.model.toModel
import io.getstream.feeds.android.client.internal.state.query.toRequest
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.AddBookmarkRequest
import io.getstream.feeds.android.network.models.UpdateBookmarkRequest

/**
 * Default implementation of the [BookmarksRepository] interface.
 *
 * Uses the provided [FeedsApi] to perform network requests related to bookmarks.
 *
 * @property api The API service used to perform network requests.
 */
internal class BookmarksRepositoryImpl(private val api: FeedsApi) : BookmarksRepository {

    override suspend fun queryBookmarks(
        query: BookmarksQuery
    ): Result<PaginationResult<BookmarkData>> = runSafely {
        val response = api.queryBookmarks(query.toRequest())
        PaginationResult(
            models = response.bookmarks.map { it.toModel() },
            pagination = PaginationData(response.next, response.prev),
        )
    }

    override suspend fun addBookmark(
        activityId: String,
        request: AddBookmarkRequest,
    ): Result<BookmarkData> = runSafely { api.addBookmark(activityId, request).bookmark.toModel() }

    override suspend fun deleteBookmark(
        activityId: String,
        folderId: String?,
    ): Result<BookmarkData> = runSafely {
        api.deleteBookmark(activityId, folderId).bookmark.toModel()
    }

    override suspend fun updateBookmark(
        activityId: String,
        request: UpdateBookmarkRequest,
    ): Result<BookmarkData> = runSafely {
        api.updateBookmark(activityId, request).bookmark.toModel()
    }

    override suspend fun queryBookmarkFolders(
        query: BookmarkFoldersQuery
    ): Result<PaginationResult<BookmarkFolderData>> = runSafely {
        val response = api.queryBookmarkFolders(query.toRequest())
        PaginationResult(
            models = response.bookmarkFolders.map { it.toModel() },
            pagination = PaginationData(response.next, response.prev),
        )
    }
}
