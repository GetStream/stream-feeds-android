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

import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.state.ActivityReactionListStateUpdates
import io.getstream.feeds.android.client.internal.test.TestData.activityResponse
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionResponse
import io.getstream.feeds.android.network.models.ActivityReactionDeletedEvent
import io.getstream.feeds.android.network.models.WSEvent
import io.mockk.called
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import java.util.Date

internal class ActivityReactionListEventHandlerTest {
    private val activityId = "activity-1"
    private val state: ActivityReactionListStateUpdates = mockk(relaxed = true)

    private val handler = ActivityReactionListEventHandler(activityId, state)

    @Test
    fun `on ActivityReactionDeletedEvent for matching activity, then call onReactionRemoved`() {
        val activity = activityResponse().copy(id = activityId)
        val reaction = feedsReactionResponse()
        val event = ActivityReactionDeletedEvent(
            createdAt = Date(),
            fid = "user:feed-1",
            activity = activity,
            reaction = reaction,
            type = "feeds.activity.reaction.deleted"
        )

        handler.onEvent(event)

        verify { state.onReactionRemoved(reaction.toModel()) }
    }

    @Test
    fun `on ActivityReactionDeletedEvent for different activity, then do not call onReactionRemoved`() {
        val activity = activityResponse().copy(id = "different-activity")
        val reaction = feedsReactionResponse()
        val event = ActivityReactionDeletedEvent(
            createdAt = Date(),
            fid = "user:feed-1",
            activity = activity,
            reaction = reaction,
            type = "feeds.activity.reaction.deleted"
        )

        handler.onEvent(event)

        verify(exactly = 0) { state.onReactionRemoved(any()) }
    }

    @Test
    fun `on unknown event, then do nothing`() {
        val unknownEvent = object : WSEvent {
            override fun getWSEventType(): String = "unknown.event"
        }

        handler.onEvent(unknownEvent)

        verify { state wasNot called }
    }
}
