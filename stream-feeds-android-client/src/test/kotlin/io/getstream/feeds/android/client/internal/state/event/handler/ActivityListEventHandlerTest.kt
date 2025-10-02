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
import io.getstream.feeds.android.client.api.state.query.ActivitiesFilterField
import io.getstream.feeds.android.client.internal.state.ActivityListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityAdded
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityDeleted
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
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollUpdated
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollVoteCasted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollVoteChanged
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.PollVoteRemoved
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

internal class ActivityListEventHandlerTest(
    testName: String,
    event: StateUpdateEvent,
    verifyBlock: MockKVerificationScope.(ActivityListStateUpdates) -> Unit,
) : BaseEventHandlerTest<ActivityListStateUpdates>(testName, event, verifyBlock) {

    override val state: ActivityListStateUpdates = mockk(relaxed = true)
    override val handler = ActivityListEventHandler(testFilter, state)

    companion object {
        private val testFilter = ActivitiesFilterField.type.equal("post")

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): List<Array<Any>> =
            listOf(
                testParams<ActivityListStateUpdates>(
                    name = "ActivityAdded with matching filter",
                    event = ActivityAdded("feed-1", activityData("activity-1", type = "post")),
                    verifyBlock = { state ->
                        state.onActivityUpserted(activityData("activity-1", type = "post"))
                    },
                ),
                testParams<ActivityListStateUpdates>(
                    name = "ActivityAdded with non-matching filter",
                    event = ActivityAdded("feed-1", activityData("activity-1", type = "story")),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<ActivityListStateUpdates>(
                    name = "ActivityDeleted",
                    event = ActivityDeleted("feed-1", "activity-1"),
                    verifyBlock = { state -> state.onActivityRemoved("activity-1") },
                ),
                testParams<ActivityListStateUpdates>(
                    name = "ActivityUpdated with matching filter",
                    event = ActivityUpdated("feed-1", activityData("activity-1", type = "post")),
                    verifyBlock = { state ->
                        state.onActivityUpserted(activityData("activity-1", type = "post"))
                    },
                ),
                testParams<ActivityListStateUpdates>(
                    name = "ActivityUpdated with non-matching filter",
                    event = ActivityUpdated("feed-1", activityData("activity-1", type = "story")),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<ActivityListStateUpdates>(
                    name = "ActivityReactionDeleted",
                    event =
                        ActivityReactionDeleted(
                            "feed-1",
                            activityData("activity-1"),
                            feedsReactionData("activity-1"),
                        ),
                    verifyBlock = { state ->
                        state.onReactionRemoved(
                            feedsReactionData("activity-1"),
                            activityData("activity-1"),
                        )
                    },
                ),
                testParams<ActivityListStateUpdates>(
                    name = "ActivityReactionUpserted",
                    event =
                        ActivityReactionUpserted(
                            "feed-1",
                            activityData("activity-1"),
                            feedsReactionData("activity-1"),
                        ),
                    verifyBlock = { state ->
                        state.onReactionUpserted(
                            feedsReactionData("activity-1"),
                            activityData("activity-1"),
                        )
                    },
                ),
                testParams<ActivityListStateUpdates>(
                    name = "BookmarkAdded",
                    event = BookmarkAdded(bookmarkData()),
                    verifyBlock = { state -> state.onBookmarkUpserted(bookmarkData()) },
                ),
                testParams<ActivityListStateUpdates>(
                    name = "BookmarkDeleted",
                    event = BookmarkDeleted(bookmarkData()),
                    verifyBlock = { state -> state.onBookmarkRemoved(bookmarkData()) },
                ),
                testParams<ActivityListStateUpdates>(
                    name = "BookmarkUpdated",
                    event = BookmarkUpdated(bookmarkData()),
                    verifyBlock = { state -> state.onBookmarkUpserted(bookmarkData()) },
                ),
                testParams<ActivityListStateUpdates>(
                    name = "CommentAdded",
                    event = CommentAdded("feed-1", commentData()),
                    verifyBlock = { state -> state.onCommentUpserted(commentData()) },
                ),
                testParams<ActivityListStateUpdates>(
                    name = "CommentDeleted",
                    event = CommentDeleted("feed-1", commentData()),
                    verifyBlock = { state -> state.onCommentRemoved(commentData()) },
                ),
                testParams<ActivityListStateUpdates>(
                    name = "CommentUpdated",
                    event = CommentUpdated("feed-1", commentData()),
                    verifyBlock = { state -> state.onCommentUpserted(commentData()) },
                ),
                testParams<ActivityListStateUpdates>(
                    name = "CommentReactionDeleted",
                    event = CommentReactionDeleted("feed-1", commentData(), feedsReactionData()),
                    verifyBlock = { state ->
                        state.onCommentReactionRemoved(commentData(), feedsReactionData())
                    },
                ),
                testParams<ActivityListStateUpdates>(
                    name = "CommentReactionUpserted",
                    event =
                        CommentReactionUpserted(
                            "feed-1",
                            commentData(),
                            feedsReactionData(),
                            false,
                        ),
                    verifyBlock = { state ->
                        state.onCommentReactionUpserted(commentData(), feedsReactionData())
                    },
                ),
                testParams<ActivityListStateUpdates>(
                    name = "PollDeleted",
                    event = PollDeleted("feed-1", "poll-1"),
                    verifyBlock = { state -> state.onPollDeleted("poll-1") },
                ),
                testParams<ActivityListStateUpdates>(
                    name = "PollUpdated",
                    event = PollUpdated("feed-1", pollData()),
                    verifyBlock = { state -> state.onPollUpdated(pollData()) },
                ),
                testParams<ActivityListStateUpdates>(
                    name = "PollVoteCasted",
                    event = PollVoteCasted("feed-1", "poll-1", pollVoteData()),
                    verifyBlock = { state -> state.onPollVoteUpserted("poll-1", pollVoteData()) },
                ),
                testParams<ActivityListStateUpdates>(
                    name = "PollVoteChanged",
                    event = PollVoteChanged("feed-1", "poll-1", pollVoteData()),
                    verifyBlock = { state -> state.onPollVoteUpserted("poll-1", pollVoteData()) },
                ),
                testParams<ActivityListStateUpdates>(
                    name = "PollVoteRemoved",
                    event = PollVoteRemoved("feed-1", "poll-1", pollVoteData()),
                    verifyBlock = { state -> state.onPollVoteRemoved("poll-1", pollVoteData()) },
                ),
            )
    }
}
