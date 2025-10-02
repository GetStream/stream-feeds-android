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
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityReactionAdded
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityReactionDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityReactionUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkAdded
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.BookmarkUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentAdded
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentReactionDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentReactionUpserted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollVoteCasted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollVoteChanged
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollVoteRemoved
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.getstream.feeds.android.client.internal.test.TestData.pollData
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteData
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
    override val handler: StateUpdateEventListener = ActivityEventHandler(fid, activityId, state)

    companion object {
        private val fid = FeedId("user", "activity-1")
        private const val otherFid = "user:other-activity"
        private const val activityId = "test-activity-id"
        private const val otherId = "other-activity"
        private val activity = activityData(activityId)
        private val matchingBookmark =
            bookmarkData(activityData(activityId, feeds = listOf(fid.rawValue)))
        private val nonMatchingActivityBookmark =
            bookmarkData(activityData(otherId, feeds = listOf(fid.rawValue)))
        private val nonMatchingFeedBookmark =
            bookmarkData(activityData(activityId, feeds = listOf(otherFid)))
        private val matchingComment = commentData(objectId = activityId)
        private val nonMatchingActivityComment = commentData(objectId = "other-activity")
        private val commentReaction = feedsReactionData()

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Collection<Array<Any>> =
            listOf(
                testParams<ActivityStateUpdates>(
                    name = "ActivityReactionAdded matching feed and activity",
                    event =
                        ActivityReactionAdded(
                            fid.rawValue,
                            activity,
                            feedsReactionData(activityId),
                        ),
                    verifyBlock = { it.onReactionUpserted(feedsReactionData(activityId), activity) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityReactionAdded non-matching feed",
                    event =
                        ActivityReactionAdded(otherFid, activity, feedsReactionData(activityId)),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityReactionAdded non-matching activity",
                    event =
                        ActivityReactionAdded(fid.rawValue, activity, feedsReactionData(otherId)),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityReactionDeleted matching feed and activity",
                    event =
                        ActivityReactionDeleted(
                            fid.rawValue,
                            activity,
                            feedsReactionData(activityId),
                        ),
                    verifyBlock = { it.onReactionRemoved(feedsReactionData(activityId), activity) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityReactionDeleted non-matching feed",
                    event =
                        ActivityReactionDeleted(otherFid, activity, feedsReactionData(activityId)),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityReactionDeleted non-matching activity",
                    event =
                        ActivityReactionDeleted(fid.rawValue, activity, feedsReactionData(otherId)),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityReactionUpdated matching feed and activity",
                    event =
                        ActivityReactionUpdated(
                            fid.rawValue,
                            activity,
                            feedsReactionData(activityId),
                        ),
                    verifyBlock = { it.onReactionUpserted(feedsReactionData(activityId), activity) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityReactionUpdated non-matching feed",
                    event =
                        ActivityReactionUpdated(otherFid, activity, feedsReactionData(activityId)),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityReactionUpdated non-matching activity",
                    event =
                        ActivityReactionUpdated(fid.rawValue, activity, feedsReactionData(otherId)),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityDeleted matching feed and activity",
                    event = ActivityDeleted(fid.rawValue, activityId),
                    verifyBlock = { it.onActivityRemoved() },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityDeleted non-matching feed",
                    event = ActivityDeleted(otherFid, activityId),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityDeleted non-matching activity",
                    event = ActivityDeleted(fid.rawValue, "other-activity"),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityUpdated matching feed and activity",
                    event = ActivityUpdated(fid.rawValue, activity),
                    verifyBlock = { it.onActivityUpdated(activity) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityUpdated non-matching feed",
                    event = ActivityUpdated(otherFid, activity),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "ActivityUpdated non-matching activity",
                    event = ActivityUpdated(fid.rawValue, activityData("other-activity")),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "BookmarkDeleted matching feed and activity",
                    event = BookmarkDeleted(matchingBookmark),
                    verifyBlock = { it.onBookmarkRemoved(matchingBookmark) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "BookmarkDeleted non-matching feed",
                    event = BookmarkDeleted(nonMatchingFeedBookmark),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "BookmarkDeleted non-matching activity",
                    event = BookmarkDeleted(nonMatchingActivityBookmark),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "BookmarkAdded matching feed and activity",
                    event = BookmarkAdded(matchingBookmark),
                    verifyBlock = { it.onBookmarkUpserted(matchingBookmark) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "BookmarkAdded non-matching feed",
                    event = BookmarkAdded(nonMatchingFeedBookmark),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "BookmarkAdded non-matching activity",
                    event = BookmarkAdded(nonMatchingActivityBookmark),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "BookmarkUpdated matching feed and activity",
                    event = BookmarkUpdated(matchingBookmark),
                    verifyBlock = { it.onBookmarkUpserted(matchingBookmark) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "BookmarkUpdated non-matching feed",
                    event = BookmarkUpdated(nonMatchingFeedBookmark),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "BookmarkUpdated non-matching activity",
                    event = BookmarkUpdated(nonMatchingActivityBookmark),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "PollDeleted matching feed",
                    event = PollDeleted(fid.rawValue, "poll-1"),
                    verifyBlock = { it.onPollDeleted("poll-1") },
                ),
                testParams<ActivityStateUpdates>(
                    name = "PollDeleted non-matching feed",
                    event = PollDeleted(otherFid, "poll-1"),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "PollUpdated matching feed",
                    event = PollUpdated(fid.rawValue, pollData()),
                    verifyBlock = { it.onPollUpdated(pollData()) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "PollUpdated non-matching feed",
                    event = PollUpdated(otherFid, pollData()),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "PollVoteCasted matching feed",
                    event = PollVoteCasted(fid.rawValue, "poll-1", pollVoteData()),
                    verifyBlock = { it.onPollVoteUpserted(pollVoteData(), "poll-1") },
                ),
                testParams<ActivityStateUpdates>(
                    name = "PollVoteCasted non-matching feed",
                    event = PollVoteCasted(otherFid, "poll-1", pollVoteData()),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "PollVoteChanged matching feed",
                    event = PollVoteChanged(fid.rawValue, "poll-1", pollVoteData()),
                    verifyBlock = { it.onPollVoteUpserted(pollVoteData(), "poll-1") },
                ),
                testParams<ActivityStateUpdates>(
                    name = "PollVoteChanged non-matching feed",
                    event = PollVoteChanged(otherFid, "poll-1", pollVoteData()),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "PollVoteRemoved matching feed",
                    event = PollVoteRemoved(fid.rawValue, "poll-1", pollVoteData()),
                    verifyBlock = { it.onPollVoteRemoved(pollVoteData(), "poll-1") },
                ),
                testParams<ActivityStateUpdates>(
                    name = "PollVoteRemoved non-matching feed",
                    event = PollVoteRemoved(otherFid, "poll-1", pollVoteData()),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentAdded matching feed and activity",
                    event = CommentAdded(fid.rawValue, matchingComment),
                    verifyBlock = { it.onCommentUpserted(matchingComment) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentAdded non-matching feed",
                    event = CommentAdded(otherFid, matchingComment),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentAdded non-matching activity",
                    event = CommentAdded(fid.rawValue, nonMatchingActivityComment),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentDeleted matching feed and activity",
                    event = CommentDeleted(fid.rawValue, matchingComment),
                    verifyBlock = { it.onCommentRemoved(matchingComment.id) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentDeleted non-matching feed",
                    event = CommentDeleted(otherFid, matchingComment),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentDeleted non-matching activity",
                    event = CommentDeleted(fid.rawValue, nonMatchingActivityComment),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentUpdated matching feed and activity",
                    event = CommentUpdated(fid.rawValue, matchingComment),
                    verifyBlock = { it.onCommentUpserted(matchingComment) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentUpdated non-matching feed",
                    event = CommentUpdated(otherFid, matchingComment),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentUpdated non-matching activity",
                    event = CommentUpdated(fid.rawValue, nonMatchingActivityComment),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentReactionDeleted matching feed and activity",
                    event = CommentReactionDeleted(fid.rawValue, matchingComment, commentReaction),
                    verifyBlock = { it.onCommentReactionRemoved(matchingComment, commentReaction) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentReactionDeleted non-matching feed",
                    event = CommentReactionDeleted(otherFid, matchingComment, commentReaction),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentReactionDeleted non-matching activity",
                    event =
                        CommentReactionDeleted(
                            fid.rawValue,
                            nonMatchingActivityComment,
                            commentReaction,
                        ),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentReactionUpserted matching feed and activity",
                    event = CommentReactionUpserted(fid.rawValue, matchingComment, commentReaction),
                    verifyBlock = { it.onCommentReactionUpserted(matchingComment, commentReaction) },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentReactionUpserted non-matching feed",
                    event = CommentReactionUpserted(otherFid, matchingComment, commentReaction),
                    verifyBlock = { it wasNot called },
                ),
                testParams<ActivityStateUpdates>(
                    name = "CommentReactionUpserted non-matching activity",
                    event =
                        CommentReactionUpserted(
                            fid.rawValue,
                            nonMatchingActivityComment,
                            commentReaction,
                        ),
                    verifyBlock = { it wasNot called },
                ),
            )
    }
}
