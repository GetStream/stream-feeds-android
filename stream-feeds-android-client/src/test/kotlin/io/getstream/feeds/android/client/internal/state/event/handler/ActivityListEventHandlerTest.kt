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
import io.getstream.feeds.android.client.internal.state.ActivityListStateUpdates
import io.getstream.feeds.android.client.internal.test.TestData.activityResponse
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkResponse
import io.getstream.feeds.android.client.internal.test.TestData.commentResponse
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionResponse
import io.getstream.feeds.android.network.models.ActivityDeletedEvent
import io.getstream.feeds.android.network.models.ActivityReactionAddedEvent
import io.getstream.feeds.android.network.models.ActivityReactionDeletedEvent
import io.getstream.feeds.android.network.models.BookmarkAddedEvent
import io.getstream.feeds.android.network.models.BookmarkDeletedEvent
import io.getstream.feeds.android.network.models.CommentAddedEvent
import io.getstream.feeds.android.network.models.CommentDeletedEvent
import io.getstream.feeds.android.network.models.WSEvent
import io.mockk.called
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import java.util.Date

internal class ActivityListEventHandlerTest {
    private val state: ActivityListStateUpdates = mockk(relaxed = true)

    private val handler = ActivityListEventHandler(state)

    @Test
    fun `on ActivityDeletedEvent, then call onActivityRemoved`() {
        val activity = activityResponse()
        val event = ActivityDeletedEvent(
            createdAt = Date(),
            fid = "user:feed-1",
            activity = activity,
            type = "feeds.activity.deleted"
        )

        handler.onEvent(event)

        verify { state.onActivityRemoved(activity.toModel()) }
    }

    @Test
    fun `on ActivityReactionAddedEvent, then call onReactionAdded`() {
        val activity = activityResponse()
        val reaction = feedsReactionResponse()
        val event = ActivityReactionAddedEvent(
            createdAt = Date(),
            fid = "user:feed-1",
            activity = activity,
            reaction = reaction,
            type = "feeds.activity.reaction.added"
        )

        handler.onEvent(event)

        verify { state.onReactionAdded(reaction.toModel()) }
    }

    @Test
    fun `on ActivityReactionDeletedEvent, then call onReactionRemoved`() {
        val activity = activityResponse()
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
    fun `on BookmarkAddedEvent, then call onBookmarkAdded`() {
        val bookmark = bookmarkResponse()
        val event = BookmarkAddedEvent(
            createdAt = Date(),
            bookmark = bookmark,
            type = "feeds.bookmark.added"
        )

        handler.onEvent(event)

        verify { state.onBookmarkAdded(bookmark.toModel()) }
    }

    @Test
    fun `on BookmarkDeletedEvent, then call onBookmarkRemoved`() {
        val bookmark = bookmarkResponse()
        val event = BookmarkDeletedEvent(
            createdAt = Date(),
            bookmark = bookmark,
            type = "feeds.bookmark.deleted"
        )

        handler.onEvent(event)

        verify { state.onBookmarkRemoved(bookmark.toModel()) }
    }

    @Test
    fun `on CommentAddedEvent, then call onCommentAdded`() {
        val comment = commentResponse()
        val event = CommentAddedEvent(
            createdAt = Date(),
            fid = "user:feed-1",
            comment = comment,
            activity = activityResponse(),
            type = "feeds.comment.added"
        )

        handler.onEvent(event)

        verify { state.onCommentAdded(comment.toModel()) }
    }

    @Test
    fun `on CommentDeletedEvent, then call onCommentRemoved`() {
        val comment = commentResponse()
        val event = CommentDeletedEvent(
            createdAt = Date(),
            fid = "user:feed-1",
            comment = comment,
            type = "feeds.comment.deleted"
        )

        handler.onEvent(event)

        verify { state.onCommentRemoved(comment.toModel()) }
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
