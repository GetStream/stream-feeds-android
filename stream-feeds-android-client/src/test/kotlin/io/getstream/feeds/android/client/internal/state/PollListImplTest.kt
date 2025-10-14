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
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.state.query.PollsQuery
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.repository.PollsRepository
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.getstream.feeds.android.client.internal.test.TestData.pollData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class PollListImplTest {
    private val pollsRepository: PollsRepository = mockk()
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener> =
        mockk(relaxed = true)
    private val query = PollsQuery(limit = 10)

    private val pollList =
        PollListImpl(
            query = query,
            pollsRepository = pollsRepository,
            subscriptionManager = subscriptionManager,
        )

    @Test
    fun `on get, then return polls and update state`() = runTest {
        val polls = listOf(pollData("poll-1", "Test Poll"), pollData("poll-2", "Test Poll 2"))
        val paginationResult = createPaginationResult(polls, next = "next-cursor")
        coEvery { pollsRepository.queryPolls(query) } returns Result.success(paginationResult)

        val result = pollList.get()

        assertEquals(polls, result.getOrNull())
        coVerify { pollsRepository.queryPolls(query) }
    }

    @Test
    fun `on queryMorePolls with next cursor, then query with next cursor`() = runTest {
        setupInitialState()

        val morePolls = listOf(pollData("poll-3", "Test Poll 3"), pollData("poll-4", "Test Poll 4"))
        val morePaginationResult =
            createPaginationResult(
                polls = morePolls,
                next = "next-cursor-2",
                previous = "next-cursor",
            )
        coEvery { pollsRepository.queryPolls(any()) } returns Result.success(morePaginationResult)

        val result = pollList.queryMorePolls()

        assertEquals(morePolls, result.getOrNull())
        coVerify { pollsRepository.queryPolls(any()) }
    }

    @Test
    fun `on queryMorePolls with no next cursor, then return empty list`() = runTest {
        setupInitialState(nextCursor = null)

        val result = pollList.queryMorePolls()

        assertEquals(emptyList<PollData>(), result.getOrNull())
        coVerify(exactly = 1) { pollsRepository.queryPolls(any()) }
    }

    @Test
    fun `on queryMorePolls with custom limit, then use custom limit`() = runTest {
        setupInitialState()

        val customLimit = 5
        val morePolls = listOf(pollData("poll-3", "Test Poll 3"))
        val morePaginationResult =
            createPaginationResult(polls = morePolls, previous = "next-cursor")
        coEvery { pollsRepository.queryPolls(any()) } returns Result.success(morePaginationResult)

        val result = pollList.queryMorePolls(customLimit)

        assertEquals(morePolls, result.getOrNull())
        coVerify { pollsRepository.queryPolls(any()) }
    }

    private suspend fun setupInitialState(nextCursor: String? = "next-cursor") {
        val initialPolls = listOf(pollData("poll-1", "Test Poll"))
        val initialPaginationResult =
            PaginationResult(
                models = initialPolls,
                pagination = PaginationData(next = nextCursor, previous = null),
            )
        coEvery { pollsRepository.queryPolls(query) } returns
            Result.success(initialPaginationResult)
        pollList.get()
    }

    private fun createPaginationResult(
        polls: List<PollData>,
        next: String? = null,
        previous: String? = null,
    ) =
        PaginationResult(
            models = polls,
            pagination = PaginationData(next = next, previous = previous),
        )
}
