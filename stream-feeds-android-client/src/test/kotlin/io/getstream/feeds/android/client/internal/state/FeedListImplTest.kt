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
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.FeedsQuery
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.repository.FeedsCapabilityRepository
import io.getstream.feeds.android.client.internal.repository.FeedsRepository
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.getstream.feeds.android.client.internal.test.TestData.feedData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class FeedListImplTest {
    private val feedsRepository: FeedsRepository = mockk()
    private val capabilityRepository: FeedsCapabilityRepository = mockk(relaxed = true)
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener> =
        mockk(relaxed = true)
    private val query = FeedsQuery(limit = 10, watch = false)

    private val feedList =
        FeedListImpl(
            query = query,
            feedsRepository = feedsRepository,
            capabilityRepository = capabilityRepository,
            subscriptionManager = subscriptionManager,
        )

    @Test
    fun `on get, then return feeds and update state`() = runTest {
        val feeds = listOf(feedData(), feedData("feed-2", "user-2"))
        val paginationResult = createPaginationResult(feeds, next = "next-cursor")
        coEvery { feedsRepository.queryFeeds(query) } returns Result.success(paginationResult)

        val result = feedList.get()

        assertEquals(feeds, result.getOrNull())
        coVerify { feedsRepository.queryFeeds(query) }
    }

    @Test
    fun `on queryMoreFeeds with next cursor, then query with next cursor`() = runTest {
        setupInitialState()

        val moreFeeds = listOf(feedData("feed-3", "user-3"), feedData("feed-4", "user-4"))
        val morePaginationResult =
            createPaginationResult(
                feeds = moreFeeds,
                next = "next-cursor-2",
                previous = "next-cursor",
            )
        coEvery { feedsRepository.queryFeeds(any()) } returns Result.success(morePaginationResult)

        val result = feedList.queryMoreFeeds()

        assertEquals(moreFeeds, result.getOrNull())
        coVerify { feedsRepository.queryFeeds(any()) }
    }

    @Test
    fun `on queryMoreFeeds with no next cursor, then return empty list`() = runTest {
        setupInitialState(nextCursor = null)

        val result = feedList.queryMoreFeeds()

        assertEquals(emptyList<FeedData>(), result.getOrNull())
        coVerify(exactly = 1) { feedsRepository.queryFeeds(any()) }
    }

    @Test
    fun `on queryMoreFeeds with custom limit, then use custom limit`() = runTest {
        setupInitialState()

        val customLimit = 5
        val moreFeeds = listOf(feedData("feed-3", "user-3"))
        val morePaginationResult =
            createPaginationResult(feeds = moreFeeds, previous = "next-cursor")
        coEvery { feedsRepository.queryFeeds(any()) } returns Result.success(morePaginationResult)

        val result = feedList.queryMoreFeeds(customLimit)

        assertEquals(moreFeeds, result.getOrNull())
        coVerify { feedsRepository.queryFeeds(any()) }
    }

    private suspend fun setupInitialState(nextCursor: String? = "next-cursor") {
        val initialFeeds = listOf(feedData())
        val initialPaginationResult =
            PaginationResult(
                models = initialFeeds,
                pagination = PaginationData(next = nextCursor, previous = null),
            )
        coEvery { feedsRepository.queryFeeds(query) } returns
            Result.success(initialPaginationResult)
        feedList.get()
    }

    private fun createPaginationResult(
        feeds: List<FeedData>,
        next: String? = null,
        previous: String? = null,
    ) =
        PaginationResult(
            models = feeds,
            pagination = PaginationData(next = next, previous = previous),
        )
}
