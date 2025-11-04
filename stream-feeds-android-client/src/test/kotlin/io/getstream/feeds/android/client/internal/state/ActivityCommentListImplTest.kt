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
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.state.query.ActivityCommentsQuery
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.repository.CommentsRepository
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.getstream.feeds.android.client.internal.test.TestData.threadedCommentData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class ActivityCommentListImplTest {
    private val commentsRepository: CommentsRepository = mockk()
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener> =
        mockk(relaxed = true)
    private val currentUserId = "user-1"
    private val query =
        ActivityCommentsQuery(objectId = "activity-1", objectType = "activity", limit = 10)

    private val activityCommentList =
        ActivityCommentListImpl(
            query = query,
            currentUserId = currentUserId,
            commentsRepository = commentsRepository,
            subscriptionManager = subscriptionManager,
        )

    @Test
    fun `on get, then return comments and update state`() = runTest {
        val comments = listOf(threadedCommentData("comment-1"), threadedCommentData("comment-2"))
        val paginationResult = createPaginationResult(comments, next = "next-cursor")
        coEvery { commentsRepository.getComments(query) } returns Result.success(paginationResult)

        val result = activityCommentList.get()

        assertEquals(comments, result.getOrNull())
        coVerify { commentsRepository.getComments(query) }
    }

    @Test
    fun `on queryMoreComments with next cursor, then query with next cursor`() = runTest {
        setupInitialState()

        val moreComments =
            listOf(threadedCommentData("comment-3"), threadedCommentData("comment-4"))
        val morePaginationResult =
            createPaginationResult(
                comments = moreComments,
                next = "next-cursor-2",
                previous = "next-cursor",
            )
        coEvery { commentsRepository.getComments(any()) } returns
            Result.success(morePaginationResult)

        val result = activityCommentList.queryMoreComments()

        assertEquals(moreComments, result.getOrNull())
        coVerify { commentsRepository.getComments(any()) }
    }

    @Test
    fun `on queryMoreComments with no next cursor, then return empty list`() = runTest {
        setupInitialState(nextCursor = null)

        val result = activityCommentList.queryMoreComments()

        assertEquals(emptyList<ThreadedCommentData>(), result.getOrNull())
        coVerify(exactly = 1) { commentsRepository.getComments(any()) }
    }

    @Test
    fun `on queryMoreComments with custom limit, then use custom limit`() = runTest {
        setupInitialState()

        val customLimit = 5
        val moreComments = listOf(threadedCommentData("comment-3"))
        val morePaginationResult =
            createPaginationResult(comments = moreComments, previous = "next-cursor")
        coEvery { commentsRepository.getComments(any()) } returns
            Result.success(morePaginationResult)

        val result = activityCommentList.queryMoreComments(customLimit)

        assertEquals(moreComments, result.getOrNull())
        coVerify { commentsRepository.getComments(any()) }
    }

    private suspend fun setupInitialState(nextCursor: String? = "next-cursor") {
        val initialComments = listOf(threadedCommentData("comment-1"))
        val initialPaginationResult =
            PaginationResult(
                models = initialComments,
                pagination = PaginationData(next = nextCursor, previous = null),
            )
        coEvery { commentsRepository.getComments(query) } returns
            Result.success(initialPaginationResult)
        activityCommentList.get()
    }

    private fun createPaginationResult(
        comments: List<ThreadedCommentData>,
        next: String? = null,
        previous: String? = null,
    ) =
        PaginationResult(
            models = comments,
            pagination = PaginationData(next = next, previous = previous),
        )
}
