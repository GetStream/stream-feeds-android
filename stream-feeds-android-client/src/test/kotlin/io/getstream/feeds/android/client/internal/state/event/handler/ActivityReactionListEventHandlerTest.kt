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

import io.getstream.feeds.android.client.internal.state.ActivityReactionListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.mockk.called
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

internal class ActivityReactionListEventHandlerTest {
    private val activityId = "activity-1"
    private val state: ActivityReactionListStateUpdates = mockk(relaxed = true)

    private val handler = ActivityReactionListEventHandler(activityId, state)

    @Test
    fun `on ActivityReactionAdded for matching activity, then call onReactionAdded`() {
        val reaction = feedsReactionData(activityId)
        val event = StateUpdateEvent.ActivityReactionAdded("feed-1", reaction)

        handler.onEvent(event)

        verify { state.onReactionAdded(reaction) }
    }

    @Test
    fun `on ActivityReactionAdded for different activity, then do not call onReactionAdded`() {
        val reaction = feedsReactionData("different-activity")
        val event = StateUpdateEvent.ActivityReactionAdded("feed-1", reaction)

        handler.onEvent(event)

        verify(exactly = 0) { state.onReactionAdded(any()) }
    }

    @Test
    fun `on ActivityReactionDeleted for matching activity, then call onReactionRemoved`() {
        val reaction = feedsReactionData(activityId)
        val event = StateUpdateEvent.ActivityReactionDeleted("feed-1", reaction)

        handler.onEvent(event)

        verify { state.onReactionRemoved(reaction) }
    }

    @Test
    fun `on ActivityReactionDeleted for different activity, then do not call onReactionRemoved`() {
        val reaction = feedsReactionData("different-activity")
        val event = StateUpdateEvent.ActivityReactionDeleted("feed-1", reaction)

        handler.onEvent(event)

        verify(exactly = 0) { state.onReactionRemoved(any()) }
    }

    @Test
    fun `on unknown event, then do nothing`() {
        val unknownEvent = StateUpdateEvent.BookmarkFolderDeleted("folder-id")

        handler.onEvent(unknownEvent)

        verify { state wasNot called }
    }
}
