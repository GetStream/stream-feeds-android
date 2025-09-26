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
import io.getstream.feeds.android.client.api.state.query.CommentsQuery
import io.getstream.feeds.android.client.api.state.query.CommentsSort
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import java.util.Date
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CommentListStateImplTest {

    private val query = CommentsQuery(null, sort = CommentsSort.First)
    private val state = CommentListStateImpl(query)

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
        val result =
            PaginationResult(listOf(comment1, comment2), PaginationData("next", "previous"))
        val updatedComment2 = comment2.copy(text = "Updated Second", createdAt = Date(2))
        val expected = listOf(updatedComment2, comment1)

        state.onQueryMoreComments(result)
        state.onCommentUpdated(updatedComment2)

        assertEquals(expected, state.comments.value)
    }

    @Test
    fun `on onCommentUpdated, then preserve ownReactions when updating comment`() {
        val ownReaction = feedsReactionData("activity-1", "like", "current-user")

        val originalComment =
            commentData("comment-1", "Original text", ownReactions = listOf(ownReaction))
        val result = PaginationResult(listOf(originalComment), PaginationData("next", "previous"))

        val updatedComment = commentData("comment-1", "Updated text", ownReactions = emptyList())

        state.onQueryMoreComments(result)
        state.onCommentUpdated(updatedComment)

        val expectedComment = updatedComment.copy(ownReactions = listOf(ownReaction))
        assertEquals(listOf(expectedComment), state.comments.value)
    }

    @Test
    fun `on commentRemoved from top level, then remove specific comment`() {
        val comment1 = commentData("1", text = "First", createdAt = Date(1))
        val comment2 = commentData("2", text = "Second", createdAt = Date(2))
        val comment3 = commentData("3", text = "Third", createdAt = Date(3))
        val result =
            PaginationResult(
                models = listOf(comment1, comment2, comment3),
                pagination = PaginationData("next", "previous"),
            )

        state.onQueryMoreComments(result)
        state.onCommentRemoved("2")

        val expected = listOf(comment1, comment3)
        assertEquals(expected, state.comments.value)
    }

    @Test
    fun `on commentRemoved from nested reply, then remove reply and update parent reply count`() {
        val reply1 = commentData("reply1", text = "Reply 1", createdAt = Date(2))
        val reply2 = commentData("reply2", text = "Reply 2", createdAt = Date(3))
        val reply3 = commentData("reply3", text = "Reply 3", createdAt = Date(4))
        val parentComment =
            commentData("parent", text = "Parent", createdAt = Date(1))
                .copy(replies = listOf(reply1, reply2, reply3), replyCount = 3)

        val result = PaginationResult(models = listOf(parentComment), pagination = PaginationData())

        state.onQueryMoreComments(result)
        state.onCommentRemoved("reply2")

        val expectedParent = parentComment.copy(replies = listOf(reply1, reply3), replyCount = 2)
        assertEquals(listOf(expectedParent), state.comments.value)
    }
}
