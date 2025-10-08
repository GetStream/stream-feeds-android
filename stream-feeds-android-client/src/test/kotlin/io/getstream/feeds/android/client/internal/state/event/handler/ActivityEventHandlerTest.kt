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
import io.getstream.feeds.android.client.internal.state.ActivityStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.getstream.feeds.android.client.internal.test.TestData.pollData
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteData
import io.mockk.called
import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

internal class ActivityEventHandlerTest {

    private val fid = FeedId("user", "activity-1")
    private val differentFid = "user:different-activity"
    private val state: ActivityStateUpdates = mockk(relaxed = true)
    private val activityId = "test-activity-id"
    private val handler = ActivityEventHandler(fid, activityId, state)

    @Test
    fun `on ActivityReactionAdded, then handle based on feed and activity match`() {
        val activity = activityData(activityId)
        val reaction = feedsReactionData(activityId)
        val matchingEvent = StateUpdateEvent.ActivityReactionAdded(fid.rawValue, activity, reaction)
        val nonMatchingEvent =
            StateUpdateEvent.ActivityReactionAdded(differentFid, activity, reaction)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onReactionUpserted(reaction, activity) },
        )
    }

    @Test
    fun `on ActivityReactionUpdated, then handle based on feed and activity match`() {
        val activity = activityData(activityId)
        val reaction = feedsReactionData(activityId)
        val matchingEvent =
            StateUpdateEvent.ActivityReactionUpdated(fid.rawValue, activity, reaction)
        val nonMatchingEvent =
            StateUpdateEvent.ActivityReactionUpdated(differentFid, activity, reaction)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onReactionUpserted(reaction, activity) },
        )
    }

    @Test
    fun `on ActivityReactionDeleted, then handle based on feed and activity match`() {
        val activity = activityData(activityId)
        val reaction = feedsReactionData(activityId)
        val matchingEvent =
            StateUpdateEvent.ActivityReactionDeleted(fid.rawValue, activity, reaction)
        val nonMatchingEvent =
            StateUpdateEvent.ActivityReactionDeleted(differentFid, activity, reaction)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onReactionRemoved(reaction, activity) },
        )
    }

    @Test
    fun `on ActivityUpdated, then handle based on feed and activity match`() {
        val activity = activityData(activityId)
        val matchingEvent = StateUpdateEvent.ActivityUpdated(fid.rawValue, activity)
        val nonMatchingEvent = StateUpdateEvent.ActivityUpdated(differentFid, activity)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onActivityUpdated(activity) },
        )
    }

    @Test
    fun `on BookmarkAdded, then handle based on feed and activity match`() {
        val matchingActivity = activityData(activityId).copy(feeds = listOf(fid.rawValue))
        val matchingBookmark = bookmarkData().copy(activity = matchingActivity)
        val matchingEvent = StateUpdateEvent.BookmarkAdded(matchingBookmark)

        val nonMatchingActivity = matchingActivity.copy(feeds = listOf(differentFid))
        val nonMatchingBookmark = matchingBookmark.copy(activity = nonMatchingActivity)
        val nonMatchingEvent = StateUpdateEvent.BookmarkAdded(nonMatchingBookmark)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onBookmarkUpserted(matchingBookmark) },
        )
    }

    @Test
    fun `on BookmarkUpdated, then handle based on feed and activity match`() {
        val matchingActivity = activityData(activityId).copy(feeds = listOf(fid.rawValue))
        val matchingBookmark = bookmarkData().copy(activity = matchingActivity)
        val matchingEvent = StateUpdateEvent.BookmarkUpdated(matchingBookmark)

        val nonMatchingActivity = matchingActivity.copy(feeds = listOf(differentFid))
        val nonMatchingBookmark = matchingBookmark.copy(activity = nonMatchingActivity)
        val nonMatchingEvent = StateUpdateEvent.BookmarkUpdated(nonMatchingBookmark)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onBookmarkUpserted(matchingBookmark) },
        )
    }

    @Test
    fun `on BookmarkDeleted, then handle based on feed and activity match`() {
        val matchingActivity = activityData(activityId).copy(feeds = listOf(fid.rawValue))
        val matchingBookmark = bookmarkData().copy(activity = matchingActivity)
        val matchingEvent = StateUpdateEvent.BookmarkDeleted(matchingBookmark)

        val nonMatchingActivity = matchingActivity.copy(feeds = listOf(differentFid))
        val nonMatchingBookmark = matchingBookmark.copy(activity = nonMatchingActivity)
        val nonMatchingEvent = StateUpdateEvent.BookmarkDeleted(nonMatchingBookmark)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onBookmarkRemoved(matchingBookmark) },
        )
    }

    @Test
    fun `on PollClosed, then handle based on feed match`() {
        val poll = pollData()
        val matchingEvent = StateUpdateEvent.PollClosed(fid.rawValue, poll)
        val nonMatchingEvent = StateUpdateEvent.PollClosed(differentFid, poll)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onPollClosed(poll) },
        )
    }

    @Test
    fun `on PollDeleted, then handle based on feed match`() {
        val pollId = "poll-1"
        val matchingEvent = StateUpdateEvent.PollDeleted(fid.rawValue, pollId)
        val nonMatchingEvent = StateUpdateEvent.PollDeleted(differentFid, pollId)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onPollDeleted(pollId) },
        )
    }

    @Test
    fun `on PollUpdated, then handle based on feed match`() {
        val poll = pollData()
        val matchingEvent = StateUpdateEvent.PollUpdated(fid.rawValue, poll)
        val nonMatchingEvent = StateUpdateEvent.PollUpdated(differentFid, poll)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onPollUpdated(poll) },
        )
    }

    @Test
    fun `on PollVoteCasted, then handle based on feed match`() {
        val pollId = "poll-1"
        val pollVote = pollVoteData()
        val matchingEvent = StateUpdateEvent.PollVoteCasted(fid.rawValue, pollId, pollVote)
        val nonMatchingEvent = StateUpdateEvent.PollVoteCasted(differentFid, pollId, pollVote)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onPollVoteCasted(pollVote, pollId) },
        )
    }

    @Test
    fun `on PollVoteChanged, then handle based on feed match`() {
        val pollId = "poll-1"
        val pollVote = pollVoteData()
        val matchingEvent = StateUpdateEvent.PollVoteChanged(fid.rawValue, pollId, pollVote)
        val nonMatchingEvent = StateUpdateEvent.PollVoteChanged(differentFid, pollId, pollVote)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onPollVoteChanged(pollVote, pollId) },
        )
    }

    @Test
    fun `on PollVoteRemoved, then handle based on feed match`() {
        val pollId = "poll-1"
        val pollVote = pollVoteData()
        val matchingEvent = StateUpdateEvent.PollVoteRemoved(fid.rawValue, pollId, pollVote)
        val nonMatchingEvent = StateUpdateEvent.PollVoteRemoved(differentFid, pollId, pollVote)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onPollVoteRemoved(pollVote, pollId) },
        )
    }

    @Test
    fun `on unknown event, then do nothing`() {
        val unknownEvent = StateUpdateEvent.FeedDeleted("feed-id")

        handler.onEvent(unknownEvent)

        verify { state wasNot called }
    }

    private fun testEventHandling(
        matchingEvent: StateUpdateEvent,
        nonMatchingEvent: StateUpdateEvent,
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
