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

import io.getstream.android.core.api.filter.equal
import io.getstream.feeds.android.client.api.state.query.BookmarksFilterField
import io.getstream.feeds.android.client.internal.state.BookmarkListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkAdded
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkFolderDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkFolderUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkUpdated
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkFolderData
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
    override val handler: StateUpdateEventListener = BookmarkListEventHandler(filter, state)

    companion object {
        private val filter = BookmarksFilterField.activityId.equal("activity-1")
        private val matchingBookmark = bookmarkData(activity = activityData("activity-1"))
        private val nonMatchingBookmark = bookmarkData(activity = activityData("other-activity"))
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
                    name = "BookmarkAdded matching filter",
                    event = BookmarkAdded(matchingBookmark),
                    verifyBlock = { state -> state.onBookmarkUpserted(matchingBookmark) },
                ),
                testParams<BookmarkListStateUpdates>(
                    name = "BookmarkAdded non-matching filter",
                    event = BookmarkAdded(nonMatchingBookmark),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<BookmarkListStateUpdates>(
                    name = "BookmarkDeleted",
                    event = BookmarkDeleted(matchingBookmark),
                    verifyBlock = { state -> state.onBookmarkRemoved(matchingBookmark) },
                ),
                testParams<BookmarkListStateUpdates>(
                    name = "BookmarkUpdated matching filter",
                    event = BookmarkUpdated(matchingBookmark),
                    verifyBlock = { state -> state.onBookmarkUpserted(matchingBookmark) },
                ),
                testParams<BookmarkListStateUpdates>(
                    name = "BookmarkUpdated non-matching filter",
                    event = BookmarkUpdated(nonMatchingBookmark),
                    verifyBlock = { state -> state.onBookmarkRemoved(nonMatchingBookmark) },
                ),
                testParams<BookmarkListStateUpdates>(
                    name = "unknown event",
                    event = StateUpdateEvent.PollDeleted("poll-1"),
                    verifyBlock = { state -> state wasNot called },
                ),
            )
    }
}
