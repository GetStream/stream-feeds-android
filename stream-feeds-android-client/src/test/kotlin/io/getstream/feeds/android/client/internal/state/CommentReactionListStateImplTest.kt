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

import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.state.query.CommentReactionsQuery
import io.getstream.feeds.android.client.api.state.query.CommentReactionsSort
import io.getstream.feeds.android.client.internal.state.query.CommentReactionsQueryConfig
import io.getstream.feeds.android.client.internal.test.TestData.defaultPaginationResult
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class CommentReactionListStateImplTest {
    private val query = CommentReactionsQuery(commentId = "comment-1", limit = 10)
    private val commentReactionListState = CommentReactionListStateImpl(query)

    @Test
    fun `on initial state, then return empty reactions and null pagination`() = runTest {
        assertEquals(emptyList<FeedsReactionData>(), commentReactionListState.reactions.value)
        assertNull(commentReactionListState.pagination)
    }

    @Test
    fun `on queryMoreReactions, then update reactions and pagination`() = runTest {
        val reactions =
            listOf(feedsReactionData(), feedsReactionData("reaction-2", "comment-1", "user-2"))
        val paginationResult = defaultPaginationResult(reactions)

        commentReactionListState.onQueryMoreReactions(paginationResult, queryConfig)

        assertEquals(reactions, commentReactionListState.reactions.value)
        assertEquals("next-cursor", commentReactionListState.pagination?.next)
        assertEquals(queryConfig, commentReactionListState.queryConfig)
    }

    @Test
    fun `on reactionRemoved, then remove specific reaction`() = runTest {
        val initialReactions =
            listOf(feedsReactionData(), feedsReactionData("reaction-2", "comment-1", "user-2"))
        val paginationResult = defaultPaginationResult(initialReactions)
        commentReactionListState.onQueryMoreReactions(paginationResult, queryConfig)

        commentReactionListState.onReactionRemoved(initialReactions[0])

        val remainingReactions = commentReactionListState.reactions.value
        assertEquals(initialReactions.drop(1), remainingReactions)
    }

    @Test
    fun `on onReactionUpserted for new reaction, then add reaction`() = runTest {
        val initialReaction = feedsReactionData(commentId = "comment-1", userId = "user-1")
        val paginationResult = defaultPaginationResult(listOf(initialReaction))
        commentReactionListState.onQueryMoreReactions(paginationResult, queryConfig)

        val newReaction = feedsReactionData(commentId = "comment-1", userId = "user-2")
        commentReactionListState.onReactionUpserted(newReaction)

        assertEquals(listOf(initialReaction, newReaction), commentReactionListState.reactions.value)
    }

    @Test
    fun `on onCommentRemoved, clear all reactions`() = runTest {
        val reactions =
            listOf(feedsReactionData(), feedsReactionData("reaction-2", "comment-1", "user-2"))
        val paginationResult = defaultPaginationResult(reactions)

        commentReactionListState.onQueryMoreReactions(paginationResult, queryConfig)
        commentReactionListState.onCommentRemoved()

        assertEquals(emptyList<FeedsReactionData>(), commentReactionListState.reactions.value)
    }

    companion object {
        private val queryConfig =
            CommentReactionsQueryConfig(filter = null, sort = CommentReactionsSort.Default)
    }
}
