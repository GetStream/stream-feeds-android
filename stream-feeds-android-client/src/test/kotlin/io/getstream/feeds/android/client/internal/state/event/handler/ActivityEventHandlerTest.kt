/*
 * Copyright (c) 2014-2026 Stream.io Inc. All rights reserved.
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
import io.getstream.feeds.android.client.internal.model.FeedOwnValues
import io.getstream.feeds.android.client.internal.state.ActivityStateUpdates
import io.getstream.feeds.android.client.internal.state.event.FidScope
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityHidden
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityReactionDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityReactionUpserted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkAdded
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentAdded
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentReactionDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentReactionUpserted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.FeedOwnValuesUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollVoteCasted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollVoteChanged
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollVoteRemoved
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.feedMemberData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.getstream.feeds.android.client.internal.test.TestData.followData
import io.getstream.feeds.android.client.internal.test.TestData.pollData
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteData
import io.getstream.feeds.android.network.models.FeedOwnCapability
import io.mockk.MockKVerificationScope
import io.mockk.called
import io.mockk.mockk
import org.junit.runners.Parameterized

internal class ActivityEventHandlerTest(
    testName: String,
    event: StateUpdateEvent,
    verifyBlock: MockKVerificationScope.(ActivityStateUpdates) -> Unit,
) : BaseEventHandlerTest<ActivityStateUpdates>(testName, event, verifyBlock) {

    override val state: ActivityStateUpdates = mockk(relaxed = true)
    override val handler = ActivityEventHandler(activityId, userId, state)

    companion object {
        private const val userId = "user-1"
        private const val activityId = "test-activity-id"
        private const val otherId = "other-activity"
        private val fidScope = FidScope.of("feed-1")
        private val activity = activityData(activityId)
        private val matchingBookmark = bookmarkData(activityData(activityId))
        private val nonMatchingBookmark = bookmarkData(activityData(otherId))
        private val matchingComment = commentData(objectId = activityId)
        private val nonMatchingComment = commentData(objectId = "other-activity")
        private val commentReaction = feedsReactionData()

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Collection<Array<Any>> =
            listOf(
                testParams<ActivityStateUpdates>(
                    name = "ActivityReactionDeleted matching activity",
                    event =
                        ActivityReactionDeleted(fidScope, activity, feedsReactionData(activityId)),
                    verifyBlock = { it.onReactionRemoved(feedsReactionData(activityId), activity) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityReactionDeleted non-matching activity",
                    event = ActivityReactionDeleted(fidScope, activity, feedsReactionData(otherId)),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityReactionUpserted matching activity",
                    event =
                        ActivityReactionUpserted(
                            fidScope,
                            activity,
                            feedsReactionData(activityId),
                            false,
                        ),
                    verifyBlock = {
                        it.onReactionUpserted(feedsReactionData(activityId), activity, false)
                    },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityReactionUpserted non-matching activity",
                    event =
                        ActivityReactionUpserted(
                            fidScope,
                            activity,
                            feedsReactionData(otherId),
                            false,
                        ),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityDeleted matching activity",
                    event = ActivityDeleted(fidScope, activityId),
                    verifyBlock = { it.onActivityRemoved() },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityDeleted non-matching activity",
                    event = ActivityDeleted(fidScope, "other-activity"),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityUpdated matching activity",
                    event = ActivityUpdated(fidScope, activity),
                    verifyBlock = { it.onActivityUpdated(activity) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityUpdated non-matching activity",
                    event = ActivityUpdated(fidScope, activityData("other-activity")),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityHidden matching activity and user",
                    event = ActivityHidden(activityId, userId, hidden = true),
                    verifyBlock = { it.onActivityHidden(activityId, true) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityHidden non-matching activity",
                    event = ActivityHidden("other-activity", userId, hidden = true),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityHidden non-matching user",
                    event = ActivityHidden(activityId, "other-user", hidden = true),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "BookmarkDeleted matching activity",
                    event = BookmarkDeleted(matchingBookmark),
                    verifyBlock = { it.onBookmarkRemoved(matchingBookmark) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "BookmarkDeleted non-matching activity",
                    event = BookmarkDeleted(nonMatchingBookmark),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "BookmarkAdded matching activity",
                    event = BookmarkAdded(matchingBookmark),
                    verifyBlock = { it.onBookmarkUpserted(matchingBookmark) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "BookmarkAdded non-matching activity",
                    event = BookmarkAdded(nonMatchingBookmark),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "BookmarkUpdated matching activity",
                    event = BookmarkUpdated(matchingBookmark),
                    verifyBlock = { it.onBookmarkUpserted(matchingBookmark) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "BookmarkUpdated non-matching activity",
                    event = BookmarkUpdated(nonMatchingBookmark),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "PollDeleted always handled",
                    event = PollDeleted("poll-1"),
                    verifyBlock = { it.onPollDeleted("poll-1") },
                ),
                testParams<ActivityStateUpdates>(
                    name = "PollUpdated always handled",
                    event = PollUpdated(pollData()),
                    verifyBlock = { it.onPollUpdated(pollData()) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "PollVoteCasted always handled",
                    event = PollVoteCasted(pollData(), pollVoteData()),
                    verifyBlock = { it.onPollVoteUpserted(pollData(), pollVoteData()) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "PollVoteChanged always handled",
                    event = PollVoteChanged(pollData(), pollVoteData()),
                    verifyBlock = { it.onPollVoteUpserted(pollData(), pollVoteData()) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "PollVoteRemoved always handled",
                    event = PollVoteRemoved(pollData(), pollVoteData()),
                    verifyBlock = { it.onPollVoteRemoved(pollData(), pollVoteData()) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentAdded matching activity",
                    event = CommentAdded(fidScope, matchingComment),
                    verifyBlock = { it.onCommentUpserted(matchingComment) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentAdded non-matching activity",
                    event = CommentAdded(fidScope, nonMatchingComment),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentDeleted matching activity",
                    event = CommentDeleted(fidScope, matchingComment),
                    verifyBlock = { it.onCommentRemoved(matchingComment.id) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentDeleted non-matching activity",
                    event = CommentDeleted(fidScope, nonMatchingComment),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentUpdated matching activity",
                    event = CommentUpdated(fidScope, matchingComment),
                    verifyBlock = { it.onCommentUpserted(matchingComment) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentUpdated non-matching activity",
                    event = CommentUpdated(fidScope, nonMatchingComment),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentReactionDeleted matching activity",
                    event = CommentReactionDeleted(fidScope, matchingComment, commentReaction),
                    verifyBlock = { it.onCommentReactionRemoved(matchingComment, commentReaction) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentReactionDeleted non-matching activity",
                    event = CommentReactionDeleted(fidScope, nonMatchingComment, commentReaction),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentReactionUpserted matching activity",
                    event =
                        CommentReactionUpserted(fidScope, matchingComment, commentReaction, false),
                    verifyBlock = {
                        it.onCommentReactionUpserted(matchingComment, commentReaction, false)
                    },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentReactionUpserted non-matching activity",
                    event =
                        CommentReactionUpserted(
                            fidScope,
                            nonMatchingComment,
                            commentReaction,
                            false,
                        ),
                    verifyBlock = { it wasNot called },
                ),
                kotlin.run {
                    val feedOwnValues =
                        FeedOwnValues(
                            capabilities = setOf(FeedOwnCapability.ReadFeed),
                            follows = listOf(followData()),
                            membership = feedMemberData(),
                        )
                    val map = mapOf(FeedId("user:1") to feedOwnValues)

                    testParams<ActivityStateUpdates>(
                        name = "FeedOwnValuesUpdated always handled",
                        event = FeedOwnValuesUpdated(map),
                        verifyBlock = { state -> state.onFeedOwnValuesUpdated(map) },
                    )
                },
            )
    }
}
