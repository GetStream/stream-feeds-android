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

import io.getstream.android.core.api.filter.equal
import io.getstream.feeds.android.client.api.state.query.CommentsFilter
import io.getstream.feeds.android.client.api.state.query.CommentsFilterField
import io.getstream.feeds.android.client.internal.state.CommentListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.FidScope
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
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

internal class CommentListEventHandlerTest(
    testName: String,
    event: StateUpdateEvent,
    verifyBlock: MockKVerificationScope.(CommentListStateUpdates) -> Unit,
) : BaseEventHandlerTest<CommentListStateUpdates>(testName, event, verifyBlock) {

    override val state: CommentListStateUpdates = mockk(relaxed = true)
    override val handler = CommentListEventHandler(filter, state)

    companion object {
        private val fidScope = FidScope.of("feed-1")
        private val filter: CommentsFilter = CommentsFilterField.objectId.equal("activity-123")
        private val comment = commentData(objectId = "activity-123")
        private val nonMatchingComment = commentData(objectId = "different-activity")
        private val reaction = feedsReactionData()

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Collection<Array<Any>> =
            listOf(
                testParams<CommentListStateUpdates>(
                    name = "CommentAdded matching filter",
                    event = CommentAdded(fidScope, comment),
                    verifyBlock = { state -> state.onCommentUpserted(comment) },
                ),
                testParams<CommentListStateUpdates>(
                    name = "CommentAdded non-matching filter",
                    event = CommentAdded(fidScope, nonMatchingComment),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<CommentListStateUpdates>(
                    name = "CommentUpdated matching filter",
                    event = CommentUpdated(fidScope, comment),
                    verifyBlock = { state -> state.onCommentUpserted(comment) },
                ),
                testParams<CommentListStateUpdates>(
                    name = "CommentUpdated non-matching filter",
                    event = CommentUpdated(fidScope, nonMatchingComment),
                    verifyBlock = { state -> state.onCommentRemoved(nonMatchingComment.id) },
                ),
                testParams<CommentListStateUpdates>(
                    name = "CommentDeleted",
                    event = CommentDeleted(fidScope, comment),
                    verifyBlock = { state -> state.onCommentRemoved(comment.id) },
                ),
                testParams<CommentListStateUpdates>(
                    name = "CommentReactionDeleted",
                    event = CommentReactionDeleted(fidScope, comment, reaction),
                    verifyBlock = { state -> state.onCommentReactionRemoved(comment, reaction) },
                ),
                testParams<CommentListStateUpdates>(
                    name = "CommentReactionUpserted",
                    event = CommentReactionUpserted(fidScope, comment, reaction, false),
                    verifyBlock = { state ->
                        state.onCommentReactionUpserted(comment, reaction, false)
                    },
                ),
            )
    }
}
