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
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.ActivitiesQuery
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.repository.ActivitiesRepository
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class ActivityListImplTest {
    private val activitiesRepository: ActivitiesRepository = mockk()
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener> =
        mockk(relaxed = true)
    private val query = ActivitiesQuery(limit = 10)
    private val currentUserId = "user-123"

    private val activityList =
        ActivityListImpl(
            query = query,
            currentUserId = currentUserId,
            activitiesRepository = activitiesRepository,
            subscriptionManager = subscriptionManager,
        )

    @Test
    fun `get when repository succeeds, then return activities and update state`() = runTest {
        val activities = listOf(activityData("activity-1"), activityData("activity-2"))
        val paginationResult = createPaginationResult(activities, next = "next-cursor")
        coEvery { activitiesRepository.queryActivities(query) } returns
            Result.success(paginationResult)

        val result = activityList.get()

        assertEquals(activities, result.getOrNull())
        coVerify { activitiesRepository.queryActivities(query) }
    }

    @Test
    fun `queryMoreActivities when next cursor exists, then queries with next cursor`() = runTest {
        setupInitialState()

        val moreActivities = listOf(activityData("activity-2"), activityData("activity-3"))
        val morePaginationResult =
            createPaginationResult(
                activities = moreActivities,
                next = "next-cursor-2",
                previous = "next-cursor",
            )
        val expectedNextQuery = createNextQuery("next-cursor")
        coEvery { activitiesRepository.queryActivities(expectedNextQuery) } returns
            Result.success(morePaginationResult)

        val result = activityList.queryMoreActivities()

        assertEquals(moreActivities, result.getOrNull())
        coVerify { activitiesRepository.queryActivities(expectedNextQuery) }
    }

    @Test
    fun `queryMoreActivities when no next cursor, then returns empty list`() = runTest {
        setupInitialState(nextCursor = null)

        val result = activityList.queryMoreActivities()

        assertEquals(emptyList<ActivityData>(), result.getOrNull())
        coVerify(exactly = 1) { activitiesRepository.queryActivities(any()) }
    }

    @Test
    fun `queryMoreActivities with custom limit, then uses custom limit`() = runTest {
        setupInitialState()

        val customLimit = 5
        val expectedNextQuery = createNextQuery("next-cursor", limit = customLimit)
        val moreActivities = listOf(activityData("activity-2"))
        val morePaginationResult =
            createPaginationResult(activities = moreActivities, previous = "next-cursor")
        coEvery { activitiesRepository.queryActivities(expectedNextQuery) } returns
            Result.success(morePaginationResult)

        val result = activityList.queryMoreActivities(customLimit)

        assertEquals(moreActivities, result.getOrNull())
        coVerify { activitiesRepository.queryActivities(expectedNextQuery) }
    }

    @Test
    fun `queryMoreActivities when repository fails, then returns failure`() = runTest {
        setupInitialState()

        val exception = RuntimeException("API Error")
        val expectedNextQuery = createNextQuery("next-cursor")
        coEvery { activitiesRepository.queryActivities(expectedNextQuery) } returns
            Result.failure(exception)

        val result = activityList.queryMoreActivities()

        assertEquals(exception, result.exceptionOrNull())
    }

    // Helper functions to reduce boilerplate
    private suspend fun setupInitialState(nextCursor: String? = "next-cursor") {
        val initialActivities = listOf(activityData("activity-1"))
        val initialPaginationResult =
            PaginationResult(
                models = initialActivities,
                pagination = PaginationData(next = nextCursor, previous = null),
            )
        coEvery { activitiesRepository.queryActivities(query) } returns
            Result.success(initialPaginationResult)
        activityList.get()
    }

    private fun createPaginationResult(
        activities: List<ActivityData>,
        next: String? = null,
        previous: String? = null,
    ) =
        PaginationResult(
            models = activities,
            pagination = PaginationData(next = next, previous = previous),
        )

    private fun createNextQuery(next: String, limit: Int? = query.limit) =
        ActivitiesQuery(
            filter = query.filter,
            sort = query.sort,
            limit = limit,
            next = next,
            previous = null,
        )
}
