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

package io.getstream.feeds.android.client.internal.state

import io.getstream.android.core.api.subscribe.StreamSubscriptionManager
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.state.query.CommentRepliesQuery
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

internal class CommentReplyListImplTest {
    private val commentsRepository: CommentsRepository = mockk()
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener> =
        mockk(relaxed = true)
    private val currentUserId = "user-1"
    private val query = CommentRepliesQuery(commentId = "comment-1", limit = 10)

    private val commentReplyList =
        CommentReplyListImpl(
            query = query,
            currentUserId = currentUserId,
            commentsRepository = commentsRepository,
            subscriptionManager = subscriptionManager,
        )

    @Test
    fun `on get, then return replies and update state`() = runTest {
        val replies = listOf(threadedCommentData("reply-1"), threadedCommentData("reply-2"))
        val paginationResult = createPaginationResult(replies, next = "next-cursor")
        coEvery { commentsRepository.getCommentReplies(query) } returns
            Result.success(paginationResult)

        val result = commentReplyList.get()

        assertEquals(replies, result.getOrNull())
        coVerify { commentsRepository.getCommentReplies(query) }
    }

    @Test
    fun `on queryMoreReplies with next cursor, then query with next cursor`() = runTest {
        setupInitialState()

        val moreReplies = listOf(threadedCommentData("reply-2"), threadedCommentData("reply-3"))
        val morePaginationResult =
            createPaginationResult(
                replies = moreReplies,
                next = "next-cursor-2",
                previous = "next-cursor",
            )
        coEvery { commentsRepository.getCommentReplies(any()) } returns
            Result.success(morePaginationResult)

        val result = commentReplyList.queryMoreReplies()

        assertEquals(moreReplies, result.getOrNull())
        coVerify { commentsRepository.getCommentReplies(any()) }
    }

    @Test
    fun `on queryMoreReplies with no next cursor, then return empty list`() = runTest {
        setupInitialState(nextCursor = null)

        val result = commentReplyList.queryMoreReplies()

        assertEquals(emptyList<ThreadedCommentData>(), result.getOrNull())
        coVerify(exactly = 1) { commentsRepository.getCommentReplies(any()) }
    }

    @Test
    fun `on queryMoreReplies with custom limit, then use custom limit`() = runTest {
        setupInitialState()

        val customLimit = 5
        val moreReplies = listOf(threadedCommentData("reply-2"))
        val morePaginationResult =
            createPaginationResult(replies = moreReplies, previous = "next-cursor")
        coEvery { commentsRepository.getCommentReplies(any()) } returns
            Result.success(morePaginationResult)

        val result = commentReplyList.queryMoreReplies(customLimit)

        assertEquals(moreReplies, result.getOrNull())
        coVerify { commentsRepository.getCommentReplies(any()) }
    }

    private suspend fun setupInitialState(nextCursor: String? = "next-cursor") {
        val initialReplies = listOf(threadedCommentData("reply-1"))
        val initialPaginationResult =
            PaginationResult(
                models = initialReplies,
                pagination = PaginationData(next = nextCursor, previous = null),
            )
        coEvery { commentsRepository.getCommentReplies(query) } returns
            Result.success(initialPaginationResult)
        commentReplyList.get()
    }

    private fun createPaginationResult(
        replies: List<ThreadedCommentData>,
        next: String? = null,
        previous: String? = null,
    ) =
        PaginationResult(
            models = replies,
            pagination = PaginationData(next = next, previous = previous),
        )
}
