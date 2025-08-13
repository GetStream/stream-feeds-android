package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.state.query.CommentsQuery
import io.getstream.feeds.android.client.api.state.query.CommentsSort
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

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
        val result = PaginationResult(listOf(comment1, comment2), PaginationData("next", "previous"))
        val updatedComment2 = comment2.copy(text = "Updated Second", createdAt = Date(2))
        val expected = listOf(updatedComment2, comment1)

        state.onQueryMoreComments(result)
        state.onCommentUpdated(updatedComment2)

        assertEquals(expected, state.comments.value)
    }
}
