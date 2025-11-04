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

package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.state.query.CommentRepliesQuery
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.model.removeReaction
import io.getstream.feeds.android.client.internal.model.upsertReaction
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.getstream.feeds.android.client.internal.test.TestData.threadedCommentData
import java.util.Date
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CommentReplyListStateImplTest {

    private val currentUserId = "user-1"
    private val query = CommentRepliesQuery(commentId = "comment-1", limit = 10)
    private val state = CommentReplyListStateImpl(query = query, currentUserId = currentUserId)

    @Test
    fun `on onQueryMoreReplies, update state with new replies and pagination`() = runTest {
        val replies =
            listOf(
                threadedCommentData("reply-1", text = "First reply"),
                threadedCommentData("reply-2", text = "Second reply"),
            )
        val pagination = PaginationData(next = "next-cursor", previous = null)
        val result = PaginationResult(models = replies, pagination = pagination)

        state.onQueryMoreReplies(result)

        assertEquals(replies, state.replies.value)
        assertEquals(pagination, state.pagination)
    }

    @Test
    fun `on onQueryMoreReplies with existing replies, merge new replies`() = runTest {
        val initialReply = threadedCommentData("reply-1", text = "First reply")
        setupInitialReplies(initialReply)

        val newReply = threadedCommentData("reply-2", text = "Second reply")
        val newPagination = PaginationData(next = "next-cursor-2", previous = "next-cursor")
        val newResult = PaginationResult(models = listOf(newReply), pagination = newPagination)

        state.onQueryMoreReplies(newResult)

        val expectedReplies = listOf(initialReply, newReply)
        assertEquals(expectedReplies, state.replies.value)
        assertEquals(newPagination, state.pagination)
    }

    @Test
    fun `on onCommentAdded with direct reply, add reply to parent comment`() = runTest {
        val parentComment = threadedCommentData("parent-1", text = "Parent comment")

        setupInitialReplies(parentComment)

        val newReply = commentData("reply-1", parentId = "parent-1", text = "Reply to parent")

        state.onCommentUpserted(newReply)

        val expectedParent =
            parentComment.copy(replies = listOf(ThreadedCommentData(newReply)), replyCount = 1)
        assertEquals(listOf(expectedParent), state.replies.value)
    }

    @Test
    fun `on onCommentAdded with non-reply comment, ignore it`() = runTest {
        val parentComment = threadedCommentData("parent-1", text = "Parent comment")

        setupInitialReplies(parentComment)

        val nonReply = commentData("comment-1", parentId = null, text = "Not a reply")

        state.onCommentUpserted(nonReply)

        assertEquals(listOf(parentComment), state.replies.value)
    }

    @Test
    fun `on onCommentAdded replies are sorted by createdAt`() = runTest {
        val parentComment = threadedCommentData("parent-1", text = "Parent comment")

        setupInitialReplies(parentComment)

        val oldReply =
            commentData(
                id = "reply-1",
                parentId = "parent-1",
                text = "Old reply",
                createdAt = Date(1),
            )

        val newReply =
            commentData(
                id = "reply-2",
                parentId = "parent-1",
                text = "New reply",
                createdAt = Date(2),
            )

        // Add replies in reverse order to test sorting
        state.onCommentUpserted(oldReply)
        state.onCommentUpserted(newReply)

        val expectedParent =
            parentComment.copy(
                replies = listOf(newReply, oldReply).map(::ThreadedCommentData),
                replyCount = 2,
            )

        assertEquals(listOf(expectedParent), state.replies.value)
    }

    @Test
    fun `on onCommentRemoved with parent comment, clear all replies`() = runTest {
        val reply1 = threadedCommentData("reply-1", text = "First reply")
        val reply2 = threadedCommentData("reply-2", text = "Second reply")

        setupInitialReplies(reply1, reply2)

        state.onCommentRemoved(query.commentId)

        assertEquals(emptyList<ThreadedCommentData>(), state.replies.value)
    }

    @Test
    fun `on onCommentRemoved with nested reply, remove it recursively`() = runTest {
        val nestedReply =
            threadedCommentData("nested-reply", parentId = "reply-1", text = "Nested reply")
        val directReply =
            threadedCommentData(
                "reply-1",
                parentId = "parent-1",
                text = "Direct reply",
                replies = listOf(nestedReply),
                replyCount = 1,
            )
        val parentComment =
            threadedCommentData("parent-1", text = "Parent comment", replies = listOf(directReply))

        setupInitialReplies(parentComment)

        state.onCommentRemoved("nested-reply")

        val expectedDirectReply = directReply.copy(replies = emptyList(), replyCount = 0)
        val expectedParent = parentComment.copy(replies = listOf(expectedDirectReply))
        assertEquals(listOf(expectedParent), state.replies.value)
    }

    @Test
    fun `on onCommentUpdated, then preserve ownReactions when updating comment`() = runTest {
        val ownReaction = feedsReactionData("reply-1", "like", currentUserId)
        val parentComment =
            threadedCommentData(
                "parent-1",
                text = "Parent comment",
                replies =
                    listOf(
                        threadedCommentData(
                            "reply-1",
                            parentId = "parent-1",
                            text = "Original text",
                            ownReactions = listOf(ownReaction),
                        )
                    ),
                replyCount = 1,
            )

        setupInitialReplies(parentComment)

        val updatedCommentData =
            commentData(
                "reply-1",
                parentId = "parent-1",
                text = "Updated text",
                ownReactions = emptyList(),
            )

        state.onCommentUpserted(updatedCommentData)

        val expectedReply =
            threadedCommentData(
                "reply-1",
                parentId = "parent-1",
                text = "Updated text",
                ownReactions = listOf(ownReaction),
            )
        val expectedParent = parentComment.copy(replies = listOf(expectedReply))
        assertEquals(listOf(expectedParent), state.replies.value)
    }

    @Test
    fun `on onCommentReactionUpserted, add reaction to nested comment`() = runTest {
        val comment = threadedCommentData("comment-1", text = "Comment with reaction")
        val update = commentData("comment-1", text = "Comment with reaction")

        setupInitialReplies(comment)

        val reaction =
            feedsReactionData(activityId = "comment-1", type = "like", userId = currentUserId)

        state.onCommentReactionUpserted(update, reaction, enforceUnique = false)

        val expectedComment = comment.upsertReaction(update, reaction, currentUserId, false)
        assertEquals(listOf(expectedComment), state.replies.value)
    }

    @Test
    fun `on onCommentReactionRemoved, remove reaction from nested comment`() = runTest {
        val reaction =
            feedsReactionData(activityId = "comment-1", type = "like", userId = currentUserId)
        val update = commentData("comment-1", text = "Comment with reaction")

        val baseComment = threadedCommentData("comment-1", text = "Comment with reaction")
        val commentWithReaction = baseComment.upsertReaction(update, reaction, currentUserId, false)

        setupInitialReplies(commentWithReaction)

        state.onCommentReactionRemoved(update, reaction)

        val expectedComment = commentWithReaction.removeReaction(update, reaction, currentUserId)
        assertEquals(listOf(expectedComment), state.replies.value)
    }

    @Test
    fun `on onCommentReactionUpserted to deeply nested comment, update correct comment`() =
        runTest {
            val deeplyNestedComment =
                threadedCommentData("deep-comment", parentId = "reply-1", text = "Deep comment")
            val directReply =
                threadedCommentData(
                    "reply-1",
                    parentId = "parent-1",
                    text = "Direct reply",
                    replies = listOf(deeplyNestedComment),
                )
            val parentComment =
                threadedCommentData(
                    "parent-1",
                    text = "Parent comment",
                    replies = listOf(directReply),
                )
            val update = commentData("deep-comment", objectId = "reply-1", text = "Deep comment")

            setupInitialReplies(parentComment)

            val reaction =
                feedsReactionData(
                    activityId = "deep-comment",
                    type = "heart",
                    userId = currentUserId,
                )

            state.onCommentReactionUpserted(update, reaction, enforceUnique = false)

            val expectedDeepComment =
                deeplyNestedComment.upsertReaction(update, reaction, currentUserId, false)
            val expectedDirectReply = directReply.copy(replies = listOf(expectedDeepComment))
            val expectedParent = parentComment.copy(replies = listOf(expectedDirectReply))

            assertEquals(listOf(expectedParent), state.replies.value)
        }

    @Test
    fun `on onCommentReactionUpserted with enforceUnique true, then replace all existing user reactions with single new one`() =
        runTest {
            val existingReactions =
                listOf(
                    feedsReactionData(
                        activityId = "comment-1",
                        type = "like",
                        userId = currentUserId,
                    ),
                    feedsReactionData(
                        activityId = "comment-1",
                        type = "heart",
                        userId = currentUserId,
                    ),
                )

            val comment = threadedCommentData("comment-1", ownReactions = existingReactions)
            val update = commentData("comment-1", ownReactions = existingReactions)

            setupInitialReplies(comment)

            val newReaction =
                feedsReactionData(activityId = "comment-1", type = "smile", userId = currentUserId)

            state.onCommentReactionUpserted(update, newReaction, enforceUnique = true)

            val expectedComment =
                comment.upsertReaction(update, newReaction, currentUserId, enforceUnique = true)
            assertEquals(listOf(expectedComment), state.replies.value)
        }

    private fun setupInitialReplies(vararg replies: ThreadedCommentData) {
        val result =
            PaginationResult(models = replies.toList(), pagination = PaginationData("next"))
        state.onQueryMoreReplies(result)
    }
}
