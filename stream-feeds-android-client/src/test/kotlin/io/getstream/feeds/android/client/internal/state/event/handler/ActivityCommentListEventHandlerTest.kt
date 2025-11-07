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

import io.getstream.feeds.android.client.internal.state.ActivityCommentListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentAdded
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentReactionDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentReactionUpserted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentUpdated
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.mockk.MockKVerificationScope
import io.mockk.called
import io.mockk.mockk
import org.junit.runners.Parameterized

internal class ActivityCommentListEventHandlerTest(
    testName: String,
    event: StateUpdateEvent,
    verifyBlock: MockKVerificationScope.(ActivityCommentListStateUpdates) -> Unit,
) : BaseEventHandlerTest<ActivityCommentListStateUpdates>(testName, event, verifyBlock) {

    override val state: ActivityCommentListStateUpdates = mockk(relaxed = true)
    override val handler = ActivityCommentListEventHandler(objectId, objectType, state)

    companion object {
        private const val objectId = "activity-1"
        private const val objectType = "activity"
        private const val differentObjectId = "different-activity"
        private val matchingComment = commentData(objectId = objectId, objectType = objectType)
        private val nonMatchingComment =
            commentData(objectId = differentObjectId, objectType = objectType)

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): List<Array<Any>> =
            listOf(
                testParams<ActivityCommentListStateUpdates>(
                    name = "ActivityDeleted matching activity",
                    event = ActivityDeleted("feed-1", objectId),
                    verifyBlock = { state -> state.onActivityRemoved() },
                ),
                testParams<ActivityCommentListStateUpdates>(
                    name = "ActivityDeleted non-matching activity",
                    event = ActivityDeleted("feed-1", differentObjectId),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<ActivityCommentListStateUpdates>(
                    name = "CommentAdded matching object",
                    event = CommentAdded("feed-1", matchingComment),
                    verifyBlock = { state -> state.onCommentUpserted(matchingComment) },
                ),
                testParams<ActivityCommentListStateUpdates>(
                    name = "CommentAdded non-matching object",
                    event = CommentAdded("feed-1", nonMatchingComment),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<ActivityCommentListStateUpdates>(
                    name = "CommentDeleted matching object",
                    event = CommentDeleted("feed-1", matchingComment),
                    verifyBlock = { state -> state.onCommentRemoved(matchingComment.id) },
                ),
                testParams<ActivityCommentListStateUpdates>(
                    name = "CommentDeleted non-matching object",
                    event = CommentDeleted("feed-1", nonMatchingComment),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<ActivityCommentListStateUpdates>(
                    name = "CommentUpdated matching object",
                    event = CommentUpdated("feed-1", matchingComment),
                    verifyBlock = { state -> state.onCommentUpserted(matchingComment) },
                ),
                testParams<ActivityCommentListStateUpdates>(
                    name = "CommentUpdated non-matching object",
                    event = CommentUpdated("feed-1", nonMatchingComment),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<ActivityCommentListStateUpdates>(
                    name = "CommentReactionDeleted matching object",
                    event = CommentReactionDeleted("feed-1", matchingComment, feedsReactionData()),
                    verifyBlock = { state ->
                        state.onCommentReactionRemoved(matchingComment, feedsReactionData())
                    },
                ),
                testParams<ActivityCommentListStateUpdates>(
                    name = "CommentReactionDeleted non-matching object",
                    event =
                        CommentReactionDeleted("feed-1", nonMatchingComment, feedsReactionData()),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<ActivityCommentListStateUpdates>(
                    name = "CommentReactionUpserted matching object",
                    event =
                        CommentReactionUpserted(
                            "feed-1",
                            matchingComment,
                            feedsReactionData(),
                            true,
                        ),
                    verifyBlock = { state ->
                        state.onCommentReactionUpserted(matchingComment, feedsReactionData(), true)
                    },
                ),
                testParams<ActivityCommentListStateUpdates>(
                    name = "CommentReactionUpserted non-matching object",
                    event =
                        CommentReactionUpserted(
                            "feed-1",
                            nonMatchingComment,
                            feedsReactionData(),
                            false,
                        ),
                    verifyBlock = { state -> state wasNot called },
                ),
            )
    }
}
