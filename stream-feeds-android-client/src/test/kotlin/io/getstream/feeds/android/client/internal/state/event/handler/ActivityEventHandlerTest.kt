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
import io.getstream.feeds.android.client.internal.state.ActivityStateUpdates
import io.getstream.feeds.android.client.internal.test.TestData.activityResponse
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkResponse
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionResponse
import io.getstream.feeds.android.client.internal.test.TestData.pollResponseData
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteResponseData
import io.getstream.feeds.android.network.models.ActivityReactionAddedEvent
import io.getstream.feeds.android.network.models.ActivityReactionDeletedEvent
import io.getstream.feeds.android.network.models.ActivityUpdatedEvent
import io.getstream.feeds.android.network.models.BookmarkAddedEvent
import io.getstream.feeds.android.network.models.BookmarkDeletedEvent
import io.getstream.feeds.android.network.models.PollClosedFeedEvent
import io.getstream.feeds.android.network.models.PollDeletedFeedEvent
import io.getstream.feeds.android.network.models.PollUpdatedFeedEvent
import io.getstream.feeds.android.network.models.PollVoteCastedFeedEvent
import io.getstream.feeds.android.network.models.PollVoteChangedFeedEvent
import io.getstream.feeds.android.network.models.PollVoteRemovedFeedEvent
import io.getstream.feeds.android.network.models.WSEvent
import io.mockk.called
import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import java.util.Date
import org.junit.Test

internal class ActivityEventHandlerTest {

    private val fid = FeedId("user", "activity-1")
    private val differentFid = "user:different-activity"
    private val state: ActivityStateUpdates = mockk(relaxed = true)
    private val activityId = "test-activity-id"
    private val handler = ActivityEventHandler(fid, activityId, state)

