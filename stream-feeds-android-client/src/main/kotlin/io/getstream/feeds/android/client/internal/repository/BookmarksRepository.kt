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
        request: UpdateBookmarkRequest,
    ): Result<BookmarkData>

    suspend fun queryBookmarkFolders(
        query: BookmarkFoldersQuery
    ): Result<PaginationResult<BookmarkFolderData>>
}
