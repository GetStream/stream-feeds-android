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

import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.CommentsQuery
import io.getstream.feeds.android.client.api.state.query.CommentsSort
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import java.util.Date
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CommentListStateImplTest {

    private val query = CommentsQuery(null, sort = CommentsSort.First)
    private val currentUserId = "user-id"
    private val state = CommentListStateImpl(query, currentUserId)

    @Test
    fun `onQueryMoreComments, merge comments and update pagination`() {
        val comment1 = commentData("1", text = "First", createdAt = Date(1))
        val comment2 = commentData("2", text = "Second", createdAt = Date(2))
        val comment3 = commentData("3", text = "Third", createdAt = Date(3))
        val pagination = PaginationData("next", "previous")
        val result1 = PaginationResult(models = listOf(comment1, comment2), pagination = pagination)
        val result2 = PaginationResult(models = listOf(comment2, comment3), pagination = pagination)
        val expected = listOf(comment1, comment2, comment3)

        state.onQueryMoreComments(result1)
        state.onQueryMoreComments(result2)

        assertEquals(expected, state.comments.value)
        assertEquals(pagination, state.pagination)
    }

    @Test
    fun `onCommentUpdated, update the comment in the list`() {
        val comment1 = commentData("1", text = "First", createdAt = Date(3))
        val comment2 = commentData("2", text = "Second", createdAt = Date(4))
        val updatedComment2 = comment2.copy(text = "Updated Second", createdAt = Date(2))
        setupInitialComments(comment1, comment2)

        state.onCommentUpserted(updatedComment2)

        assertEquals(listOf(updatedComment2, comment1), state.comments.value)
    }

    @Test
    fun `on onCommentUpdated, then preserve ownReactions when updating comment`() {
        val ownReaction = feedsReactionData("activity-1", "like", "current-user")
        val originalComment =
            commentData("comment-1", "Original text", ownReactions = listOf(ownReaction))
        val updatedComment = commentData("comment-1", "Updated text", ownReactions = emptyList())
        setupInitialComments(originalComment)

        state.onCommentUpserted(updatedComment)

        val expectedComment = updatedComment.copy(ownReactions = listOf(ownReaction))
        assertEquals(listOf(expectedComment), state.comments.value)
    }

    @Test
    fun `on onCommentRemoved, remove specific comment`() {
        val comment1 = commentData("1", text = "First", createdAt = Date(1))
        val comment2 = commentData("2", text = "Second", createdAt = Date(2))
        val comment3 = commentData("3", text = "Third", createdAt = Date(3))
        setupInitialComments(comment1, comment2, comment3)

        state.onCommentRemoved("2")

        val expected = listOf(comment1, comment3)
        assertEquals(expected, state.comments.value)
    }

    @Test
    fun `on onCommentReactionRemoved, remove reaction`() {
        val reaction = feedsReactionData(commentId = "comment-1", userId = currentUserId)
        val comment = commentData("comment-1", text = "Original", ownReactions = listOf(reaction))
        setupInitialComments(comment)

        val updatedComment = commentData("comment-1", text = "Updated")
        state.onCommentReactionRemoved(updatedComment, reaction)

        val expectedComment = updatedComment.copy(ownReactions = emptyList())
        assertEquals(listOf(expectedComment), state.comments.value)
    }

    @Test
    fun `on onCommentReactionUpserted, upsert reaction`() {
        val reaction = feedsReactionData(commentId = "comment-1", userId = currentUserId)
        val comment = commentData("comment-1", text = "Original")
        setupInitialComments(comment)

        val updatedComment = commentData("comment-1", text = "Updated")
        state.onCommentReactionUpserted(updatedComment, reaction, enforceUnique = false)

        val expectedComment = updatedComment.copy(ownReactions = listOf(reaction))
        assertEquals(listOf(expectedComment), state.comments.value)
    }

    @Test
    fun `on onCommentReactionUpserted with enforceUnique true, then replace all existing user reactions with single new one`() {
        val existingReactions =
            listOf(
                feedsReactionData(commentId = "comment-1", type = "like", userId = currentUserId),
                feedsReactionData(commentId = "comment-1", type = "heart", userId = currentUserId),
            )

        val comment = commentData("comment-1", ownReactions = existingReactions)
        val update = commentData("comment-1", ownReactions = existingReactions)

        setupInitialComments(comment)

        val newReaction =
            feedsReactionData(commentId = "comment-1", type = "smile", userId = currentUserId)

        state.onCommentReactionUpserted(update, newReaction, enforceUnique = true)

        val expectedComment = update.copy(ownReactions = listOf(newReaction))
        assertEquals(listOf(expectedComment), state.comments.value)
    }

    private fun setupInitialComments(vararg comments: CommentData) {
        val result = PaginationResult(comments.toList(), PaginationData("next", "previous"))
        state.onQueryMoreComments(result)
    }
}
