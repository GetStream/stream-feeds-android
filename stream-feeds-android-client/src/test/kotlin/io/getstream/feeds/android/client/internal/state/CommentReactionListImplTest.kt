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
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.state.query.CommentReactionsQuery
import io.getstream.feeds.android.client.internal.repository.CommentsRepository
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CommentReactionListImplTest {
    private val commentsRepository: CommentsRepository = mockk()
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener> =
        mockk(relaxed = true)
    private val query = CommentReactionsQuery(commentId = "comment-1", limit = 10)

    private val commentReactionList =
        CommentReactionListImpl(
            query = query,
            commentsRepository = commentsRepository,
            subscriptionManager = subscriptionManager,
        )

    @Test
    fun `on get, then return reactions and update state`() = runTest {
        val reactions = listOf(feedsReactionData(), feedsReactionData())
        val paginationResult = createPaginationResult(reactions, next = "next-cursor")
        coEvery { commentsRepository.queryCommentReactions(query.commentId, query) } returns
            Result.success(paginationResult)

        val result = commentReactionList.get()

        assertEquals(reactions, result.getOrNull())
        coVerify { commentsRepository.queryCommentReactions(query.commentId, query) }
    }

    @Test
    fun `on queryMoreReactions with next cursor, then query with next cursor`() = runTest {
        setupInitialState()

        val moreReactions = listOf(feedsReactionData(), feedsReactionData())
        val morePaginationResult =
            createPaginationResult(
                reactions = moreReactions,
                next = "next-cursor-2",
                previous = "next-cursor",
            )
        coEvery { commentsRepository.queryCommentReactions(any(), any()) } returns
            Result.success(morePaginationResult)

        val result = commentReactionList.queryMoreReactions()

        assertEquals(moreReactions, result.getOrNull())
        coVerify { commentsRepository.queryCommentReactions(any(), any()) }
    }

    @Test
    fun `on queryMoreReactions with no next cursor, then return empty list`() = runTest {
        setupInitialState(nextCursor = null)

        val result = commentReactionList.queryMoreReactions()

        assertEquals(emptyList<FeedsReactionData>(), result.getOrNull())
        coVerify(exactly = 1) { commentsRepository.queryCommentReactions(any(), any()) }
    }

    @Test
    fun `on queryMoreReactions with custom limit, then use custom limit`() = runTest {
        setupInitialState()

        val customLimit = 5
        val moreReactions = listOf(feedsReactionData())
        val morePaginationResult =
            createPaginationResult(reactions = moreReactions, previous = "next-cursor")
        coEvery { commentsRepository.queryCommentReactions(any(), any()) } returns
            Result.success(morePaginationResult)

        val result = commentReactionList.queryMoreReactions(customLimit)

        assertEquals(moreReactions, result.getOrNull())
        coVerify { commentsRepository.queryCommentReactions(any(), any()) }
    }

    private suspend fun setupInitialState(nextCursor: String? = "next-cursor") {
        val initialReactions = listOf(feedsReactionData())
        val initialPaginationResult =
            PaginationResult(
                models = initialReactions,
                pagination = PaginationData(next = nextCursor, previous = null),
            )
        coEvery { commentsRepository.queryCommentReactions(query.commentId, query) } returns
            Result.success(initialPaginationResult)
        commentReactionList.get()
    }

    private fun createPaginationResult(
        reactions: List<FeedsReactionData>,
        next: String? = null,
        previous: String? = null,
    ) =
        PaginationResult(
            models = reactions,
            pagination = PaginationData(next = next, previous = previous),
        )
}
