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

import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.state.query.FollowsFilterField
import io.getstream.feeds.android.client.api.state.query.FollowsQuery
import io.getstream.feeds.android.client.api.state.query.FollowsSort
import io.getstream.feeds.android.client.internal.test.TestData.followData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class FollowListStateImplTest {
    private val query = FollowsQuery(limit = 10)
    private val followListState = FollowListStateImpl(query)

    @Test
    fun `on initial state, then return empty follows and null pagination`() = runTest {
        assertEquals(emptyList<FollowData>(), followListState.follows.value)
        assertNull(followListState.pagination)
    }

    @Test
    fun `on queryMoreFollows, then update follows and pagination`() = runTest {
        val follows = listOf(followData(), followData("user-2", "user-3"))
        val paginationResult =
            PaginationResult(
                models = follows,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig =
            QueryConfiguration<FollowsFilterField, FollowsSort>(
                filter = null,
                sort = FollowsSort.Default,
            )

        followListState.onQueryMoreFollows(paginationResult, queryConfig)

        assertEquals(follows, followListState.follows.value)
        assertEquals("next-cursor", followListState.pagination?.next)
        assertEquals(queryConfig, followListState.queryConfig)
    }

    @Test
    fun `on followUpdated, then update specific follow`() = runTest {
        val initialFollows = listOf(followData(), followData("user-2", "user-3"))
        val paginationResult =
            PaginationResult(
                models = initialFollows,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig =
            QueryConfiguration<FollowsFilterField, FollowsSort>(
                filter = null,
                sort = FollowsSort.Default,
            )
        followListState.onQueryMoreFollows(paginationResult, queryConfig)

        val updatedFollow =
            followData(
                sourceUserId = "user-1",
                targetUserId = "user-2",
                createdAt = java.util.Date(1000),
            )
        followListState.onFollowUpdated(updatedFollow)

        val updatedFollows = followListState.follows.value
        assertEquals(updatedFollow, updatedFollows.find { it.id == updatedFollow.id })
        assertEquals(initialFollows[1], updatedFollows.find { it.id == initialFollows[1].id })
    }

    @Test
    fun `on followUpdated with non-existent follow, then keep existing follows unchanged`() =
        runTest {
            val initialFollows = listOf(followData(), followData("user-2", "user-3"))
            val paginationResult =
                PaginationResult(
                    models = initialFollows,
                    pagination = PaginationData(next = "next-cursor", previous = null),
                )
            val queryConfig =
                QueryConfiguration<FollowsFilterField, FollowsSort>(
                    filter = null,
                    sort = FollowsSort.Default,
                )
            followListState.onQueryMoreFollows(paginationResult, queryConfig)

            val nonExistentFollow = followData("user-4", "user-5")
            followListState.onFollowUpdated(nonExistentFollow)

            assertEquals(initialFollows, followListState.follows.value)
        }

    @Test
    fun `on followRemoved, then remove specific follow`() = runTest {
        val initialFollows = listOf(followData(), followData("user-2", "user-3"))
        val paginationResult =
            PaginationResult(
                models = initialFollows,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig =
            QueryConfiguration<FollowsFilterField, FollowsSort>(
                filter = null,
                sort = FollowsSort.Default,
            )
        followListState.onQueryMoreFollows(paginationResult, queryConfig)

        followListState.onFollowRemoved(initialFollows[0])

        val remainingFollows = followListState.follows.value
        assertEquals(listOf(initialFollows[1]), remainingFollows)
    }
}
