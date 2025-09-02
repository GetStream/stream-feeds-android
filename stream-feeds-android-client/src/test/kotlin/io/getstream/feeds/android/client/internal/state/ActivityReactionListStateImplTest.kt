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
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.state.query.ActivityReactionsFilterField
import io.getstream.feeds.android.client.api.state.query.ActivityReactionsQuery
import io.getstream.feeds.android.client.api.state.query.ActivityReactionsSort
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
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
            listOf(feedsReactionData(), feedsReactionData("reaction-2", "activity-1", "user-2"))
        val paginationResult =
            PaginationResult(
                models = reactions,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig =
            QueryConfiguration<ActivityReactionsFilterField, ActivityReactionsSort>(
                filter = null,
                sort = ActivityReactionsSort.Default,
            )

        activityReactionListState.onQueryMoreActivityReactions(paginationResult, queryConfig)

        assertEquals(reactions, activityReactionListState.reactions.value)
        assertEquals("next-cursor", activityReactionListState.pagination?.next)
        assertEquals(queryConfig, activityReactionListState.queryConfig)
    }

    @Test
    fun `on reactionRemoved, then remove specific reaction`() = runTest {
        val initialReactions =
            listOf(feedsReactionData(), feedsReactionData("reaction-2", "activity-1", "user-2"))
        val paginationResult =
            PaginationResult(
                models = initialReactions,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig =
            QueryConfiguration<ActivityReactionsFilterField, ActivityReactionsSort>(
                filter = null,
                sort = ActivityReactionsSort.Default,
            )
        activityReactionListState.onQueryMoreActivityReactions(paginationResult, queryConfig)

        activityReactionListState.onReactionRemoved(initialReactions[0])

        val remainingReactions = activityReactionListState.reactions.value
        assertEquals(listOf(initialReactions[1]), remainingReactions)
    }
}
