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

import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.state.query.BookmarksQuery
import io.getstream.feeds.android.client.api.state.query.BookmarksSort
import io.getstream.feeds.android.client.internal.state.query.BookmarksQueryConfig
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkFolderData
import io.getstream.feeds.android.client.internal.test.TestData.defaultPaginationResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class BookmarkListStateImplTest {
    private val query = BookmarksQuery(limit = 10)
    private val bookmarkListState = BookmarkListStateImpl(query)

    @Test
    fun `on initial state, then return empty bookmarks and null pagination`() = runTest {
        assertEquals(emptyList<BookmarkData>(), bookmarkListState.bookmarks.value)
        assertNull(bookmarkListState.pagination)
    }

    @Test
    fun `on queryMoreBookmarks, then update bookmarks and pagination`() = runTest {
        val bookmarks = listOf(bookmarkData(), bookmarkData("bookmark-2", "user-2"))
        val paginationResult = defaultPaginationResult(bookmarks)

        bookmarkListState.onQueryMoreBookmarks(paginationResult, queryConfig)

        assertEquals(bookmarks, bookmarkListState.bookmarks.value)
        assertEquals("next-cursor", bookmarkListState.pagination?.next)
        assertEquals(queryConfig, bookmarkListState.queryConfig)
    }

    @Test
    fun `on onBookmarkUpserted, then add specific bookmark`() = runTest {
        val initialBookmark = bookmarkData("activity-2", "user-2", createdAt = 2000)
        setupInitialBookmarks(listOf(initialBookmark))

        val updatedBookmark = bookmarkData("activity-1", "user-1", createdAt = 3000)
        bookmarkListState.onBookmarkUpserted(updatedBookmark)

        val expected = listOf(updatedBookmark, initialBookmark)
        assertEquals(expected, bookmarkListState.bookmarks.value)
    }

    @Test
    fun `on onBookmarkUpserted, then update specific bookmark`() = runTest {
        val bookmark1 = bookmarkData("activity-2", "user-2", createdAt = 2000)
        val bookmark2 = bookmarkData("activity-1", "user-1", createdAt = 1000)
        setupInitialBookmarks(listOf(bookmark1, bookmark2))

        val updatedBookmark = bookmarkData("activity-1", "user-1", createdAt = 3000)
        bookmarkListState.onBookmarkUpserted(updatedBookmark)

        val expected = listOf(updatedBookmark, bookmark1)
        assertEquals(expected, bookmarkListState.bookmarks.value)
    }

    @Test
    fun `on bookmarkFolderUpdated, then update bookmarks with folder reference`() = runTest {
        val folder = bookmarkFolderData("folder-1", "Folder 1")
        val bookmark1 = bookmarkData(folder = folder)
        val bookmark2 =
            bookmarkData("activity-2", "user-2", bookmarkFolderData("folder-2", "Folder 2"))
        setupInitialBookmarks(listOf(bookmark1, bookmark2))

        val updatedFolder = bookmarkFolderData("folder-1", "Updated Folder")
        bookmarkListState.onBookmarkFolderUpdated(updatedFolder)

        val expected = listOf(bookmark1.copy(folder = updatedFolder), bookmark2)
        assertEquals(expected, bookmarkListState.bookmarks.value)
    }

    @Test
    fun `on bookmarkFolderRemoved, then remove folder reference from bookmarks`() = runTest {
        val folder = bookmarkFolderData("folder-1", "Folder 1")
        val bookmark1 = bookmarkData(folder = folder)
        val bookmark2 =
            bookmarkData("activity-2", "user-2", bookmarkFolderData("folder-2", "Folder 2"))
        setupInitialBookmarks(listOf(bookmark1, bookmark2))

        bookmarkListState.onBookmarkFolderRemoved(folder.id)

        val expected = listOf(bookmark1.copy(folder = null), bookmark2)
        assertEquals(expected, bookmarkListState.bookmarks.value)
    }

    @Test
    fun `on bookmarkRemoved, then remove specific bookmark`() = runTest {
        val bookmark1 = bookmarkData("activity-1", "user-1")
        val bookmark2 = bookmarkData("activity-2", "user-2")
        val bookmark3 = bookmarkData("activity-3", "user-3")
        setupInitialBookmarks(listOf(bookmark1, bookmark2, bookmark3))

        bookmarkListState.onBookmarkRemoved(bookmark2)

        val expected = listOf(bookmark1, bookmark3)
        assertEquals(expected, bookmarkListState.bookmarks.value)
    }

    @Test
    fun `on bookmarkRemoved with nonexistent bookmark, then keep all bookmarks`() = runTest {
        val bookmark1 = bookmarkData("activity-1", "user-1")
        val bookmark2 = bookmarkData("activity-2", "user-2")
        setupInitialBookmarks(listOf(bookmark1, bookmark2))

        val nonexistentBookmark = bookmarkData("activity-999", "user-999")
        bookmarkListState.onBookmarkRemoved(nonexistentBookmark)

        val expected = listOf(bookmark1, bookmark2)
        assertEquals(expected, bookmarkListState.bookmarks.value)
    }

    private fun setupInitialBookmarks(bookmarks: List<BookmarkData>) {
        val paginationResult = defaultPaginationResult(bookmarks)
        bookmarkListState.onQueryMoreBookmarks(paginationResult, queryConfig)
    }

    companion object {
        private val queryConfig = BookmarksQueryConfig(filter = null, sort = BookmarksSort.Default)
    }
}
