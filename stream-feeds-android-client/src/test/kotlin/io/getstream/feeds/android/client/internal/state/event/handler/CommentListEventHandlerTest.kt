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

import io.getstream.feeds.android.client.internal.state.CommentListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentReactionAdded
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentReactionDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentReactionUpdated
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
    override val handler = CommentListEventHandler(state)

    companion object {
        private val comment = commentData()
        private val reaction = feedsReactionData()

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Collection<Array<Any>> =
            listOf(
                testParams<CommentListStateUpdates>(
                    name = "CommentUpdated",
                    event = CommentUpdated("feed-1", comment),
                    verifyBlock = { state -> state.onCommentUpdated(comment) },
                ),
                testParams<CommentListStateUpdates>(
                    name = "CommentDeleted",
                    event = CommentDeleted("feed-1", comment),
                    verifyBlock = { state -> state.onCommentRemoved(comment.id) },
                ),
                testParams<CommentListStateUpdates>(
                    name = "CommentReactionAdded",
                    event = CommentReactionAdded("feed-1", comment, reaction),
                    verifyBlock = { state -> state.onCommentReactionUpserted(comment, reaction) },
                ),
                testParams<CommentListStateUpdates>(
                    name = "CommentReactionDeleted",
                    event = CommentReactionDeleted("feed-1", comment, reaction),
                    verifyBlock = { state -> state.onCommentReactionRemoved(comment, reaction) },
                ),
                testParams<CommentListStateUpdates>(
                    name = "CommentReactionUpdated",
                    event = CommentReactionUpdated("feed-1", comment, reaction),
                    verifyBlock = { state -> state.onCommentReactionUpserted(comment, reaction) },
                ),
                testParams<CommentListStateUpdates>(
                    name = "unknown event",
                    event = StateUpdateEvent.CommentAdded("feed-1", comment),
                    verifyBlock = { state -> state wasNot called },
                ),
            )
    }
}
