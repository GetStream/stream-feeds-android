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

import io.getstream.android.core.api.subscribe.StreamSubscriptionManager
import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.CommentsQuery
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.repository.CommentsRepository
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CommentListImplTest {
    private val commentsRepository: CommentsRepository = mockk()
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener> =
        mockk(relaxed = true)
    private val query = CommentsQuery(filter = null, limit = 10)
    private val currentUserId = "user-id"

    private val commentList =
        CommentListImpl(
            query = query,
            commentsRepository = commentsRepository,
            currentUserId = currentUserId,
            subscriptionManager = subscriptionManager,
        )

    @Test
    fun `on get, then return comments and update state`() = runTest {
        val comments = listOf(commentData("comment-1"), commentData("comment-2"))
        val paginationResult = createPaginationResult(comments, next = "next-cursor")
        coEvery { commentsRepository.queryComments(query) } returns Result.success(paginationResult)

        val result = commentList.get()

        assertEquals(comments, result.getOrNull())
        coVerify { commentsRepository.queryComments(query) }
    }

    @Test
    fun `on queryMoreComments with next cursor, then query with next cursor`() = runTest {
        setupInitialState()

        val moreComments = listOf(commentData("comment-2"), commentData("comment-3"))
        val morePaginationResult =
            createPaginationResult(
                activities = moreComments,
                next = "next-cursor-2",
                previous = "next-cursor",
            )
        coEvery { commentsRepository.queryComments(any()) } returns
            Result.success(morePaginationResult)

        val result = commentList.queryMoreComments()

        assertEquals(moreComments, result.getOrNull())
        coVerify { commentsRepository.queryComments(any()) }
    }

    @Test
    fun `on queryMoreComments with no next cursor, then return empty list`() = runTest {
        setupInitialState(nextCursor = null)

        val result = commentList.queryMoreComments()

        assertEquals(emptyList<CommentData>(), result.getOrNull())
        coVerify(exactly = 1) { commentsRepository.queryComments(any()) }
    }

    @Test
    fun `on queryMoreComments with custom limit, then use custom limit`() = runTest {
        setupInitialState()

        val customLimit = 5
        val moreComments = listOf(commentData("comment-2"))
        val morePaginationResult =
            createPaginationResult(activities = moreComments, previous = "next-cursor")
        coEvery { commentsRepository.queryComments(any()) } returns
            Result.success(morePaginationResult)

        val result = commentList.queryMoreComments(customLimit)

        assertEquals(moreComments, result.getOrNull())
        coVerify { commentsRepository.queryComments(any()) }
    }

    private suspend fun setupInitialState(nextCursor: String? = "next-cursor") {
        val initialComments = listOf(commentData("comment-1"))
        val initialPaginationResult =
            PaginationResult(
                models = initialComments,
                pagination = PaginationData(next = nextCursor, previous = null),
            )
        coEvery { commentsRepository.queryComments(query) } returns
            Result.success(initialPaginationResult)
        commentList.get()
    }

    private fun createPaginationResult(
        activities: List<CommentData>,
        next: String? = null,
        previous: String? = null,
    ) =
        PaginationResult(
            models = activities,
            pagination = PaginationData(next = next, previous = previous),
        )
}
