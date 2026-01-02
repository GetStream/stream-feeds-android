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

import io.getstream.feeds.android.client.internal.state.CommentReplyListStateUpdates
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
import io.mockk.mockk
import org.junit.runners.Parameterized

internal class CommentReplyListEventHandlerTest(
    testName: String,
    event: StateUpdateEvent,
    verifyBlock: MockKVerificationScope.(CommentReplyListStateUpdates) -> Unit,
) : BaseEventHandlerTest<CommentReplyListStateUpdates>(testName, event, verifyBlock) {

    override val state: CommentReplyListStateUpdates = mockk(relaxed = true)
    override val handler = CommentReplyListEventHandler(state)

    companion object {
        private val fidScope = FidScope.of("feed-1")

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): List<Array<Any>> =
            listOf(
                testParams<CommentReplyListStateUpdates>(
                    name = "CommentAdded",
                    event = CommentAdded(fidScope, commentData()),
                    verifyBlock = { state -> state.onCommentUpserted(commentData()) },
                ),
                testParams<CommentReplyListStateUpdates>(
                    name = "CommentDeleted",
                    event = CommentDeleted(fidScope, commentData()),
                    verifyBlock = { state -> state.onCommentRemoved(commentData().id) },
                ),
                testParams<CommentReplyListStateUpdates>(
                    name = "CommentUpdated",
                    event = CommentUpdated(fidScope, commentData()),
                    verifyBlock = { state -> state.onCommentUpserted(commentData()) },
                ),
                testParams<CommentReplyListStateUpdates>(
                    name = "CommentReactionUpserted",
                    event =
                        CommentReactionUpserted(
                            fidScope,
                            commentData(),
                            feedsReactionData(),
                            false,
                        ),
                    verifyBlock = { state ->
                        state.onCommentReactionUpserted(commentData(), feedsReactionData(), false)
                    },
                ),
                testParams<CommentReplyListStateUpdates>(
                    name = "CommentReactionDeleted",
                    event = CommentReactionDeleted(fidScope, commentData(), feedsReactionData()),
                    verifyBlock = { state ->
                        state.onCommentReactionRemoved(commentData(), feedsReactionData())
                    },
                ),
            )
    }
}
