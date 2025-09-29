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

import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.AggregatedActivityData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.internal.state.FeedStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityAdded
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityPinned
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityReactionAdded
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityReactionDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityReactionUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityRemovedFromFeed
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityUnpinned
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkAdded
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentAdded
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentReactionAdded
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentReactionDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentReactionUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.FeedDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.FeedUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.FollowAdded
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.FollowDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.FollowUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.NotificationFeedUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollVoteCasted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollVoteChanged
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollVoteRemoved
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.activityPin
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.feedData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.getstream.feeds.android.client.internal.test.TestData.followData
import io.getstream.feeds.android.client.internal.test.TestData.pollData
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteData
import io.getstream.feeds.android.network.models.NotificationStatusResponse
import io.mockk.MockKVerificationScope
import io.mockk.called
import io.mockk.mockk
import java.util.Date
import org.junit.runners.Parameterized

internal class FeedEventHandlerTest(
    testName: String,
    event: StateUpdateEvent,
    verifyBlock: MockKVerificationScope.(FeedStateUpdates) -> Unit,
) : BaseEventHandlerTest<FeedStateUpdates>(testName, event, verifyBlock) {

    override val state: FeedStateUpdates = mockk(relaxed = true)
    override val handler = FeedEventHandler(fid, state)

    companion object {
        private val fid = FeedId("group", "feed-1")
        private const val activityId = "activity-1"
        private val activity = activityData(activityId)
        private val reaction = feedsReactionData(activityId)
        private val matchingBookmark =
            bookmarkData(activityData(feeds = listOf(fid.rawValue, "other:feed")))
        private val nonMatchingBookmark =
            bookmarkData(activityData(feeds = listOf("other:feed", "another:feed")))
        private val comment = commentData()
        private val commentReaction = feedsReactionData()
        private val matchingFeed = feedData(id = fid.id, groupId = fid.group)
        private val nonMatchingFeed = feedData(id = "group:different", groupId = "group")
        private val matchingFollow = followData(sourceFid = fid.rawValue)
        private val nonMatchingFollow =
            followData(sourceFid = "other:feed", targetFid = "another:feed")
        private val aggregatedActivities =
            listOf(
                AggregatedActivityData(
                    activities = listOf<ActivityData>(activity),
                    activityCount = 1,
                    createdAt = Date(),
                    group = "test-group",
                    score = 1.0f,
                    updatedAt = Date(),
                    userCount = 1,
                    userCountTruncated = false,
                )
            )
        private val notificationStatus = NotificationStatusResponse(unread = 0, unseen = 1)
        private const val pollId = "poll-1"
        private val poll = pollData(pollId)
        private val pollVote = pollVoteData()

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): List<Array<Any>> =
            listOf(
                testParams<FeedStateUpdates>(
                    name = "ActivityAdded matching feed",
                    event = ActivityAdded(fid.rawValue, activity),
                    verifyBlock = { state -> state.onActivityAdded(activity) },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityAdded non-matching feed",
                    event = ActivityAdded("group:different", activity),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityRemovedFromFeed matching feed",
                    event = ActivityRemovedFromFeed(fid.rawValue, activityId),
                    verifyBlock = { state -> state.onActivityRemoved(activityId) },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityRemovedFromFeed non-matching feed",
                    event = ActivityRemovedFromFeed("group:different", activityId),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityDeleted matching feed",
                    event = ActivityDeleted(fid.rawValue, activityId),
                    verifyBlock = { state -> state.onActivityRemoved(activityId) },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityDeleted non-matching feed",
                    event = ActivityDeleted("group:different", activityId),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityReactionAdded matching feed",
                    event = ActivityReactionAdded(fid.rawValue, activity, reaction),
                    verifyBlock = { state -> state.onReactionUpserted(reaction, activity) },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityReactionAdded non-matching feed",
                    event = ActivityReactionAdded("group:different", activity, reaction),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityReactionDeleted matching feed",
                    event = ActivityReactionDeleted(fid.rawValue, activity, reaction),
                    verifyBlock = { state -> state.onReactionRemoved(reaction, activity) },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityReactionDeleted non-matching feed",
                    event = ActivityReactionDeleted("group:different", activity, reaction),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityReactionUpdated matching feed",
                    event = ActivityReactionUpdated(fid.rawValue, activity, reaction),
                    verifyBlock = { state -> state.onReactionUpserted(reaction, activity) },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityReactionUpdated non-matching feed",
                    event = ActivityReactionUpdated("group:different", activity, reaction),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityUpdated matching feed",
                    event = ActivityUpdated(fid.rawValue, activity),
                    verifyBlock = { state -> state.onActivityUpdated(activity) },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityUpdated non-matching feed",
                    event = ActivityUpdated("group:different", activity),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityPinned matching feed",
                    event = ActivityPinned(fid.rawValue, activityPin(activity)),
                    verifyBlock = { state -> state.onActivityPinned(activityPin(activity)) },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityPinned non-matching feed",
                    event = ActivityPinned("group:different", activityPin(activity)),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityUnpinned matching feed",
                    event = ActivityUnpinned(fid.rawValue, activityId),
                    verifyBlock = { state -> state.onActivityUnpinned(activityId) },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityUnpinned non-matching feed",
                    event = ActivityUnpinned("group:different", activityId),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "BookmarkAdded matching feeds",
                    event = BookmarkAdded(matchingBookmark),
                    verifyBlock = { state -> state.onBookmarkUpserted(matchingBookmark) },
                ),
                testParams<FeedStateUpdates>(
                    name = "BookmarkAdded non-matching feeds",
                    event = BookmarkAdded(nonMatchingBookmark),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "BookmarkDeleted matching activity feeds",
                    event = BookmarkDeleted(matchingBookmark),
                    verifyBlock = { state -> state.onBookmarkRemoved(matchingBookmark) },
                ),
                testParams<FeedStateUpdates>(
                    name = "BookmarkDeleted non-matching activity feeds",
                    event = BookmarkDeleted(nonMatchingBookmark),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "BookmarkUpserted matching feeds",
                    event = BookmarkUpdated(matchingBookmark),
                    verifyBlock = { state -> state.onBookmarkUpserted(matchingBookmark) },
                ),
                testParams<FeedStateUpdates>(
                    name = "BookmarkUpserted non-matching feeds",
                    event = BookmarkUpdated(nonMatchingBookmark),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "CommentAdded matching feed",
                    event = CommentAdded(fid.rawValue, comment),
                    verifyBlock = { state -> state.onCommentAdded(comment) },
                ),
                testParams<FeedStateUpdates>(
                    name = "CommentAdded non-matching feed",
                    event = CommentAdded("group:different", comment),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "CommentDeleted matching feed",
                    event = CommentDeleted(fid.rawValue, comment),
                    verifyBlock = { state -> state.onCommentRemoved(comment) },
                ),
                testParams<FeedStateUpdates>(
                    name = "CommentDeleted non-matching feed",
                    event = CommentDeleted("group:different", comment),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "CommentReactionAdded matching feed",
                    event = CommentReactionAdded(fid.rawValue, comment, commentReaction),
                    verifyBlock = { state ->
                        state.onCommentReactionUpserted(comment, commentReaction)
                    },
                ),
                testParams<FeedStateUpdates>(
                    name = "CommentReactionAdded non-matching feed",
                    event = CommentReactionAdded("group:different", comment, commentReaction),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "CommentReactionDeleted matching feed",
                    event = CommentReactionDeleted(fid.rawValue, comment, commentReaction),
                    verifyBlock = { state ->
                        state.onCommentReactionRemoved(comment, commentReaction)
                    },
                ),
                testParams<FeedStateUpdates>(
                    name = "CommentReactionDeleted non-matching feed",
                    event = CommentReactionDeleted("group:different", comment, commentReaction),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "CommentReactionUpdated matching feed",
                    event = CommentReactionUpdated(fid.rawValue, comment, commentReaction),
                    verifyBlock = { state ->
                        state.onCommentReactionUpserted(comment, commentReaction)
                    },
                ),
                testParams<FeedStateUpdates>(
                    name = "CommentReactionUpdated non-matching feed",
                    event = CommentReactionUpdated("group:different", comment, commentReaction),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "FeedDeleted matching feed",
                    event = FeedDeleted(fid.rawValue),
                    verifyBlock = { state -> state.onFeedDeleted() },
                ),
                testParams<FeedStateUpdates>(
                    name = "FeedDeleted non-matching feed",
                    event = FeedDeleted("group:different"),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "FeedUpdated matching feed",
                    event = FeedUpdated(matchingFeed),
                    verifyBlock = { state -> state.onFeedUpdated(matchingFeed) },
                ),
                testParams<FeedStateUpdates>(
                    name = "FeedUpdated non-matching feed",
                    event = FeedUpdated(nonMatchingFeed),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "FollowAdded matching feed",
                    event = FollowAdded(matchingFollow),
                    verifyBlock = { state -> state.onFollowAdded(matchingFollow) },
                ),
                testParams<FeedStateUpdates>(
                    name = "FollowAdded non-matching feed",
                    event = FollowAdded(nonMatchingFollow),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "FollowDeleted matching feed",
                    event = FollowDeleted(matchingFollow),
                    verifyBlock = { state -> state.onFollowRemoved(matchingFollow) },
                ),
                testParams<FeedStateUpdates>(
                    name = "FollowDeleted non-matching feed",
                    event = FollowDeleted(nonMatchingFollow),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "FollowUpdated matching feed",
                    event = FollowUpdated(matchingFollow),
                    verifyBlock = { state -> state.onFollowUpdated(matchingFollow) },
                ),
                testParams<FeedStateUpdates>(
                    name = "FollowUpdated non-matching feed",
                    event = FollowUpdated(nonMatchingFollow),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "NotificationFeedUpdated matching feed",
                    event =
                        NotificationFeedUpdated(
                            fid.rawValue,
                            aggregatedActivities,
                            notificationStatus,
                        ),
                    verifyBlock = { state ->
                        state.onNotificationFeedUpdated(aggregatedActivities, notificationStatus)
                    },
                ),
                testParams<FeedStateUpdates>(
                    name = "NotificationFeedUpdated non-matching feed",
                    event =
                        NotificationFeedUpdated(
                            "group:different",
                            aggregatedActivities,
                            notificationStatus,
                        ),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "PollDeleted matching feed",
                    event = PollDeleted(fid.rawValue, pollId),
                    verifyBlock = { state -> state.onPollDeleted(pollId) },
                ),
                testParams<FeedStateUpdates>(
                    name = "PollDeleted non-matching feed",
                    event = PollDeleted("group:different", pollId),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "PollUpdated matching feed",
                    event = PollUpdated(fid.rawValue, poll),
                    verifyBlock = { state -> state.onPollUpdated(poll) },
                ),
                testParams<FeedStateUpdates>(
                    name = "PollUpdated non-matching feed",
                    event = PollUpdated("group:different", poll),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "PollVoteCasted matching feed",
                    event = PollVoteCasted(fid.rawValue, pollId, pollVote),
                    verifyBlock = { state -> state.onPollVoteCasted(pollVote, pollId) },
                ),
                testParams<FeedStateUpdates>(
                    name = "PollVoteCasted non-matching feed",
                    event = PollVoteCasted("group:different", pollId, pollVote),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "PollVoteChanged matching feed",
                    event = PollVoteChanged(fid.rawValue, pollId, pollVote),
                    verifyBlock = { state -> state.onPollVoteChanged(pollVote, pollId) },
                ),
                testParams<FeedStateUpdates>(
                    name = "PollVoteChanged non-matching feed",
                    event = PollVoteChanged("group:different", pollId, pollVote),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "PollVoteRemoved matching feed",
                    event = PollVoteRemoved(fid.rawValue, pollId, pollVote),
                    verifyBlock = { state -> state.onPollVoteRemoved(pollVote, pollId) },
                ),
                testParams<FeedStateUpdates>(
                    name = "PollVoteRemoved non-matching feed",
                    event = PollVoteRemoved("group:different", pollId, pollVote),
                    verifyBlock = { state -> state wasNot called },
                ),
            )
    }
}
