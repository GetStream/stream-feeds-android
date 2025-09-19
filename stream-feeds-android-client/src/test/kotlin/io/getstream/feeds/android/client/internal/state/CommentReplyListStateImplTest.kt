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
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.addReaction
import io.getstream.feeds.android.client.api.model.removeReaction
import io.getstream.feeds.android.client.api.state.query.CommentRepliesQuery
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.getstream.feeds.android.client.internal.test.TestData.threadedCommentData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CommentReplyListStateImplTest {

    private val currentUserId = "user-1"
    private val query = CommentRepliesQuery(commentId = "comment-1", limit = 10)

    private fun createState() =
        CommentReplyListStateImpl(query = query, currentUserId = currentUserId)

    @Test
    fun `on onQueryMoreReplies, update state with new replies and pagination`() = runTest {
        val state = createState()
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
        val state = createState()

        // Set up initial replies
        val initialReplies = listOf(threadedCommentData("reply-1", text = "First reply"))
        val initialPagination = PaginationData(next = "next-cursor", previous = null)
        val initialResult =
            PaginationResult(models = initialReplies, pagination = initialPagination)
        state.onQueryMoreReplies(initialResult)

        // Add more replies
        val newReplies = listOf(threadedCommentData("reply-2", text = "Second reply"))
        val newPagination = PaginationData(next = "next-cursor-2", previous = "next-cursor")
        val newResult = PaginationResult(models = newReplies, pagination = newPagination)

        state.onQueryMoreReplies(newResult)

        val expectedReplies = initialReplies + newReplies
        assertEquals(expectedReplies, state.replies.value)
        assertEquals(newPagination, state.pagination)
    }

    @Test
    fun `on onCommentAdded with direct reply, add reply to parent comment`() = runTest {
        val state = createState()
        val parentComment = threadedCommentData("parent-1", text = "Parent comment")

        // Set up initial state with parent comment
        val initialResult =
            PaginationResult(models = listOf(parentComment), pagination = PaginationData.EMPTY)
        state.onQueryMoreReplies(initialResult)

        // Add a direct reply
        val newReply =
            threadedCommentData("reply-1", parentId = "parent-1", text = "Reply to parent")

        state.onCommentAdded(newReply)

        // Compare expected final state
        val expectedParent = parentComment.copy(replies = listOf(newReply), replyCount = 1)
        assertEquals(listOf(expectedParent), state.replies.value)
    }

    @Test
    fun `on onCommentAdded with non-reply comment, ignore it`() = runTest {
        val state = createState()
        val parentComment = threadedCommentData("parent-1", text = "Parent comment")

        // Set up initial state
        val initialResult =
            PaginationResult(models = listOf(parentComment), pagination = PaginationData.EMPTY)
        state.onQueryMoreReplies(initialResult)

        // Add a comment without parentId (not a reply)
        val nonReply = threadedCommentData("comment-1", parentId = null, text = "Not a reply")

        state.onCommentAdded(nonReply)

        // State should remain unchanged
        assertEquals(listOf(parentComment), state.replies.value)
    }

    @Test
    fun `on onCommentRemoved with top-level comment, remove it from state`() = runTest {
        val state = createState()
        val comment1 = threadedCommentData("comment-1", text = "First comment")
        val comment2 = threadedCommentData("comment-2", text = "Second comment")

        // Set up initial state
        val initialResult =
            PaginationResult(models = listOf(comment1, comment2), pagination = PaginationData.EMPTY)
        state.onQueryMoreReplies(initialResult)

        state.onCommentRemoved("comment-1")

        assertEquals(listOf(comment2), state.replies.value)
    }

    @Test
    fun `on onCommentRemoved with nested reply, remove it recursively`() = runTest {
        val state = createState()
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

        // Set up initial state
        val initialResult =
            PaginationResult(models = listOf(parentComment), pagination = PaginationData.EMPTY)
        state.onQueryMoreReplies(initialResult)

        state.onCommentRemoved("nested-reply")

        // Expected: direct reply with no nested replies and updated count
        val expectedDirectReply = directReply.copy(replies = emptyList(), replyCount = 0)
        val expectedParent = parentComment.copy(replies = listOf(expectedDirectReply))
        assertEquals(listOf(expectedParent), state.replies.value)
    }

    @Test
    fun `on onCommentUpdated, update comment data in nested structure`() = runTest {
        val state = createState()
        val originalComment = threadedCommentData("comment-1", text = "Original text")

        // Set up initial state
        val initialResult =
            PaginationResult(models = listOf(originalComment), pagination = PaginationData.EMPTY)
        state.onQueryMoreReplies(initialResult)

        // Update the comment
        val updatedCommentData = commentData("comment-1", text = "Updated text")

        state.onCommentUpdated(updatedCommentData)

        val expectedComment = originalComment.copy(text = "Updated text")
        assertEquals(listOf(expectedComment), state.replies.value)
    }

    @Test
    fun `on onCommentReactionAdded, add reaction to nested comment`() = runTest {
        val state = createState()
        val comment = threadedCommentData("comment-1", text = "Comment with reaction")

        // Set up initial state
        val initialResult =
            PaginationResult(models = listOf(comment), pagination = PaginationData.EMPTY)
        state.onQueryMoreReplies(initialResult)

        // Add reaction
        val reaction =
            feedsReactionData(activityId = "comment-1", type = "like", userId = currentUserId)

        state.onCommentReactionAdded("comment-1", reaction)

        // Use the actual addReaction method to compute expected state
        val expectedComment = comment.addReaction(reaction, currentUserId)
        assertEquals(listOf(expectedComment), state.replies.value)
    }

    @Test
    fun `on onCommentReactionRemoved, remove reaction from nested comment`() = runTest {
        val state = createState()
        val reaction =
            feedsReactionData(activityId = "comment-1", type = "like", userId = currentUserId)

        // Create comment with reaction already added using the actual method
        val baseComment = threadedCommentData("comment-1", text = "Comment with reaction")
        val commentWithReaction = baseComment.addReaction(reaction, currentUserId)

        // Set up initial state
        val initialResult =
            PaginationResult(
                models = listOf(commentWithReaction),
                pagination = PaginationData.EMPTY,
            )
        state.onQueryMoreReplies(initialResult)

        state.onCommentReactionRemoved("comment-1", reaction)

        // Use the actual removeReaction method to compute expected state
        val expectedComment = commentWithReaction.removeReaction(reaction, currentUserId)
        assertEquals(listOf(expectedComment), state.replies.value)
    }

    @Test
    fun `on onCommentReactionAdded to deeply nested comment, update correct comment`() = runTest {
        val state = createState()
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
            threadedCommentData("parent-1", text = "Parent comment", replies = listOf(directReply))

        // Set up initial state
        val initialResult =
            PaginationResult(models = listOf(parentComment), pagination = PaginationData.EMPTY)
        state.onQueryMoreReplies(initialResult)

        // Add reaction to deeply nested comment
        val reaction =
            feedsReactionData(activityId = "deep-comment", type = "heart", userId = currentUserId)

        state.onCommentReactionAdded("deep-comment", reaction)

        // Build expected nested structure with reaction added using actual method
        val expectedDeepComment = deeplyNestedComment.addReaction(reaction, currentUserId)
        val expectedDirectReply = directReply.copy(replies = listOf(expectedDeepComment))
        val expectedParent = parentComment.copy(replies = listOf(expectedDirectReply))

        assertEquals(listOf(expectedParent), state.replies.value)
    }
}
