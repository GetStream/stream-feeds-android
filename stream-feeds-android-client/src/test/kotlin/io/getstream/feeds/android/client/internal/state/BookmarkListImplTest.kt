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
package io.getstream.feeds.android.client.internal.state

import io.getstream.android.core.api.subscribe.StreamSubscriptionManager
import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.BookmarksQuery
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.repository.BookmarksRepository
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class BookmarkListImplTest {
    private val bookmarksRepository: BookmarksRepository = mockk()
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener> =
        mockk(relaxed = true)
    private val query = BookmarksQuery(limit = 10)

    private val bookmarkList =
        BookmarkListImpl(
            query = query,
            bookmarksRepository = bookmarksRepository,
            subscriptionManager = subscriptionManager,
        )

    @Test
    fun `on get, then return bookmarks and update state`() = runTest {
        val bookmarks = listOf(bookmarkData("bookmark-1"), bookmarkData("bookmark-2"))
        val paginationResult = createPaginationResult(bookmarks, next = "next-cursor")
        coEvery { bookmarksRepository.queryBookmarks(query) } returns
            Result.success(paginationResult)

        val result = bookmarkList.get()

        assertEquals(bookmarks, result.getOrNull())
        coVerify { bookmarksRepository.queryBookmarks(query) }
    }

    @Test
    fun `on queryMoreBookmarks with next cursor, then query with next cursor`() = runTest {
        setupInitialState()

        val moreBookmarks = listOf(bookmarkData("bookmark-2"), bookmarkData("bookmark-3"))
        val morePaginationResult =
            createPaginationResult(
                bookmarks = moreBookmarks,
                next = "next-cursor-2",
                previous = "next-cursor",
            )
        coEvery { bookmarksRepository.queryBookmarks(any()) } returns
            Result.success(morePaginationResult)

        val result = bookmarkList.queryMoreBookmarks()

        assertEquals(moreBookmarks, result.getOrNull())
        coVerify { bookmarksRepository.queryBookmarks(any()) }
    }

    @Test
    fun `on queryMoreBookmarks with no next cursor, then return empty list`() = runTest {
        setupInitialState(nextCursor = null)

        val result = bookmarkList.queryMoreBookmarks()

        assertEquals(emptyList<BookmarkData>(), result.getOrNull())
        coVerify(exactly = 1) { bookmarksRepository.queryBookmarks(any()) }
    }

    @Test
    fun `on queryMoreBookmarks with custom limit, then use custom limit`() = runTest {
        setupInitialState()

        val customLimit = 5
        val moreBookmarks = listOf(bookmarkData("bookmark-2"))
        val morePaginationResult =
            createPaginationResult(bookmarks = moreBookmarks, previous = "next-cursor")
        coEvery { bookmarksRepository.queryBookmarks(any()) } returns
            Result.success(morePaginationResult)

        val result = bookmarkList.queryMoreBookmarks(customLimit)

        assertEquals(moreBookmarks, result.getOrNull())
        coVerify { bookmarksRepository.queryBookmarks(any()) }
    }

    private suspend fun setupInitialState(nextCursor: String? = "next-cursor") {
        val initialBookmarks = listOf(bookmarkData("bookmark-1"))
        val initialPaginationResult =
            PaginationResult(
                models = initialBookmarks,
                pagination = PaginationData(next = nextCursor, previous = null),
            )
        coEvery { bookmarksRepository.queryBookmarks(query) } returns
            Result.success(initialPaginationResult)
        bookmarkList.get()
    }

    private fun createPaginationResult(
        bookmarks: List<BookmarkData>,
        next: String? = null,
        previous: String? = null,
    ) =
        PaginationResult(
            models = bookmarks,
            pagination = PaginationData(next = next, previous = previous),
        )
}
