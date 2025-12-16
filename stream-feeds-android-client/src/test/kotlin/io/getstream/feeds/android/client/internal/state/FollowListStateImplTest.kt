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
import io.getstream.feeds.android.client.api.model.ModelUpdates
import io.getstream.feeds.android.client.api.state.query.FollowsQuery
import io.getstream.feeds.android.client.api.state.query.FollowsSort
import io.getstream.feeds.android.client.internal.state.query.FollowsQueryConfig
import io.getstream.feeds.android.client.internal.test.TestData.defaultPaginationResult
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
        val paginationResult = defaultPaginationResult(follows)

        followListState.onQueryMoreFollows(paginationResult, queryConfig)

        assertEquals(follows, followListState.follows.value)
        assertEquals("next-cursor", followListState.pagination?.next)
        assertEquals(queryConfig, followListState.queryConfig)
    }

    @Test
    fun `on followUpserted, then update specific follow`() = runTest {
        val initialFollows = listOf(followData(), followData("user-2", "user-3"))
        val paginationResult = defaultPaginationResult(initialFollows)
        followListState.onQueryMoreFollows(paginationResult, queryConfig)

        val updatedFollow =
            followData(sourceFid = "user:user-1", targetFid = "user:user-2", createdAt = 1000)
        followListState.onFollowUpserted(updatedFollow)

        val updatedFollows = followListState.follows.value
        assertEquals(updatedFollow, updatedFollows.find { it.id == updatedFollow.id })
        assertEquals(initialFollows[1], updatedFollows.find { it.id == initialFollows[1].id })
    }

    @Test
    fun `on followUpserted with non-existent follow, then insert in sorted position`() = runTest {
        val follow1 = followData("user-1", "user-2", createdAt = 2000)
        val follow2 = followData("user-2", "user-3", createdAt = 1000)
        val initialFollows = listOf(follow1, follow2)
        val paginationResult = defaultPaginationResult(initialFollows)
        followListState.onQueryMoreFollows(paginationResult, queryConfig)

        val newFollow = followData("user-4", "user-5", createdAt = 1500)
        followListState.onFollowUpserted(newFollow)

        // Expect follows to be sorted by createdAt in descending order (newest first)
        val expectedFollows = listOf(follow1, newFollow, follow2)
        assertEquals(expectedFollows, followListState.follows.value)
    }

    @Test
    fun `on followRemoved, then remove specific follow`() = runTest {
        val initialFollows = listOf(followData(), followData("user-2", "user-3"))
        val paginationResult = defaultPaginationResult(initialFollows)
        followListState.onQueryMoreFollows(paginationResult, queryConfig)

        followListState.onFollowRemoved(initialFollows[0])

        val remainingFollows = followListState.follows.value
        assertEquals(listOf(initialFollows[1]), remainingFollows)
    }

    @Test
    fun `on onFollowsUpdated with mixed operations, apply all updates and maintain sort`() =
        runTest {
            val follow1 = followData("user-1", "user-2", createdAt = 3000)
            val follow2 = followData("user-2", "user-3", createdAt = 2000)
            val follow3 = followData("user-3", "user-4", createdAt = 1000)
            val initialFollows = listOf(follow1, follow2, follow3)
            val paginationResult = defaultPaginationResult(initialFollows)
            followListState.onQueryMoreFollows(paginationResult, queryConfig)

            val newFollow = followData("user-5", "user-6", createdAt = 2500)
            val updatedFollow2 = follow2.copy(pushPreference = "disabled")
            val updates =
                ModelUpdates(
                    added = listOf(newFollow),
                    updated = listOf(updatedFollow2),
                    removedIds = setOf(follow3.id),
                )

            followListState.onFollowsUpdated(updates)

            val expectedFollows = listOf(follow1, newFollow, updatedFollow2)
            assertEquals(expectedFollows, followListState.follows.value)
        }

    companion object {
        private val queryConfig = FollowsQueryConfig(filter = null, sort = FollowsSort.Default)
    }
}
