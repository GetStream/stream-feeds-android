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
import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.state.query.FollowsQuery
import io.getstream.feeds.android.client.internal.repository.FeedsRepository
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.getstream.feeds.android.client.internal.test.TestData.followData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class FollowListImplTest {
    private val feedsRepository: FeedsRepository = mockk()
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener> =
        mockk(relaxed = true)
    private val query = FollowsQuery(limit = 10)

    private val followList =
        FollowListImpl(
            query = query,
            feedsRepository = feedsRepository,
            subscriptionManager = subscriptionManager,
        )

    @Test
    fun `on get, then return follows and update state`() = runTest {
        val follows = listOf(followData(), followData())
        val paginationResult = createPaginationResult(follows, next = "next-cursor")
        coEvery { feedsRepository.queryFollows(any()) } returns Result.success(paginationResult)

        val result = followList.get()

        assertEquals(follows, result.getOrNull())
        coVerify { feedsRepository.queryFollows(any()) }
    }

    @Test
    fun `on queryMoreFollows with next cursor, then query with next cursor`() = runTest {
        setupInitialState()

        val moreFollows = listOf(followData(), followData())
        val morePaginationResult =
            createPaginationResult(
                follows = moreFollows,
                next = "next-cursor-2",
                previous = "next-cursor",
            )
        coEvery { feedsRepository.queryFollows(any()) } returns Result.success(morePaginationResult)

        val result = followList.queryMoreFollows()

        assertEquals(moreFollows, result.getOrNull())
        coVerify { feedsRepository.queryFollows(any()) }
    }

    @Test
    fun `on queryMoreFollows with no next cursor, then return empty list`() = runTest {
        setupInitialState(nextCursor = null)

        val result = followList.queryMoreFollows()

        assertEquals(emptyList<FollowData>(), result.getOrNull())
        coVerify(exactly = 1) { feedsRepository.queryFollows(any()) }
    }

    @Test
    fun `on queryMoreFollows with custom limit, then use custom limit`() = runTest {
        setupInitialState()

        val customLimit = 5
        val moreFollows = listOf(followData())
        val morePaginationResult =
            createPaginationResult(follows = moreFollows, previous = "next-cursor")
        coEvery { feedsRepository.queryFollows(any()) } returns Result.success(morePaginationResult)

        val result = followList.queryMoreFollows(customLimit)

        assertEquals(moreFollows, result.getOrNull())
        coVerify { feedsRepository.queryFollows(any()) }
    }

    private suspend fun setupInitialState(nextCursor: String? = "next-cursor") {
        val initialFollows = listOf(followData())
        val initialPaginationResult =
            PaginationResult(
                models = initialFollows,
                pagination = PaginationData(next = nextCursor, previous = null),
            )
        coEvery { feedsRepository.queryFollows(any()) } returns
            Result.success(initialPaginationResult)
        followList.get()
    }

    private fun createPaginationResult(
        follows: List<FollowData>,
        next: String? = null,
        previous: String? = null,
    ) =
        PaginationResult(
            models = follows,
            pagination = PaginationData(next = next, previous = previous),
        )
}
