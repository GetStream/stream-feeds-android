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
import io.getstream.feeds.android.client.internal.state.PollListStateUpdates
import io.getstream.feeds.android.client.internal.test.TestData.pollResponseData
import io.getstream.feeds.android.network.models.PollUpdatedFeedEvent
import io.getstream.feeds.android.network.models.WSEvent
import io.mockk.called
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import java.util.Date

internal class PollListEventHandlerTest {
    private val state: PollListStateUpdates = mockk(relaxed = true)

    private val handler = PollListEventHandler(state)

    @Test
    fun `on PollUpdatedFeedEvent, then call onPollUpdated`() {
        val poll = pollResponseData()
        val event = PollUpdatedFeedEvent(
            createdAt = Date(),
            fid = "user:feed-1",
            poll = poll,
            type = "feeds.poll.updated"
        )

        handler.onEvent(event)

        verify { state.onPollUpdated(poll.toModel()) }
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
