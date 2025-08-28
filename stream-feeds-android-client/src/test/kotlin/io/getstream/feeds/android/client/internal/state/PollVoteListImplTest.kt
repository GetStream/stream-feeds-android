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
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.state.query.PollVotesQuery
import io.getstream.feeds.android.client.internal.repository.PollsRepository
import io.getstream.feeds.android.client.internal.subscribe.FeedsEventListener
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class PollVoteListImplTest {
    private val pollsRepository: PollsRepository = mockk()
    private val subscriptionManager: StreamSubscriptionManager<FeedsEventListener> =
        mockk(relaxed = true)
    private val query = PollVotesQuery(pollId = "poll-1", userId = "user-1", limit = 10)

    private val pollVoteList =
        PollVoteListImpl(
            query = query,
            repository = pollsRepository,
            subscriptionManager = subscriptionManager,
        )

    @Test
    fun `on get, then return poll votes and update state`() = runTest {
        val votes = listOf(pollVoteData(), pollVoteData("vote-2", "poll-1", "option-2", "user-2"))
        val paginationResult = createPaginationResult(votes, next = "next-cursor")
        coEvery { pollsRepository.queryPollVotes(query) } returns Result.success(paginationResult)

        val result = pollVoteList.get()

        assertEquals(votes, result.getOrNull())
        coVerify { pollsRepository.queryPollVotes(query) }
    }

    @Test
    fun `on queryMorePollVotes with next cursor, then query with next cursor`() = runTest {
        setupInitialState()

        val moreVotes =
            listOf(
                pollVoteData("vote-3", "poll-1", "option-1", "user-3"),
                pollVoteData("vote-4", "poll-1", "option-2", "user-4"),
            )
        val morePaginationResult =
            createPaginationResult(
                votes = moreVotes,
                next = "next-cursor-2",
                previous = "next-cursor",
            )
        coEvery { pollsRepository.queryPollVotes(any()) } returns
            Result.success(morePaginationResult)

        val result = pollVoteList.queryMorePollVotes()

        assertEquals(moreVotes, result.getOrNull())
        coVerify { pollsRepository.queryPollVotes(any()) }
    }

    @Test
    fun `on queryMorePollVotes with no next cursor, then return empty list`() = runTest {
        setupInitialState(nextCursor = null)

        val result = pollVoteList.queryMorePollVotes()

        assertEquals(emptyList<PollVoteData>(), result.getOrNull())
        coVerify(exactly = 1) { pollsRepository.queryPollVotes(any()) }
    }

    @Test
    fun `on queryMorePollVotes with custom limit, then use custom limit`() = runTest {
        setupInitialState()

        val customLimit = 5
        val moreVotes = listOf(pollVoteData("vote-3", "poll-1", "option-1", "user-3"))
        val morePaginationResult =
            createPaginationResult(votes = moreVotes, previous = "next-cursor")
        coEvery { pollsRepository.queryPollVotes(any()) } returns
            Result.success(morePaginationResult)

        val result = pollVoteList.queryMorePollVotes(customLimit)

        assertEquals(moreVotes, result.getOrNull())
        coVerify { pollsRepository.queryPollVotes(any()) }
    }

    private suspend fun setupInitialState(nextCursor: String? = "next-cursor") {
        val initialVotes = listOf(pollVoteData())
        val initialPaginationResult =
            PaginationResult(
                models = initialVotes,
                pagination = PaginationData(next = nextCursor, previous = null),
            )
        coEvery { pollsRepository.queryPollVotes(query) } returns
            Result.success(initialPaginationResult)

        pollVoteList.get()
    }

    private fun createPaginationResult(
        votes: List<PollVoteData>,
        next: String? = null,
        previous: String? = null,
    ) =
        PaginationResult(
            models = votes,
            pagination = PaginationData(next = next, previous = previous),
        )
}
