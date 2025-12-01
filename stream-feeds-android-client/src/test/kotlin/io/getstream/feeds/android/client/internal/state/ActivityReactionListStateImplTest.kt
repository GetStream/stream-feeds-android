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
import io.getstream.feeds.android.client.api.state.query.ActivityReactionsQuery
import io.getstream.feeds.android.client.api.state.query.ActivityReactionsSort
import io.getstream.feeds.android.client.internal.state.query.ActivityReactionsQueryConfig
import io.getstream.feeds.android.client.internal.test.TestData.defaultPaginationResult
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import java.util.Date
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class ActivityReactionListStateImplTest {
    private val query = ActivityReactionsQuery(activityId = "activity-1", limit = 10)
    private val activityReactionListState = ActivityReactionListStateImpl(query)

    @Test
    fun `on initial state, then return empty reactions and null pagination`() = runTest {
        assertEquals(emptyList<FeedsReactionData>(), activityReactionListState.reactions.value)
        assertNull(activityReactionListState.pagination)
    }

    @Test
    fun `on queryMoreActivityReactions, then update reactions and pagination`() = runTest {
        val reactions =
            listOf(
                feedsReactionData(activityId = "activity-1", userId = "user-1"),
                feedsReactionData(activityId = "activity-1", userId = "user-2"),
            )
        val paginationResult = defaultPaginationResult(reactions)

        activityReactionListState.onQueryMoreActivityReactions(paginationResult, queryConfig)

        assertEquals(reactions, activityReactionListState.reactions.value)
        assertEquals("next-cursor", activityReactionListState.pagination?.next)
        assertEquals(queryConfig, activityReactionListState.queryConfig)
    }

    @Test
    fun `on reactionRemoved, then remove specific reaction`() = runTest {
        val initialReactions =
            listOf(
                feedsReactionData(activityId = "activity-1", userId = "user-1"),
                feedsReactionData(activityId = "activity-1", userId = "user-2"),
            )
        val paginationResult = defaultPaginationResult(initialReactions)
        activityReactionListState.onQueryMoreActivityReactions(paginationResult, queryConfig)

        activityReactionListState.onReactionRemoved(initialReactions[0])

        val remainingReactions = activityReactionListState.reactions.value
        assertEquals(listOf(initialReactions[1]), remainingReactions)
    }

    @Test
    fun `on onReactionUpserted for new reaction, then insert in sorted order`() = runTest {
        val olderReaction =
            feedsReactionData(activityId = "activity-1", userId = "user-1", createdAt = Date(1000))
        val newerReaction =
            feedsReactionData(activityId = "activity-1", userId = "user-3", createdAt = Date(3000))
        val initialReactions = listOf(newerReaction, olderReaction) // newest first
        val paginationResult = defaultPaginationResult(initialReactions)
        activityReactionListState.onQueryMoreActivityReactions(paginationResult, queryConfig)

        val middleReaction =
            feedsReactionData(activityId = "activity-1", userId = "user-2", createdAt = Date(2000))
        activityReactionListState.onReactionUpserted(middleReaction, enforceUnique = false)

        // The reaction is inserted in sorted position (newest first)
        val expectedOrder = listOf(newerReaction, middleReaction, olderReaction)
        assertEquals(expectedOrder, activityReactionListState.reactions.value)
    }

    @Test
    fun `on onReactionUpserted with enforceUnique true, replace existing user reactions and keep sort order`() =
        runTest {
            val existingReactions =
                listOf(
                    feedsReactionData(
                        commentId = "comment-1",
                        type = "like",
                        userId = "another-user",
                        createdAt = Date(4000),
                    ),
                    feedsReactionData(
                        commentId = "comment-1",
                        type = "heart",
                        userId = "the-user",
                        createdAt = Date(3000),
                    ),
                    feedsReactionData(
                        commentId = "comment-1",
                        type = "like",
                        userId = "the-user",
                        createdAt = Date(2000),
                    ),
                )

            val paginationResult = defaultPaginationResult(existingReactions)
            activityReactionListState.onQueryMoreActivityReactions(paginationResult, queryConfig)

            val newReaction =
                feedsReactionData(
                    commentId = "comment-1",
                    type = "smile",
                    userId = "the-user",
                    createdAt = Date(5000),
                )

            activityReactionListState.onReactionUpserted(newReaction, enforceUnique = true)

            val expected = listOf(newReaction, existingReactions.first())
            assertEquals(expected, activityReactionListState.reactions.value)
        }

    @Test
    fun `on onActivityRemoved, clear all reactions`() = runTest {
        val reactions =
            listOf(
                feedsReactionData(activityId = "activity-1", userId = "user-1"),
                feedsReactionData(activityId = "activity-1", userId = "user-2"),
            )
        val paginationResult = defaultPaginationResult(reactions)

        activityReactionListState.onQueryMoreActivityReactions(paginationResult, queryConfig)
        activityReactionListState.onActivityRemoved()

        assertEquals(emptyList<FeedsReactionData>(), activityReactionListState.reactions.value)
    }

    companion object {
        private val queryConfig =
            ActivityReactionsQueryConfig(filter = null, sort = ActivityReactionsSort.Default)
    }
}
