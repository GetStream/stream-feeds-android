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
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.state.ActivityCommentListStateUpdates
import io.getstream.feeds.android.client.internal.test.TestData.activityResponse
import io.getstream.feeds.android.client.internal.test.TestData.commentResponse
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionResponse
import io.getstream.feeds.android.network.models.CommentAddedEvent
import io.getstream.feeds.android.network.models.CommentDeletedEvent
import io.getstream.feeds.android.network.models.CommentReactionAddedEvent
import io.getstream.feeds.android.network.models.CommentReactionDeletedEvent
import io.getstream.feeds.android.network.models.CommentUpdatedEvent
import io.getstream.feeds.android.network.models.WSEvent
import io.mockk.called
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import java.util.Date

internal class ActivityCommentListEventHandlerTest {
    private val objectId = "activity-1"
    private val objectType = "activity"
    private val state: ActivityCommentListStateUpdates = mockk(relaxed = true)

    private val handler = ActivityCommentListEventHandler(objectId, objectType, state)

    @Test
    fun `on CommentAddedEvent for matching object, then call onCommentAdded`() {
        val comment = commentResponse().copy(objectId = objectId, objectType = objectType)
        val event = CommentAddedEvent(
            createdAt = Date(),
            fid = "user:feed-1",
            comment = comment,
            activity = activityResponse(),
            type = "feeds.comment.added"
        )

        handler.onEvent(event)

        verify { state.onCommentAdded(ThreadedCommentData(comment.toModel())) }
    }

    @Test
    fun `on CommentAddedEvent for different object, then do not call onCommentAdded`() {
        val comment =
            commentResponse().copy(objectId = "different-activity", objectType = objectType)
        val event = CommentAddedEvent(
            createdAt = Date(),
            fid = "user:feed-1",
            comment = comment,
            activity = activityResponse(),
            type = "feeds.comment.added"
        )

        handler.onEvent(event)

        verify(exactly = 0) { state.onCommentAdded(any()) }
    }

    @Test
    fun `on CommentDeletedEvent for matching object, then call onCommentRemoved`() {
        val comment = commentResponse().copy(objectId = objectId, objectType = objectType)
        val event = CommentDeletedEvent(
            createdAt = Date(),
            fid = "user:feed-1",
            comment = comment,
            type = "feeds.comment.deleted"
        )

        handler.onEvent(event)

        verify { state.onCommentRemoved(comment.id) }
    }

    @Test
    fun `on CommentUpdatedEvent for matching object, then call onCommentUpdated`() {
        val comment = commentResponse().copy(objectId = objectId, objectType = objectType)
        val event = CommentUpdatedEvent(
            createdAt = Date(),
            fid = "user:feed-1",
            comment = comment,
            type = "feeds.comment.updated"
        )

        handler.onEvent(event)

        verify { state.onCommentUpdated(comment.toModel()) }
    }

    @Test
    fun `on CommentReactionAddedEvent for matching object, then call onCommentReactionAdded`() {
        val comment = commentResponse().copy(objectId = objectId, objectType = objectType)
        val reaction = feedsReactionResponse()
        val event = CommentReactionAddedEvent(
            createdAt = Date(),
            fid = "user:feed-1",
            activity = activityResponse(),
            comment = comment,
            reaction = reaction,
            type = "feeds.comment.reaction.added"
        )

        handler.onEvent(event)

        verify { state.onCommentReactionAdded(comment.id, reaction.toModel()) }
    }

    @Test
    fun `on CommentReactionDeletedEvent for matching object, then call onCommentReactionRemoved`() {
        val comment = commentResponse().copy(objectId = objectId, objectType = objectType)
        val reaction = feedsReactionResponse()
        val event = CommentReactionDeletedEvent(
            createdAt = Date(),
            fid = "user:feed-1",
            comment = comment,
            reaction = reaction,
            type = "feeds.comment.reaction.deleted"
        )

        handler.onEvent(event)

        verify { state.onCommentReactionRemoved(comment.id, reaction.toModel()) }
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
