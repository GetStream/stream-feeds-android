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
import io.getstream.feeds.android.client.api.state.query.ActivityCommentsQuery
import io.getstream.feeds.android.client.api.state.query.CommentsSort
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.threadedCommentData
import java.util.Date
import org.junit.Assert.assertEquals
import org.junit.Test

internal class ActivityCommentListStateImplTest {

    private val query =
        ActivityCommentsQuery(
            objectId = "activity_1",
            objectType = "activity",
            sort = CommentsSort.First,
        )
    private val state = ActivityCommentListStateImpl(query, currentUserId = "user_1")

    @Test
    fun `onQueryMoreComments when called, then merge comments and update pagination`() {
        val batch1 =
            listOf(
                threadedCommentData(id = "c1", createdAt = Date(1)),
                threadedCommentData(id = "c3", createdAt = Date(3)),
            )
        val batch2 =
            listOf(
                threadedCommentData(id = "c2", createdAt = Date(2)),
                threadedCommentData(id = "c4", createdAt = Date(4)),
            )
        val expected =
            listOf(
                threadedCommentData(id = "c1", createdAt = Date(1)),
                threadedCommentData(id = "c2", createdAt = Date(2)),
                threadedCommentData(id = "c3", createdAt = Date(3)),
                threadedCommentData(id = "c4", createdAt = Date(4)),
            )
        val result1 = PaginationResult(models = batch1, pagination = PaginationData("next1"))
        val result2 = PaginationResult(models = batch2, pagination = PaginationData("next2"))

        state.onQueryMoreComments(result1)
        state.onQueryMoreComments(result2)

        assertEquals(result2.pagination, state.pagination)
        assertEquals(expected, state.comments.value)
    }

    @Test
    fun `onCommentAdded when has no parent, then add it at the top-level in the correct position`() {
        val comment1 = threadedCommentData(id = "c1", createdAt = Date(1))
        val comment2 = threadedCommentData(id = "c2", createdAt = Date(2))
        val comment3 = threadedCommentData(id = "c3", createdAt = Date(3))
        val expected = listOf(comment1, comment2, comment3)

        state.onCommentAdded(comment1)
        state.onCommentAdded(comment3)
        state.onCommentAdded(comment2)

        assertEquals(expected, state.comments.value)
    }

    @Test
    fun `onCommentAdded when has a parent, then add it as a child in the correct position`() {
        val parent =
            threadedCommentData(
                id = "c1",
                replies =
                    listOf(
                        threadedCommentData("c2", parentId = "c1", createdAt = Date(2)),
                        threadedCommentData("c4", parentId = "c1", createdAt = Date(4)),
                    ),
            )
        val reply = threadedCommentData("c3", parentId = "c1", createdAt = Date(3))
        val expected =
            threadedCommentData(
                id = "c1",
                replies =
                    listOf(
                        threadedCommentData("c2", parentId = "c1", createdAt = Date(2)),
                        threadedCommentData("c3", parentId = "c1", createdAt = Date(3)),
                        threadedCommentData("c4", parentId = "c1", createdAt = Date(4)),
                    ),
            )

        state.onCommentAdded(parent)
        state.onCommentAdded(reply)

        assertEquals(listOf(expected), state.comments.value)
    }

    @Test
    fun `onCommentUpdated when it's at the top level, then update it in the list`() {
        val existingComment = threadedCommentData(id = "c1", createdAt = Date(1))
        val update = commentData(id = "c1", text = "Updated comment")
        val expected = threadedCommentData(id = "c1", text = "Updated comment", createdAt = Date(1))

        state.onCommentAdded(existingComment)
        state.onCommentUpdated(update)

        assertEquals(listOf(expected), state.comments.value)
    }

    @Test
    fun `onCommentUpdated when it's a reply, then update it in the parent's replies`() {
        val parent =
            threadedCommentData(
                id = "c1",
                replies =
                    listOf(
                        threadedCommentData("c2", createdAt = Date(2)),
                        threadedCommentData("c3", createdAt = Date(3)),
                    ),
            )
        val update = commentData(id = "c2", text = "Updated reply", createdAt = Date(4))
        val expected =
            threadedCommentData(
                id = "c1",
                replies =
                    listOf(
                        threadedCommentData("c3", createdAt = Date(3)),
                        threadedCommentData("c2", text = "Updated reply", createdAt = Date(4)),
                    ),
            )

        state.onCommentAdded(parent)
        state.onCommentUpdated(update)

        assertEquals(listOf(expected), state.comments.value)
    }
}
