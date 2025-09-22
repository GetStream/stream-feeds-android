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

import io.getstream.feeds.android.client.internal.state.CommentListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.mockk.called
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

internal class CommentListEventHandlerTest {

    private val state: CommentListStateUpdates = mockk(relaxed = true)
    private val handler = CommentListEventHandler(state)

    @Test
    fun `on CommentUpdatedEvent, then call onCommentUpdated`() {
        val comment = commentData()
        val event = StateUpdateEvent.CommentUpdated(comment)

        handler.onEvent(event)

        verify { state.onCommentUpdated(comment) }
    }

    @Test
    fun `on CommentDeletedEvent, then call onCommentRemoved`() {
        val comment = commentData()
        val event = StateUpdateEvent.CommentDeleted("feed-1", comment)

        handler.onEvent(event)

        verify { state.onCommentRemoved(comment.id) }
    }

    @Test
    fun `on unknown event, then do nothing`() {
        val comment = commentData()
        val unknownEvent = StateUpdateEvent.CommentAdded("feed-1", comment)

        handler.onEvent(unknownEvent)

        verify { state wasNot called }
    }
}
