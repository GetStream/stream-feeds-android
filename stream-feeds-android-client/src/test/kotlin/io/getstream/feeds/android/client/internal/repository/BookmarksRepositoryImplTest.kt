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

import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.api.state.query.BookmarkFoldersQuery
import io.getstream.feeds.android.client.api.state.query.BookmarksQuery
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.model.toModel
import io.getstream.feeds.android.client.internal.repository.RepositoryTestUtils.testDelegation
import io.getstream.feeds.android.client.internal.state.query.toRequest
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkFolderResponse
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkResponse
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.AddBookmarkRequest
import io.getstream.feeds.android.network.models.AddBookmarkResponse
import io.getstream.feeds.android.network.models.DeleteBookmarkResponse
import io.getstream.feeds.android.network.models.QueryBookmarkFoldersResponse
import io.getstream.feeds.android.network.models.QueryBookmarksResponse
import io.getstream.feeds.android.network.models.UpdateBookmarkRequest
import io.getstream.feeds.android.network.models.UpdateBookmarkResponse
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class BookmarksRepositoryImplTest {
    private val feedsApi: FeedsApi = mockk()
    private val repository = BookmarksRepositoryImpl(api = feedsApi)

    @Test
    fun `on addBookmark, delegate to api`() = runTest {
        val request = AddBookmarkRequest()
        val apiResult = AddBookmarkResponse("duration", bookmarkResponse())

        testDelegation(
            apiFunction = { feedsApi.addBookmark("activity-1", request) },
            repositoryCall = { repository.addBookmark("activity-1", request) },
            apiResult = apiResult,
            repositoryResult = apiResult.bookmark.toModel(),
        )
    }

    @Test
    fun `on deleteBookmark, delegate to api`() = runTest {
        val apiResult = DeleteBookmarkResponse("duration", bookmarkResponse())

        testDelegation(
            apiFunction = { feedsApi.deleteBookmark("activity-1", "folder-1") },
            repositoryCall = { repository.deleteBookmark("activity-1", "folder-1") },
            apiResult = apiResult,
            repositoryResult = apiResult.bookmark.toModel(),
        )
    }

    @Test
    fun `on deleteBookmark without folder, delegate to api`() = runTest {
        val apiResult = DeleteBookmarkResponse("duration", bookmarkResponse())

        testDelegation(
            apiFunction = { feedsApi.deleteBookmark("activity-1", null) },
            repositoryCall = { repository.deleteBookmark("activity-1", null) },
            apiResult = apiResult,
            repositoryResult = apiResult.bookmark.toModel(),
        )
    }

    @Test
    fun `on updateBookmark, delegate to api`() = runTest {
        val request = UpdateBookmarkRequest()
        val apiResult = UpdateBookmarkResponse("duration", bookmarkResponse())

        testDelegation(
            apiFunction = { feedsApi.updateBookmark("activity-1", request) },
            repositoryCall = { repository.updateBookmark("activity-1", request) },
            apiResult = apiResult,
            repositoryResult = apiResult.bookmark.toModel(),
        )
    }

    @Test
    fun `on queryBookmarks, delegate to api`() = runTest {
        val query = BookmarksQuery()
        val request = query.toRequest()

        val apiResult =
            QueryBookmarksResponse(
                duration = "duration",
                bookmarks = listOf(bookmarkResponse()),
                next = "next",
                prev = "prev",
            )

        testDelegation(
            apiFunction = { feedsApi.queryBookmarks(request) },
            repositoryCall = { repository.queryBookmarks(query) },
            apiResult = apiResult,
            repositoryResult =
                PaginationResult(
                    models = listOf(bookmarkResponse().toModel()),
                    pagination = PaginationData(next = "next", previous = "prev"),
                ),
        )
    }

    @Test
    fun `on queryBookmarkFolders, delegate to api`() = runTest {
        val query = BookmarkFoldersQuery()
        val request = query.toRequest()

        val apiResult =
            QueryBookmarkFoldersResponse(
                duration = "duration",
                bookmarkFolders = listOf(bookmarkFolderResponse()),
                next = "next",
                prev = "prev",
            )

        testDelegation(
            apiFunction = { feedsApi.queryBookmarkFolders(request) },
            repositoryCall = { repository.queryBookmarkFolders(query) },
            apiResult = apiResult,
            repositoryResult =
                PaginationResult(
                    models = listOf(bookmarkFolderResponse().toModel()),
                    pagination = PaginationData(next = "next", previous = "prev"),
                ),
        )
    }
}