    @Test
    fun `on ActivityReactionAddedEvent, then handle based on feed and activity match`() {
        val activity = activityResponse(activityId)
        val reaction = feedsReactionResponse()
        val matchingEvent =
            ActivityReactionAddedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                activity = activity,
                reaction = reaction,
                type = "activity.reaction.added",
            )
        val nonMatchingEvent = matchingEvent.copy(fid = differentFid)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onReactionAdded(reaction.toModel()) },
        )
    }

    @Test
    fun `on ActivityReactionDeletedEvent, then handle based on feed and activity match`() {
        val activity = activityResponse(activityId)
        val reaction = feedsReactionResponse()
        val matchingEvent =
            ActivityReactionDeletedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                activity = activity,
                reaction = reaction,
                type = "activity.reaction.deleted",
            )
        val nonMatchingEvent = matchingEvent.copy(fid = differentFid)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onReactionRemoved(reaction.toModel()) },
        )
    }

    @Test
    fun `on ActivityUpdatedEvent, then handle based on feed and activity match`() {
        val activity = activityResponse(activityId)
        val matchingEvent =
            ActivityUpdatedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                activity = activity,
                type = "activity.updated",
            )
        val nonMatchingEvent = matchingEvent.copy(fid = differentFid)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onActivityUpdated(activity.toModel()) },
        )
    }

    @Test
    fun `on BookmarkAddedEvent, then handle based on feed and activity match`() {
        val bookmark = bookmarkResponse()
        val matchingActivity = bookmark.activity.copy(feeds = listOf(fid.rawValue), id = activityId)
        val matchingBookmark = bookmark.copy(activity = matchingActivity)
        val matchingEvent =
            BookmarkAddedEvent(
                createdAt = Date(),
                bookmark = matchingBookmark,
                type = "bookmark.added",
            )
        val nonMatchingActivity = matchingActivity.copy(feeds = listOf(differentFid))
        val nonMatchingBookmark = matchingBookmark.copy(activity = nonMatchingActivity)
        val nonMatchingEvent = matchingEvent.copy(bookmark = nonMatchingBookmark)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onBookmarkAdded(matchingBookmark.toModel()) },
        )
    }

    @Test
    fun `on BookmarkDeletedEvent, then handle based on feed and activity match`() {
        val bookmark = bookmarkResponse()
        val matchingActivity = bookmark.activity.copy(feeds = listOf(fid.rawValue), id = activityId)
        val matchingBookmark = bookmark.copy(activity = matchingActivity)
        val matchingEvent =
            BookmarkDeletedEvent(
                createdAt = Date(),
                bookmark = matchingBookmark,
                type = "bookmark.deleted",
            )
        val nonMatchingActivity = matchingActivity.copy(feeds = listOf(differentFid))
        val nonMatchingBookmark = matchingBookmark.copy(activity = nonMatchingActivity)
        val nonMatchingEvent = matchingEvent.copy(bookmark = nonMatchingBookmark)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onBookmarkRemoved(matchingBookmark.toModel()) },
        )
    }

    @Test
    fun `on PollClosedFeedEvent, then handle based on feed match`() {
        val poll = pollResponseData()
        val matchingEvent =
            PollClosedFeedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                poll = poll,
                type = "feeds.poll.closed",
            )
        val nonMatchingEvent = matchingEvent.copy(fid = differentFid)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onPollClosed(poll.toModel()) },
        )
    }

    @Test
    fun `on PollDeletedFeedEvent, then handle based on feed match`() {
        val poll = pollResponseData()
        val matchingEvent =
            PollDeletedFeedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                poll = poll,
                type = "feeds.poll.deleted",
            )
        val nonMatchingEvent = matchingEvent.copy(fid = differentFid)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onPollDeleted(poll.id) },
        )
    }

    @Test
    fun `on PollUpdatedFeedEvent, then handle based on feed match`() {
        val poll = pollResponseData()
        val matchingEvent =
            PollUpdatedFeedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                poll = poll,
                type = "feeds.poll.updated",
            )
        val nonMatchingEvent = matchingEvent.copy(fid = differentFid)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onPollUpdated(poll.toModel()) },
        )
    }

    @Test
    fun `on PollVoteCastedFeedEvent, then handle based on feed match`() {
        val poll = pollResponseData()
        val pollVote = pollVoteResponseData()
        val matchingEvent =
            PollVoteCastedFeedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                poll = poll,
                pollVote = pollVote,
                type = "feeds.poll.vote.casted",
            )
        val nonMatchingEvent = matchingEvent.copy(fid = differentFid)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onPollVoteCasted(pollVote.toModel(), poll.id) },
        )
    }

    @Test
    fun `on PollVoteChangedFeedEvent, then handle based on feed match`() {
        val poll = pollResponseData()
        val pollVote = pollVoteResponseData()
        val matchingEvent =
            PollVoteChangedFeedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                poll = poll,
                pollVote = pollVote,
                type = "feeds.poll.vote.changed",
            )
        val nonMatchingEvent = matchingEvent.copy(fid = differentFid)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onPollVoteChanged(pollVote.toModel(), poll.id) },
        )
    }

    @Test
    fun `on PollVoteRemovedFeedEvent, then handle based on feed match`() {
        val poll = pollResponseData()
        val pollVote = pollVoteResponseData()
        val matchingEvent =
            PollVoteRemovedFeedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                poll = poll,
                pollVote = pollVote,
                type = "feeds.poll.vote.removed",
            )
        val nonMatchingEvent = matchingEvent.copy(fid = differentFid)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onPollVoteRemoved(pollVote.toModel(), poll.id) },
        )
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

    private fun testEventHandling(
        matchingEvent: WSEvent,
        nonMatchingEvent: WSEvent,
        verifyBlock: () -> Unit,
    ) {
        // Test matching event
        handler.onEvent(matchingEvent)
        verify { verifyBlock() }

        // Reset mock for clean verification
        clearMocks(state)

        // Test non-matching event
        handler.onEvent(nonMatchingEvent)
        verify { state wasNot called }
    }
}
