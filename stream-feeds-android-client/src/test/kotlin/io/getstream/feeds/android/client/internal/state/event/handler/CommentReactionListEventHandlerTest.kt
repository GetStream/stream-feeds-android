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

import io.getstream.feeds.android.client.internal.state.CommentReactionListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentReactionDeleted
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.CommentReactionUpserted
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.mockk.MockKVerificationScope
import io.mockk.called
import io.mockk.mockk
import org.junit.runners.Parameterized

internal class CommentReactionListEventHandlerTest(
    testName: String,
    event: StateUpdateEvent,
    verifyBlock: MockKVerificationScope.(CommentReactionListStateUpdates) -> Unit,
) : BaseEventHandlerTest<CommentReactionListStateUpdates>(testName, event, verifyBlock) {

    override val state: CommentReactionListStateUpdates = mockk(relaxed = true)
    override val handler = CommentReactionListEventHandler("comment-1", state)

    companion object {
        private val reaction = feedsReactionData()
        private val matchingComment = commentData("comment-1")
        private val nonMatchingComment = commentData("different-comment")

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Collection<Array<Any>> =
            listOf(
                testParams<CommentReactionListStateUpdates>(
                    name = "CommentDeleted matching comment",
                    event = CommentDeleted("feed-1", matchingComment),
                    verifyBlock = { state -> state.onCommentRemoved() },
                ),
                testParams<CommentReactionListStateUpdates>(
                    name = "CommentDeleted non-matching comment",
                    event = CommentDeleted("feed-1", nonMatchingComment),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<CommentReactionListStateUpdates>(
                    name = "CommentReactionDeleted matching comment",
                    event = CommentReactionDeleted("feed-1", matchingComment, reaction),
                    verifyBlock = { state -> state.onReactionRemoved(reaction) },
                ),
                testParams<CommentReactionListStateUpdates>(
                    name = "CommentReactionDeleted non-matching comment",
                    event = CommentReactionDeleted("feed-1", nonMatchingComment, reaction),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<CommentReactionListStateUpdates>(
                    name = "CommentReactionUpserted matching comment",
                    event = CommentReactionUpserted("feed-1", matchingComment, reaction, false),
                    verifyBlock = { state -> state.onReactionUpserted(reaction, false) },
                ),
                testParams<CommentReactionListStateUpdates>(
                    name = "CommentReactionUpserted non-matching comment",
                    event = CommentReactionUpserted("feed-1", nonMatchingComment, reaction, false),
                    verifyBlock = { state -> state wasNot called },
                ),
            )
    }
}
