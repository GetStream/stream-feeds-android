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

package io.getstream.feeds.android.client.internal.state.event.handler

import io.getstream.android.core.api.filter.equal
import io.getstream.feeds.android.client.api.state.query.BookmarkFoldersFilterField
import io.getstream.feeds.android.client.internal.state.BookmarkFolderListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkFolderDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkFolderUpdated
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkFolderData
import io.mockk.MockKVerificationScope
import io.mockk.mockk
import org.junit.runners.Parameterized

internal class BookmarkFolderListEventHandlerTest(
    testName: String,
    event: StateUpdateEvent,
    verifyBlock: MockKVerificationScope.(BookmarkFolderListStateUpdates) -> Unit,
) : BaseEventHandlerTest<BookmarkFolderListStateUpdates>(testName, event, verifyBlock) {

    override val state: BookmarkFolderListStateUpdates = mockk(relaxed = true)
    override val handler = BookmarkFolderListEventHandler(filter, state)

    companion object {
        private val filter = BookmarkFoldersFilterField.folderName.equal("important")
        private val matchingFolder = bookmarkFolderData(name = "important")
        private val nonMatchingFolder = bookmarkFolderData(name = "general")

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): List<Array<Any>> =
            listOf(
                testParams<BookmarkFolderListStateUpdates>(
                    name = "BookmarkFolderDeleted calls onBookmarkFolderRemoved",
                    event = BookmarkFolderDeleted("folder-123"),
                    verifyBlock = { state -> state.onBookmarkFolderRemoved("folder-123") },
                ),
                testParams<BookmarkFolderListStateUpdates>(
                    name = "BookmarkFolderUpdated matching filter",
                    event = BookmarkFolderUpdated(matchingFolder),
                    verifyBlock = { state -> state.onBookmarkFolderUpdated(matchingFolder) },
                ),
                testParams<BookmarkFolderListStateUpdates>(
                    name = "BookmarkFolderUpdated non-matching filter",
                    event = BookmarkFolderUpdated(nonMatchingFolder),
                    verifyBlock = { state -> state.onBookmarkFolderRemoved(nonMatchingFolder.id) },
                ),
            )
    }
}
