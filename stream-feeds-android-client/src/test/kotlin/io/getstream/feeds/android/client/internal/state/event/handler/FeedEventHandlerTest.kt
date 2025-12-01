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

import io.getstream.android.core.api.filter.equal
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.AggregatedActivityData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.state.InsertionAction
import io.getstream.feeds.android.client.api.state.query.ActivitiesFilterField
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.internal.state.FeedStateUpdates
import io.getstream.feeds.android.client.internal.state.event.FidScope
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityAdded
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityHidden
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityPinned
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityReactionDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityReactionUpserted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityRemovedFromFeed
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityUnpinned
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkAdded
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentAdded
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentReactionDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentReactionUpserted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.FeedCapabilitiesUpdated
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
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.StoriesFeedUpdated
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.activityPin
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.feedData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.getstream.feeds.android.client.internal.test.TestData.followData
import io.getstream.feeds.android.client.internal.test.TestData.pollData
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteData
import io.getstream.feeds.android.network.models.FeedOwnCapability
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
    override val handler = FeedEventHandler(query, userId, ::defaultOnNewActivity, state)

    companion object {
        private val fid = FeedId("group", "feed-1")
        private val fidScope = FidScope.of(fid)
        private val otherFidScope = FidScope.of("group:different")
        private val query =
            FeedQuery(fid = fid, activityFilter = ActivitiesFilterField.type.equal("post"))
        private const val userId = "user-1"
        private const val activityId = "activity-1"
        private val activity = activityData(activityId, type = "post")
        private val nonMatchingActivity = activityData(activityId, type = "comment")
        private val reaction = feedsReactionData(activityId)
        private val matchingBookmark =
            bookmarkData(activityData(feeds = listOf(fid.rawValue, "other:feed")))
        private val comment = commentData()
        private val commentReaction = feedsReactionData()
        private val matchingFeed = feedData(id = fid.id, groupId = fid.group)
        private val nonMatchingFeed = feedData(id = "group:different", groupId = "group")
        private val matchingFollow = followData(sourceFid = fid.rawValue)
        private val nonMatchingFollow =
            followData(sourceFid = "other:feed", targetFid = "another:feed")
        private val activities = listOf(activity)
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
                    name = "ActivityAdded matching feed and filter",
                    event = ActivityAdded(fidScope, activity),
                    verifyBlock = { state ->
                        state.onActivityAdded(activity, InsertionAction.AddToStart)
                    },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityAdded non-matching feed",
                    event = ActivityAdded(otherFidScope, activity),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityAdded non-matching filter",
                    event = ActivityAdded(fidScope, nonMatchingActivity),
                    verifyBlock = { state ->
                        state.onActivityAdded(nonMatchingActivity, InsertionAction.Ignore)
                    },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityRemovedFromFeed matching feed",
                    event = ActivityRemovedFromFeed(fidScope, activityId),
                    verifyBlock = { state -> state.onActivityRemoved(activityId) },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityRemovedFromFeed non-matching feed",
                    event = ActivityRemovedFromFeed(otherFidScope, activityId),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityDeleted matching feed",
                    event = ActivityDeleted(fidScope, activityId),
                    verifyBlock = { state -> state.onActivityRemoved(activityId) },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityDeleted non-matching feed",
                    event = ActivityDeleted(otherFidScope, activityId),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityReactionDeleted matching feed",
                    event = ActivityReactionDeleted(fidScope, activity, reaction),
                    verifyBlock = { state -> state.onReactionRemoved(reaction, activity) },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityReactionDeleted non-matching feed",
                    event = ActivityReactionDeleted(otherFidScope, activity, reaction),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityReactionUpserted matching feed",
                    event = ActivityReactionUpserted(fidScope, activity, reaction, true),
                    verifyBlock = { state -> state.onReactionUpserted(reaction, activity, true) },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityReactionUpserted non-matching feed",
                    event = ActivityReactionUpserted(otherFidScope, activity, reaction, true),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityUpdated matching feed and filter",
                    event = ActivityUpdated(fidScope, activity),
                    verifyBlock = { state -> state.onActivityUpdated(activity) },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityUpdated non-matching feed",
                    event = ActivityUpdated(otherFidScope, activity),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityUpdated non-matching filter",
                    event = ActivityUpdated(fidScope, nonMatchingActivity),
                    verifyBlock = { state -> state.onActivityRemoved(nonMatchingActivity.id) },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityPinned matching feed",
                    event = ActivityPinned(fidScope, activityPin(activity)),
                    verifyBlock = { state -> state.onActivityPinned(activityPin(activity)) },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityPinned non-matching feed",
                    event = ActivityPinned(otherFidScope, activityPin(activity)),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityUnpinned matching feed",
                    event = ActivityUnpinned(fidScope, activityId),
                    verifyBlock = { state -> state.onActivityUnpinned(activityId) },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityUnpinned non-matching feed",
                    event = ActivityUnpinned(otherFidScope, activityId),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityHidden with hidden=true, matching user",
                    event = ActivityHidden(activityId, userId, hidden = true),
                    verifyBlock = { state -> state.onActivityHidden(activityId, true) },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityHidden with hidden=false, matching user",
                    event = ActivityHidden(activityId, userId, hidden = false),
                    verifyBlock = { state -> state.onActivityHidden(activityId, false) },
                ),
                testParams<FeedStateUpdates>(
                    name = "ActivityHidden non-matching user",
                    event = ActivityHidden(activityId, "other-user", hidden = true),
                    verifyBlock = { it wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "BookmarkAdded always handled",
                    event = BookmarkAdded(matchingBookmark),
                    verifyBlock = { state -> state.onBookmarkUpserted(matchingBookmark) },
                ),
                testParams<FeedStateUpdates>(
                    name = "BookmarkDeleted always handled",
                    event = BookmarkDeleted(matchingBookmark),
                    verifyBlock = { state -> state.onBookmarkRemoved(matchingBookmark) },
                ),
                testParams<FeedStateUpdates>(
                    name = "BookmarkUpdated always handled",
                    event = BookmarkUpdated(matchingBookmark),
                    verifyBlock = { state -> state.onBookmarkUpserted(matchingBookmark) },
                ),
                testParams<FeedStateUpdates>(
                    name = "CommentAdded matching feed",
                    event = CommentAdded(fidScope, comment),
                    verifyBlock = { state -> state.onCommentUpserted(comment) },
                ),
                testParams<FeedStateUpdates>(
                    name = "CommentAdded non-matching feed",
                    event = CommentAdded(otherFidScope, comment),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "CommentDeleted matching feed",
                    event = CommentDeleted(fidScope, comment),
                    verifyBlock = { state -> state.onCommentRemoved(comment) },
                ),
                testParams<FeedStateUpdates>(
                    name = "CommentDeleted non-matching feed",
                    event = CommentDeleted(otherFidScope, comment),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "CommentUpdated matching feed",
                    event = CommentUpdated(fidScope, comment),
                    verifyBlock = { state -> state.onCommentUpserted(comment) },
                ),
                testParams<FeedStateUpdates>(
                    name = "CommentUpdated non-matching feed",
                    event = CommentUpdated(otherFidScope, comment),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "CommentReactionDeleted matching feed",
                    event = CommentReactionDeleted(fidScope, comment, commentReaction),
                    verifyBlock = { state ->
                        state.onCommentReactionRemoved(comment, commentReaction)
                    },
                ),
                testParams<FeedStateUpdates>(
                    name = "CommentReactionDeleted non-matching feed",
                    event = CommentReactionDeleted(otherFidScope, comment, commentReaction),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "CommentReactionUpserted matching feed",
                    event = CommentReactionUpserted(fidScope, comment, commentReaction, false),
                    verifyBlock = { state ->
                        state.onCommentReactionUpserted(comment, commentReaction, false)
                    },
                ),
                testParams<FeedStateUpdates>(
                    name = "CommentReactionUpserted non-matching feed",
                    event = CommentReactionUpserted(otherFidScope, comment, commentReaction, false),
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
                kotlin.run {
                    val capabilities = mapOf(FeedId("user:1") to setOf(FeedOwnCapability.ReadFeed))

                    testParams<FeedStateUpdates>(
                        name = "FeedCapabilitiesUpdated handled regardless of feed ID",
                        event = FeedCapabilitiesUpdated(capabilities),
                        verifyBlock = { state -> state.onFeedCapabilitiesUpdated(capabilities) },
                    )
                },
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
                    name = "StoriesFeedUpdated matching feed",
                    event = StoriesFeedUpdated(fid.rawValue, activities, aggregatedActivities),
                    verifyBlock = { state ->
                        state.onStoriesFeedUpdated(activities, aggregatedActivities)
                    },
                ),
                testParams<FeedStateUpdates>(
                    name = "StoriesFeedUpdated non-matching feed",
                    event = StoriesFeedUpdated("group:different", activities, aggregatedActivities),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<FeedStateUpdates>(
                    name = "PollDeleted handled regardless of feed ID",
                    event = PollDeleted(pollId),
                    verifyBlock = { state -> state.onPollDeleted(pollId) },
                ),
                testParams<FeedStateUpdates>(
                    name = "PollUpdated handled regardless of feed ID",
                    event = PollUpdated(poll),
                    verifyBlock = { state -> state.onPollUpdated(poll) },
                ),
                testParams<FeedStateUpdates>(
                    name = "PollVoteCasted handled regardless of feed ID",
                    event = PollVoteCasted(pollId, pollVote),
                    verifyBlock = { state -> state.onPollVoteUpserted(pollVote, pollId) },
                ),
                testParams<FeedStateUpdates>(
                    name = "PollVoteChanged handled regardless of feed ID",
                    event = PollVoteChanged(pollId, pollVote),
                    verifyBlock = { state -> state.onPollVoteUpserted(pollVote, pollId) },
                ),
                testParams<FeedStateUpdates>(
                    name = "PollVoteRemoved handled regardless of feed ID",
                    event = PollVoteRemoved(pollId, pollVote),
                    verifyBlock = { state -> state.onPollVoteRemoved(pollVote, pollId) },
                ),
            )
    }
}
