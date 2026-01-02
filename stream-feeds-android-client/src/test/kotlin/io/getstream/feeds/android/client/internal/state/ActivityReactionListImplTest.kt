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
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.ActivityReactionsQuery
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.repository.ActivitiesRepository
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class ActivityReactionListImplTest {
    private val activitiesRepository: ActivitiesRepository = mockk()
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener> =
        mockk(relaxed = true)
    private val query = ActivityReactionsQuery(activityId = "activity-1", limit = 10)

    private val activityReactionList =
        ActivityReactionListImpl(
            query = query,
            activitiesRepository = activitiesRepository,
            subscriptionManager = subscriptionManager,
        )

    @Test
    fun `on get, then return reactions and update state`() = runTest {
        val reactions = listOf(feedsReactionData(), feedsReactionData())
        val paginationResult = createPaginationResult(reactions, next = "next-cursor")
        coEvery { activitiesRepository.queryActivityReactions(query.activityId, any()) } returns
            Result.success(paginationResult)

        val result = activityReactionList.get()

        assertEquals(reactions, result.getOrNull())
        coVerify { activitiesRepository.queryActivityReactions(query.activityId, any()) }
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
        coEvery { activitiesRepository.queryActivityReactions(any(), any()) } returns
            Result.success(morePaginationResult)

        val result = activityReactionList.queryMoreReactions()

        assertEquals(moreReactions, result.getOrNull())
        coVerify { activitiesRepository.queryActivityReactions(any(), any()) }
    }

    @Test
    fun `on queryMoreReactions with no next cursor, then return empty list`() = runTest {
        setupInitialState(nextCursor = null)

        val result = activityReactionList.queryMoreReactions()

        assertEquals(emptyList<FeedsReactionData>(), result.getOrNull())
        coVerify(exactly = 1) { activitiesRepository.queryActivityReactions(any(), any()) }
    }

    @Test
    fun `on queryMoreReactions with custom limit, then use custom limit`() = runTest {
        setupInitialState()

        val customLimit = 5
        val moreReactions = listOf(feedsReactionData())
        val morePaginationResult =
            createPaginationResult(reactions = moreReactions, previous = "next-cursor")
        coEvery { activitiesRepository.queryActivityReactions(any(), any()) } returns
            Result.success(morePaginationResult)

        val result = activityReactionList.queryMoreReactions(customLimit)

        assertEquals(moreReactions, result.getOrNull())
        coVerify { activitiesRepository.queryActivityReactions(any(), any()) }
    }

    private suspend fun setupInitialState(nextCursor: String? = "next-cursor") {
        val initialReactions = listOf(feedsReactionData())
        val initialPaginationResult =
            PaginationResult(
                models = initialReactions,
                pagination = PaginationData(next = nextCursor, previous = null),
            )
        coEvery { activitiesRepository.queryActivityReactions(query.activityId, any()) } returns
            Result.success(initialPaginationResult)
        activityReactionList.get()
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
