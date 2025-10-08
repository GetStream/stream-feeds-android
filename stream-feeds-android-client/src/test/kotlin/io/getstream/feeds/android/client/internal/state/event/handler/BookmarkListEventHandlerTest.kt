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
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkAdded
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkFolderDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkFolderUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkUpdated
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkFolderData
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.mockk.MockKVerificationScope
import io.mockk.called
import io.mockk.mockk
import org.junit.runners.Parameterized

internal class BookmarkListEventHandlerTest(
    testName: String,
    event: StateUpdateEvent,
    verifyBlock: MockKVerificationScope.(BookmarkListStateUpdates) -> Unit,
) : BaseEventHandlerTest<BookmarkListStateUpdates>(testName, event, verifyBlock) {

    override val state: BookmarkListStateUpdates = mockk(relaxed = true)
    override val handler: StateUpdateEventListener = BookmarkListEventHandler(state)

    companion object {
        private val bookmark = bookmarkData()
        private val folder = bookmarkFolderData()

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Collection<Array<Any>> =
            listOf(
                testParams<BookmarkListStateUpdates>(
                    name = "BookmarkFolderDeleted",
                    event = BookmarkFolderDeleted("folder-1"),
                    verifyBlock = { state -> state.onBookmarkFolderRemoved("folder-1") },
                ),
                testParams<BookmarkListStateUpdates>(
                    name = "BookmarkFolderUpdated",
                    event = BookmarkFolderUpdated(folder),
                    verifyBlock = { state -> state.onBookmarkFolderUpdated(folder) },
                ),
                testParams<BookmarkListStateUpdates>(
                    name = "BookmarkAdded",
                    event = BookmarkAdded(bookmark),
                    verifyBlock = { state -> state.onBookmarkUpserted(bookmark) },
                ),
                testParams<BookmarkListStateUpdates>(
                    name = "BookmarkDeleted",
                    event = BookmarkDeleted(bookmark),
                    verifyBlock = { state -> state.onBookmarkRemoved(bookmark) },
                ),
                testParams<BookmarkListStateUpdates>(
                    name = "BookmarkUpdated",
                    event = BookmarkUpdated(bookmark),
                    verifyBlock = { state -> state.onBookmarkUpserted(bookmark) },
                ),
                testParams<BookmarkListStateUpdates>(
                    name = "unknown event",
                    event = StateUpdateEvent.CommentAdded("feed-1", commentData()),
                    verifyBlock = { state -> state wasNot called },
                ),
            )
    }
}
