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
package io.getstream.feeds.android.client.internal.state.event.handler

import io.getstream.feeds.android.client.internal.state.BookmarkListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkFolderData
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.mockk.called
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

internal class BookmarkListEventHandlerTest {

    private val state: BookmarkListStateUpdates = mockk(relaxed = true)
    private val handler = BookmarkListEventHandler(state)

    @Test
    fun `on BookmarkFolderDeletedEvent, then call onBookmarkFolderRemoved`() {
        val folderId = "folder-1"
        val event = StateUpdateEvent.BookmarkFolderDeleted(folderId)

        handler.onEvent(event)

        verify { state.onBookmarkFolderRemoved(folderId) }
    }

    @Test
    fun `on BookmarkFolderUpdatedEvent, then call onBookmarkFolderUpdated`() {
        val folder = bookmarkFolderData()
        val event = StateUpdateEvent.BookmarkFolderUpdated(folder)

        handler.onEvent(event)

        verify { state.onBookmarkFolderUpdated(folder) }
    }

    @Test
    fun `on BookmarkUpdatedEvent, then call onBookmarkUpdated`() {
        val bookmark = bookmarkData()
        val event = StateUpdateEvent.BookmarkUpdated(bookmark)

        handler.onEvent(event)

        verify { state.onBookmarkUpdated(bookmark) }
    }

    @Test
    fun `on BookmarkDeletedEvent, then call onBookmarkRemoved`() {
        val bookmark = bookmarkData()
        val event = StateUpdateEvent.BookmarkDeleted(bookmark)

        handler.onEvent(event)

        verify { state.onBookmarkRemoved(bookmark) }
    }

    @Test
    fun `on unknown event, then do nothing`() {
        val comment = commentData()
        val unknownEvent = StateUpdateEvent.CommentAdded(comment)

        handler.onEvent(unknownEvent)

        verify { state wasNot called }
    }
}
