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

import io.getstream.feeds.android.client.api.model.BookmarkFolderData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.state.query.BookmarkFoldersQuery
import io.getstream.feeds.android.client.api.state.query.BookmarkFoldersQueryConfig
import io.getstream.feeds.android.client.api.state.query.BookmarkFoldersSort
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkFolderData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class BookmarkFolderListStateImplTest {
    private val query = BookmarkFoldersQuery(limit = 10)
    private val bookmarkFolderListState = BookmarkFolderListStateImpl(query)

    @Test
    fun `on initial state, then return empty folders and null pagination`() = runTest {
        assertEquals(emptyList<BookmarkFolderData>(), bookmarkFolderListState.folders.value)
        assertNull(bookmarkFolderListState.pagination)
    }

    @Test
    fun `on queryMoreBookmarkFolders, then update folders and pagination`() = runTest {
        val folders = listOf(bookmarkFolderData(), bookmarkFolderData("folder-2", "Test Folder 2"))
        val paginationResult =
            PaginationResult(
                models = folders,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig =
            BookmarkFoldersQueryConfig(filter = null, sort = BookmarkFoldersSort.Default)

        bookmarkFolderListState.onQueryMoreBookmarkFolders(paginationResult, queryConfig)

        assertEquals(folders, bookmarkFolderListState.folders.value)
        assertEquals("next-cursor", bookmarkFolderListState.pagination?.next)
        assertEquals(queryConfig, bookmarkFolderListState.queryConfig)
    }

    @Test
    fun `on bookmarkFolderUpdated, then update specific folder`() = runTest {
        val initialFolders =
            listOf(bookmarkFolderData(), bookmarkFolderData("folder-2", "Test Folder 2"))
        val paginationResult =
            PaginationResult(
                models = initialFolders,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig =
            BookmarkFoldersQueryConfig(filter = null, sort = BookmarkFoldersSort.Default)
        bookmarkFolderListState.onQueryMoreBookmarkFolders(paginationResult, queryConfig)

        val updatedFolder = bookmarkFolderData("folder-1", "Updated Folder")
        bookmarkFolderListState.onBookmarkFolderUpdated(updatedFolder)

        val updatedFolders = bookmarkFolderListState.folders.value
        assertEquals(updatedFolder, updatedFolders.find { it.id == updatedFolder.id })
        assertEquals(initialFolders[1], updatedFolders.find { it.id == initialFolders[1].id })
    }

    @Test
    fun `on bookmarkFolderRemoved, then remove specific folder`() = runTest {
        val initialFolders =
            listOf(bookmarkFolderData(), bookmarkFolderData("folder-2", "Test Folder 2"))
        val paginationResult =
            PaginationResult(
                models = initialFolders,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig =
            BookmarkFoldersQueryConfig(filter = null, sort = BookmarkFoldersSort.Default)
        bookmarkFolderListState.onQueryMoreBookmarkFolders(paginationResult, queryConfig)

        bookmarkFolderListState.onBookmarkFolderRemoved(initialFolders[0].id)

        val remainingFolders = bookmarkFolderListState.folders.value
        assertEquals(initialFolders.drop(1), remainingFolders)
    }
}
