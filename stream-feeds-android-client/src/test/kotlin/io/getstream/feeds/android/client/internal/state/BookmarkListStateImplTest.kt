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

import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.state.query.BookmarksQuery
import io.getstream.feeds.android.client.api.state.query.BookmarksSort
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkFolderData
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
        val paginationResult =
            PaginationResult(
                models = bookmarks,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig = QueryConfiguration(filter = null, sort = BookmarksSort.Default)

        bookmarkListState.onQueryMoreBookmarks(paginationResult, queryConfig)

        assertEquals(bookmarks, bookmarkListState.bookmarks.value)
        assertEquals("next-cursor", bookmarkListState.pagination?.next)
        assertEquals(queryConfig, bookmarkListState.queryConfig)
    }

    @Test
    fun `on bookmarkUpdated, then update specific bookmark`() = runTest {
        val initialBookmarks = listOf(bookmarkData(), bookmarkData("bookmark-2", "user-2"))
        val paginationResult =
            PaginationResult(
                models = initialBookmarks,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig = QueryConfiguration(filter = null, sort = BookmarksSort.Default)
        bookmarkListState.onQueryMoreBookmarks(paginationResult, queryConfig)

        val updatedBookmark = bookmarkData("activity-1", "user-1")
        bookmarkListState.onBookmarkUpdated(updatedBookmark)

        val updatedBookmarks = bookmarkListState.bookmarks.value
        assertEquals(updatedBookmark, updatedBookmarks.find { it.id == updatedBookmark.id })
        assertEquals(initialBookmarks[1], updatedBookmarks.find { it.id == initialBookmarks[1].id })
    }

    @Test
    fun `on bookmarkFolderUpdated, then update bookmarks with folder reference`() = runTest {
        val folder = bookmarkFolderData()
        val initialBookmarks =
            listOf(
                bookmarkData(folder = folder),
                bookmarkData(
                    "activity-2",
                    "user-2",
                    folder = bookmarkFolderData("folder-2", "Folder 2"),
                ),
            )
        val paginationResult =
            PaginationResult(
                models = initialBookmarks,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig = QueryConfiguration(filter = null, sort = BookmarksSort.Default)
        bookmarkListState.onQueryMoreBookmarks(paginationResult, queryConfig)

        val updatedFolder = bookmarkFolderData("folder-1", "Updated Folder")
        bookmarkListState.onBookmarkFolderUpdated(updatedFolder)

        val updatedBookmarks = bookmarkListState.bookmarks.value
        val bookmarkWithUpdatedFolder = updatedBookmarks.find { it.folder?.id == updatedFolder.id }
        assertEquals(updatedFolder, bookmarkWithUpdatedFolder?.folder)
    }

    @Test
    fun `on bookmarkFolderRemoved, then remove folder reference from bookmarks`() = runTest {
        val folder = bookmarkFolderData()
        val initialBookmarks =
            listOf(
                bookmarkData(folder = folder),
                bookmarkData(
                    "activity-2",
                    "user-2",
                    folder = bookmarkFolderData("folder-2", "Folder 2"),
                ),
            )
        val paginationResult =
            PaginationResult(
                models = initialBookmarks,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig = QueryConfiguration(filter = null, sort = BookmarksSort.Default)
        bookmarkListState.onQueryMoreBookmarks(paginationResult, queryConfig)

        bookmarkListState.onBookmarkFolderRemoved(folder.id)

        val updatedBookmarks = bookmarkListState.bookmarks.value
        val bookmarkWithoutFolder = updatedBookmarks.find { it.id == initialBookmarks[0].id }
        assertNull(bookmarkWithoutFolder?.folder)
        assertEquals(initialBookmarks[1], updatedBookmarks.find { it.id == initialBookmarks[1].id })
    }
}
