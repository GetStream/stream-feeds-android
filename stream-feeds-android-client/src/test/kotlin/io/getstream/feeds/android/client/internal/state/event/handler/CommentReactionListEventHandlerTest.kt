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
import io.getstream.feeds.android.client.internal.state.CommentReactionListStateUpdates
import io.getstream.feeds.android.client.internal.test.TestData.commentResponse
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionResponse
import io.getstream.feeds.android.network.models.CommentReactionDeletedEvent
import io.getstream.feeds.android.network.models.WSEvent
import io.mockk.called
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import java.util.Date

internal class CommentReactionListEventHandlerTest {
    private val state: CommentReactionListStateUpdates = mockk(relaxed = true)

    private val handler = CommentReactionListEventHandler(state)

    @Test
    fun `on CommentReactionDeletedEvent, then call onReactionRemoved`() {
        val reaction = feedsReactionResponse()
        val comment = commentResponse()
        val event = CommentReactionDeletedEvent(
            createdAt = Date(),
            fid = "user:feed-1",
            comment = comment,
            reaction = reaction,
            type = "feeds.comment.reaction.deleted"
        )

        handler.onEvent(event)

        verify { state.onReactionRemoved(reaction.toModel()) }
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
