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

import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.internal.state.ActivityCommentListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

internal class ActivityCommentListEventHandlerTest {
    private val objectId = "activity-1"
    private val objectType = "activity"
    private val state: ActivityCommentListStateUpdates = mockk(relaxed = true)

    private val handler = ActivityCommentListEventHandler(objectId, objectType, state)

    @Test
    fun `on CommentAdded for matching object, then call onCommentAdded`() {
        val comment = commentData(objectId = objectId, objectType = objectType)
        val event = StateUpdateEvent.CommentAdded(comment)

        handler.onEvent(event)

        verify { state.onCommentAdded(ThreadedCommentData(comment)) }
    }

    @Test
    fun `on CommentAdded for different object, then do not call onCommentAdded`() {
        val comment = commentData(objectId = "different-activity", objectType = objectType)
        val event = StateUpdateEvent.CommentAdded(comment)

        handler.onEvent(event)

        verify(exactly = 0) { state.onCommentAdded(any()) }
    }

    @Test
    fun `on CommentDeleted for matching object, then call onCommentRemoved`() {
        val comment = commentData(objectId = objectId, objectType = objectType)
        val event = StateUpdateEvent.CommentDeleted(comment)

        handler.onEvent(event)

        verify { state.onCommentRemoved(comment.id) }
    }

    @Test
    fun `on CommentUpdated for matching object, then call onCommentUpdated`() {
        val comment = commentData(objectId = objectId, objectType = objectType)
        val event = StateUpdateEvent.CommentUpdated(comment)

        handler.onEvent(event)

        verify { state.onCommentUpdated(comment) }
    }

    @Test
    fun `on CommentReactionAdded for matching object, then call onCommentReactionAdded`() {
        val comment = commentData(objectId = objectId, objectType = objectType)
        val reaction = feedsReactionData()
        val event = StateUpdateEvent.CommentReactionAdded(comment, reaction)

        handler.onEvent(event)

        verify { state.onCommentReactionAdded(comment.id, reaction) }
    }

    @Test
    fun `on CommentReactionDeleted for matching object, then call onCommentReactionRemoved`() {
        val comment = commentData(objectId = objectId, objectType = objectType)
        val reaction = feedsReactionData()
        val event = StateUpdateEvent.CommentReactionDeleted(comment, reaction)

        handler.onEvent(event)

        verify { state.onCommentReactionRemoved(comment.id, reaction) }
    }
}
