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

import io.getstream.feeds.android.client.internal.state.ActivityListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.mockk.called
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

internal class ActivityListEventHandlerTest {
    private val state: ActivityListStateUpdates = mockk(relaxed = true)

    private val handler = ActivityListEventHandler(state)

    @Test
    fun `on ActivityDeleted, then call onActivityRemoved`() {
        val activity = activityData()
        val event = StateUpdateEvent.ActivityDeleted(activity)

        handler.onEvent(event)

        verify { state.onActivityRemoved(activity) }
    }

    @Test
    fun `on ActivityReactionAdded, then call onReactionAdded`() {
        val reaction = feedsReactionData("activity-1")
        val event = StateUpdateEvent.ActivityReactionAdded(reaction)

        handler.onEvent(event)

        verify { state.onReactionAdded(reaction) }
    }

    @Test
    fun `on ActivityReactionDeleted, then call onReactionRemoved`() {
        val reaction = feedsReactionData("activity-1")
        val event = StateUpdateEvent.ActivityReactionDeleted(reaction)

        handler.onEvent(event)

        verify { state.onReactionRemoved(reaction) }
    }

    @Test
    fun `on BookmarkAdded, then call onBookmarkAdded`() {
        val bookmark = bookmarkData()
        val event = StateUpdateEvent.BookmarkAdded(bookmark)

        handler.onEvent(event)

        verify { state.onBookmarkAdded(bookmark) }
    }

    @Test
    fun `on BookmarkDeleted, then call onBookmarkRemoved`() {
        val bookmark = bookmarkData()
        val event = StateUpdateEvent.BookmarkDeleted(bookmark)

        handler.onEvent(event)

        verify { state.onBookmarkRemoved(bookmark) }
    }

    @Test
    fun `on CommentAdded, then call onCommentAdded`() {
        val comment = commentData()
        val event = StateUpdateEvent.CommentAdded(comment)

        handler.onEvent(event)

        verify { state.onCommentAdded(comment) }
    }

    @Test
    fun `on CommentDeleted, then call onCommentRemoved`() {
        val comment = commentData()
        val event = StateUpdateEvent.CommentDeleted(comment)

        handler.onEvent(event)

        verify { state.onCommentRemoved(comment) }
    }

    @Test
    fun `on unknown event, then do nothing`() {
        val unknownEvent = StateUpdateEvent.FeedDeleted("feed-id")

        handler.onEvent(unknownEvent)

        verify { state wasNot called }
    }
}
