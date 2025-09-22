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

import io.getstream.feeds.android.client.api.model.ActivityPinData
import io.getstream.feeds.android.client.api.model.AggregatedActivityData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.internal.state.FeedStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.feedData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.getstream.feeds.android.client.internal.test.TestData.followData
import io.getstream.feeds.android.client.internal.test.TestData.pollData
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteData
import io.getstream.feeds.android.network.models.NotificationStatusResponse
import io.mockk.called
import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import java.util.Date
import org.junit.Test

internal class FeedEventHandlerTest {
    private val fid = FeedId("group", "feed-1")
    private val state: FeedStateUpdates = mockk(relaxed = true)

    private val handler = FeedEventHandler(fid, state)

    @Test
    fun `on ActivityAdded, then handle based on feed match`() {
        val activity = activityData()
        val matchingEvent = StateUpdateEvent.ActivityAdded(fid.rawValue, activity)
        val nonMatchingEvent = StateUpdateEvent.ActivityAdded("group:different", activity)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onActivityAdded(activity) },
        )
    }

    @Test
    fun `on ActivityRemovedFromFeed, then handle based on feed match`() {
        val activityId = "activity-1"
        val matchingEvent = StateUpdateEvent.ActivityRemovedFromFeed(fid.rawValue, activityId)
        val nonMatchingEvent =
            StateUpdateEvent.ActivityRemovedFromFeed("group:different", activityId)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onActivityRemoved(activityId) },
        )
    }

    @Test
    fun `on ActivityDeleted, then handle based on feed match`() {
        val matchingEvent = StateUpdateEvent.ActivityDeleted(fid.rawValue, "activity-1")
        val nonMatchingEvent = StateUpdateEvent.ActivityDeleted("group:different", "activity-1")

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onActivityRemoved("activity-1") },
        )
    }

    @Test
    fun `on ActivityUpdated, then handle based on feed match`() {
        val activity = activityData()
        val matchingEvent = StateUpdateEvent.ActivityUpdated(fid.rawValue, activity)
        val nonMatchingEvent = StateUpdateEvent.ActivityUpdated("group:different", activity)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onActivityUpdated(activity) },
        )
    }

    @Test
    fun `on ActivityReactionAdded, then handle based on feed match`() {
        val reaction = feedsReactionData("activity-1")
        val matchingEvent = StateUpdateEvent.ActivityReactionAdded(fid.rawValue, reaction)
        val nonMatchingEvent = StateUpdateEvent.ActivityReactionAdded("group:different", reaction)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onReactionAdded(reaction) },
        )
    }

    @Test
    fun `on ActivityReactionDeleted, then handle based on feed match`() {
        val reaction = feedsReactionData("activity-1")
        val matchingEvent = StateUpdateEvent.ActivityReactionDeleted(fid.rawValue, reaction)
        val nonMatchingEvent = StateUpdateEvent.ActivityReactionDeleted("group:different", reaction)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onReactionRemoved(reaction) },
        )
    }

    @Test
    fun `on ActivityPinned, then handle based on feed match`() {
        val activity = activityData()
        val pinnedActivity =
            ActivityPinData(
                activity = activity,
                createdAt = Date(),
                fid = fid,
                updatedAt = Date(),
                userId = "user-1",
            )
        val matchingEvent = StateUpdateEvent.ActivityPinned(fid.rawValue, pinnedActivity)
        val nonMatchingEvent = StateUpdateEvent.ActivityPinned("group:different", pinnedActivity)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onActivityPinned(pinnedActivity) },
        )
    }

    @Test
    fun `on ActivityUnpinned, then handle based on feed match`() {
        val activityId = "activity-1"
        val matchingEvent = StateUpdateEvent.ActivityUnpinned(fid.rawValue, activityId)
        val nonMatchingEvent = StateUpdateEvent.ActivityUnpinned("group:different", activityId)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onActivityUnpinned(activityId) },
        )
    }

    @Test
    fun `on BookmarkAdded, then handle based on activity feed match`() {
        val matchingActivity = activityData().copy(feeds = listOf(fid.rawValue, "other:feed"))
        val matchingBookmark = bookmarkData().copy(activity = matchingActivity)
        val matchingEvent = StateUpdateEvent.BookmarkAdded(matchingBookmark)

        val nonMatchingActivity = activityData().copy(feeds = listOf("other:feed", "another:feed"))
        val nonMatchingBookmark = bookmarkData().copy(activity = nonMatchingActivity)
        val nonMatchingEvent = StateUpdateEvent.BookmarkAdded(nonMatchingBookmark)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onBookmarkAdded(matchingBookmark) },
        )
    }

    @Test
    fun `on BookmarkDeleted, then handle based on activity feed match`() {
        val matchingActivity = activityData().copy(feeds = listOf(fid.rawValue))
        val matchingBookmark = bookmarkData().copy(activity = matchingActivity)
        val matchingEvent = StateUpdateEvent.BookmarkDeleted(matchingBookmark)

        val nonMatchingActivity = activityData().copy(feeds = listOf("other:feed"))
        val nonMatchingBookmark = bookmarkData().copy(activity = nonMatchingActivity)
        val nonMatchingEvent = StateUpdateEvent.BookmarkDeleted(nonMatchingBookmark)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onBookmarkRemoved(matchingBookmark) },
        )
    }

    @Test
    fun `on CommentAdded, then handle based on feed match`() {
        val comment = commentData()
        val matchingEvent = StateUpdateEvent.CommentAdded(fid.rawValue, comment)
        val nonMatchingEvent = StateUpdateEvent.CommentAdded("group:different", comment)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onCommentAdded(comment) },
        )
    }

    @Test
    fun `on CommentDeleted, then handle based on feed match`() {
        val comment = commentData()
        val matchingEvent = StateUpdateEvent.CommentDeleted(fid.rawValue, comment)
        val nonMatchingEvent = StateUpdateEvent.CommentDeleted("group:different", comment)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onCommentRemoved(comment) },
        )
    }

    @Test
    fun `on FeedDeleted, then handle based on feed match`() {
        val matchingEvent = StateUpdateEvent.FeedDeleted(fid.rawValue)
        val nonMatchingEvent = StateUpdateEvent.FeedDeleted("group:different")

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onFeedDeleted() },
        )
    }

    @Test
    fun `on FeedUpdated, then handle based on feed match`() {
        val matchingFeed = feedData(id = fid.id, groupId = fid.group)
        val matchingEvent = StateUpdateEvent.FeedUpdated(matchingFeed)

        val nonMatchingFeed = feedData(id = "group:different", groupId = "group")
        val nonMatchingEvent = StateUpdateEvent.FeedUpdated(nonMatchingFeed)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onFeedUpdated(matchingFeed) },
        )
    }

    @Test
    fun `on FollowAdded, then handle based on feed match`() {
        val matchingFollow = followData(sourceFid = fid.rawValue)
        val matchingEvent = StateUpdateEvent.FollowAdded(matchingFollow)

        val nonMatchingFollow = followData(sourceFid = "other:feed", targetFid = "another:feed")
        val nonMatchingEvent = StateUpdateEvent.FollowAdded(nonMatchingFollow)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onFollowAdded(matchingFollow) },
        )
    }

    @Test
    fun `on FollowDeleted, then handle based on feed match`() {
        val matchingFollow = followData(sourceFid = fid.rawValue)
        val matchingEvent = StateUpdateEvent.FollowDeleted(matchingFollow)

        val nonMatchingFollow = followData(sourceFid = "other:feed", targetFid = "another:feed")
        val nonMatchingEvent = StateUpdateEvent.FollowDeleted(nonMatchingFollow)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onFollowRemoved(matchingFollow) },
        )
    }

    @Test
    fun `on FollowUpdated, then handle based on feed match`() {
        val matchingFollow = followData(sourceFid = fid.rawValue)
        val matchingEvent = StateUpdateEvent.FollowUpdated(matchingFollow)

        val nonMatchingFollow = followData(sourceFid = "other:feed", targetFid = "another:feed")
        val nonMatchingEvent = StateUpdateEvent.FollowUpdated(nonMatchingFollow)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onFollowUpdated(matchingFollow) },
        )
    }

    @Test
    fun `on NotificationFeedUpdated, then handle based on feed match`() {
        val activity = activityData()
        val aggregatedActivity =
            AggregatedActivityData(
                activities = listOf(activity),
                activityCount = 1,
                createdAt = Date(),
                group = "test-group",
                score = 1.0f,
                updatedAt = Date(),
                userCount = 1,
                userCountTruncated = false,
            )
        val aggregatedActivities = listOf(aggregatedActivity)
        val notificationStatus = NotificationStatusResponse(unread = 0, unseen = 1)
        val matchingEvent =
            StateUpdateEvent.NotificationFeedUpdated(
                fid.rawValue,
                aggregatedActivities,
                notificationStatus,
            )
        val nonMatchingEvent =
            StateUpdateEvent.NotificationFeedUpdated(
                "group:different",
                aggregatedActivities,
                notificationStatus,
            )

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = {
                state.onNotificationFeedUpdated(aggregatedActivities, notificationStatus)
            },
        )
    }

    @Test
    fun `on PollClosed, then handle based on feed match`() {
        val poll = pollData()
        val matchingEvent = StateUpdateEvent.PollClosed(fid.rawValue, poll)
        val nonMatchingEvent = StateUpdateEvent.PollClosed("group:different", poll)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onPollClosed(poll.id) },
        )
    }

    @Test
    fun `on PollDeleted, then handle based on feed match`() {
        val pollId = "poll-1"
        val matchingEvent = StateUpdateEvent.PollDeleted(fid.rawValue, pollId)
        val nonMatchingEvent = StateUpdateEvent.PollDeleted("group:different", pollId)

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
        val nonMatchingEvent = StateUpdateEvent.PollUpdated("group:different", poll)

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
        val nonMatchingEvent = StateUpdateEvent.PollVoteCasted("group:different", pollId, pollVote)

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
        val nonMatchingEvent = StateUpdateEvent.PollVoteChanged("group:different", pollId, pollVote)

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
        val nonMatchingEvent = StateUpdateEvent.PollVoteRemoved("group:different", pollId, pollVote)

        testEventHandling(
            matchingEvent = matchingEvent,
            nonMatchingEvent = nonMatchingEvent,
            verifyBlock = { state.onPollVoteRemoved(pollVote, pollId) },
        )
    }

    @Test
    fun `on unknown event, then do nothing`() {
        val unknownEvent =
            StateUpdateEvent.FeedMemberAdded(
                "other:feed",
                io.getstream.feeds.android.client.internal.test.TestData.feedMemberData(),
            )

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
