/*
 * Copyright (c) 2014-2026 Stream.io Inc. All rights reserved.
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

package io.getstream.feeds.android.client.internal.state

import io.getstream.android.core.api.subscribe.StreamSubscriptionManager
import io.getstream.feeds.android.client.api.model.BookmarkFolderData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.BookmarkFoldersQuery
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.repository.BookmarksRepository
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkFolderData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class BookmarkFolderListImplTest {
    private val bookmarksRepository: BookmarksRepository = mockk()
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener> =
        mockk(relaxed = true)
    private val query = BookmarkFoldersQuery(limit = 10)

    private val bookmarkFolderList =
        BookmarkFolderListImpl(
            query = query,
            bookmarksRepository = bookmarksRepository,
            subscriptionManager = subscriptionManager,
        )

    @Test
    fun `on get, then return bookmark folders and update state`() = runTest {
        val folders = listOf(bookmarkFolderData("folder-1"), bookmarkFolderData("folder-2"))
        val paginationResult = createPaginationResult(folders, next = "next-cursor")
        coEvery { bookmarksRepository.queryBookmarkFolders(query) } returns
            Result.success(paginationResult)

        val result = bookmarkFolderList.get()

        assertEquals(folders, result.getOrNull())
        coVerify { bookmarksRepository.queryBookmarkFolders(query) }
    }

    @Test
    fun `on queryMoreBookmarkFolders with next cursor, then query with next cursor`() = runTest {
        setupInitialState()

        val moreFolders = listOf(bookmarkFolderData("folder-2"), bookmarkFolderData("folder-3"))
        val morePaginationResult =
            createPaginationResult(
                folders = moreFolders,
                next = "next-cursor-2",
                previous = "next-cursor",
            )
        coEvery { bookmarksRepository.queryBookmarkFolders(any()) } returns
            Result.success(morePaginationResult)

        val result = bookmarkFolderList.queryMoreBookmarkFolders()

        assertEquals(moreFolders, result.getOrNull())
        coVerify { bookmarksRepository.queryBookmarkFolders(any()) }
    }

    @Test
    fun `on queryMoreBookmarkFolders with no next cursor, then return empty list`() = runTest {
        setupInitialState(nextCursor = null)

        val result = bookmarkFolderList.queryMoreBookmarkFolders()

        assertEquals(emptyList<BookmarkFolderData>(), result.getOrNull())
        coVerify(exactly = 1) { bookmarksRepository.queryBookmarkFolders(any()) }
    }

    @Test
    fun `on queryMoreBookmarkFolders with custom limit, then use custom limit`() = runTest {
        setupInitialState()

        val customLimit = 5
        val moreFolders = listOf(bookmarkFolderData("folder-2"))
        val morePaginationResult =
            createPaginationResult(folders = moreFolders, previous = "next-cursor")
        coEvery { bookmarksRepository.queryBookmarkFolders(any()) } returns
            Result.success(morePaginationResult)

        val result = bookmarkFolderList.queryMoreBookmarkFolders(customLimit)

        assertEquals(moreFolders, result.getOrNull())
        coVerify { bookmarksRepository.queryBookmarkFolders(any()) }
    }

    private suspend fun setupInitialState(nextCursor: String? = "next-cursor") {
        val initialFolders = listOf(bookmarkFolderData("folder-1"))
        val initialPaginationResult =
            PaginationResult(
                models = initialFolders,
                pagination = PaginationData(next = nextCursor, previous = null),
            )
        coEvery { bookmarksRepository.queryBookmarkFolders(query) } returns
            Result.success(initialPaginationResult)
        bookmarkFolderList.get()
    }

    private fun createPaginationResult(
        folders: List<BookmarkFolderData>,
        next: String? = null,
        previous: String? = null,
    ) =
        PaginationResult(
            models = folders,
            pagination = PaginationData(next = next, previous = previous),
        )
}
