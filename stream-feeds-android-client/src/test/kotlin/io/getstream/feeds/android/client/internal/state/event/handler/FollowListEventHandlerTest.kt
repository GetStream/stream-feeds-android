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
import io.getstream.feeds.android.client.internal.state.FollowListStateUpdates
import io.getstream.feeds.android.client.internal.test.TestData.followResponse
import io.getstream.feeds.android.network.models.FollowDeletedEvent
import io.getstream.feeds.android.network.models.FollowUpdatedEvent
import io.getstream.feeds.android.network.models.WSEvent
import io.mockk.called
import io.mockk.mockk
import io.mockk.verify
import java.util.Date
import org.junit.Test

internal class FollowListEventHandlerTest {
    private val state: FollowListStateUpdates = mockk(relaxed = true)

    private val handler = FollowListEventHandler(state)

    @Test
    fun `on FollowUpdatedEvent, then call onFollowUpdated`() {
        val follow = followResponse()
        val event =
            FollowUpdatedEvent(
                createdAt = Date(),
                fid = "user:feed-1",
                follow = follow,
                type = "feeds.follow.updated",
            )

        handler.onEvent(event)

        verify { state.onFollowUpdated(follow.toModel()) }
    }

    @Test
    fun `on FollowDeletedEvent, then call onFollowRemoved`() {
        val follow = followResponse()
        val event =
            FollowDeletedEvent(
                createdAt = Date(),
                fid = "user:feed-1",
                follow = follow,
                type = "feeds.follow.updated",
            )

        handler.onEvent(event)

        verify { state.onFollowRemoved(follow.toModel()) }
    }

    @Test
    fun `on unknown event, then do nothing`() {
        val unknownEvent =
            object : WSEvent {
                override fun getWSEventType(): String = "unknown.event"
            }

        handler.onEvent(unknownEvent)

        verify { state wasNot called }
    }
}
