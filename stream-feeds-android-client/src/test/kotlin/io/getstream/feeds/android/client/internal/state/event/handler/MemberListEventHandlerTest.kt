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

import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.state.MemberListStateUpdates
import io.getstream.feeds.android.client.internal.test.TestData.feedMemberResponse
import io.getstream.feeds.android.network.models.FeedMemberRemovedEvent
import io.getstream.feeds.android.network.models.FeedMemberUpdatedEvent
import io.getstream.feeds.android.network.models.WSEvent
import io.mockk.called
import io.mockk.mockk
import io.mockk.verify
import java.util.Date
import org.junit.Test

internal class MemberListEventHandlerTest {
    private val fid = FeedId("user:feed-1")
    private val state: MemberListStateUpdates = mockk(relaxed = true)

    private val handler = MemberListEventHandler(fid, state)

    @Test
    fun `on FeedMemberRemovedEvent, then call onMemberRemoved`() {
        val event =
            FeedMemberRemovedEvent(
                createdAt = Date(),
                fid = "user:feed-1",
                memberId = "member-1",
                type = "feeds.feed_member.removed",
            )

        handler.onEvent(event)

        verify { state.onMemberRemoved("member-1") }
    }

    @Test
    fun `on FeedMemberUpdatedEvent, then call onMemberUpdated`() {
        val member = feedMemberResponse()
        val event =
            FeedMemberUpdatedEvent(
                createdAt = Date(),
                fid = "user:feed-1",
                member = member,
                type = "feeds.feed_member.updated",
            )

        handler.onEvent(event)

        verify { state.onMemberUpdated(member.toModel()) }
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
