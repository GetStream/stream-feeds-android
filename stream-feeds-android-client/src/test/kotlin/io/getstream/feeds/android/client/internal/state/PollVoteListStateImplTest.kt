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

import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.state.query.PollVotesQuery
import io.getstream.feeds.android.client.api.state.query.PollVotesSort
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class PollVoteListStateImplTest {
    private val query = PollVotesQuery(pollId = "poll-1", userId = "user-1", limit = 10)
    private val pollVoteListState = PollVoteListStateImpl(query)

    @Test
    fun `on initial state, then return empty votes and null pagination`() = runTest {
        assertEquals(emptyList<PollVoteData>(), pollVoteListState.votes.value)
        assertNull(pollVoteListState.pagination)
    }

    @Test
    fun `on queryMorePollVotes, then update votes and pagination`() = runTest {
        val votes = listOf(pollVoteData(), pollVoteData("vote-2", "poll-1", "option-2", "user-2"))
        val paginationResult =
            PaginationResult(
                models = votes,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig = QueryConfiguration(filter = null, sort = PollVotesSort.Default)

        pollVoteListState.onQueryMorePollVotes(paginationResult, queryConfig)

        assertEquals(votes, pollVoteListState.votes.value)
        assertEquals("next-cursor", pollVoteListState.pagination?.next)
        assertEquals(queryConfig, pollVoteListState.queryConfig)
    }

    @Test
    fun `on pollVoteUpdated, then update specific vote`() = runTest {
        val initialVotes =
            listOf(pollVoteData(), pollVoteData("vote-2", "poll-1", "option-2", "user-2"))
        val paginationResult =
            PaginationResult(
                models = initialVotes,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig = QueryConfiguration(filter = null, sort = PollVotesSort.Default)
        pollVoteListState.onQueryMorePollVotes(paginationResult, queryConfig)

        val updatedVote =
            pollVoteData("vote-1", "poll-1", "option-1", "user-1", answerText = "Updated answer")
        pollVoteListState.pollVoteUpdated(updatedVote)

        val updatedVotes = pollVoteListState.votes.value
        assertEquals(listOf(updatedVote, initialVotes[1]), updatedVotes)
    }

    @Test
    fun `on pollVoteRemoved, then remove specific vote`() = runTest {
        val initialVotes =
            listOf(pollVoteData(), pollVoteData("vote-2", "poll-1", "option-2", "user-2"))
        val paginationResult =
            PaginationResult(
                models = initialVotes,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig = QueryConfiguration(filter = null, sort = PollVotesSort.Default)
        pollVoteListState.onQueryMorePollVotes(paginationResult, queryConfig)

        pollVoteListState.pollVoteRemoved(initialVotes.first().id)

        val remainingVotes = pollVoteListState.votes.value
        assertEquals(initialVotes.drop(1), remainingVotes)
    }

    @Test
    fun `on pollVoteUpdated with non-existent vote, then keep unchanged`() = runTest {
        val initialVotes =
            listOf(pollVoteData(), pollVoteData("vote-2", "poll-1", "option-2", "user-2"))
        val paginationResult =
            PaginationResult(
                models = initialVotes,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig = QueryConfiguration(filter = null, sort = PollVotesSort.Default)
        pollVoteListState.onQueryMorePollVotes(paginationResult, queryConfig)

        val nonExistentVote = pollVoteData("non-existent", "poll-1", "option-1", "user-3")
        pollVoteListState.pollVoteUpdated(nonExistentVote)

        assertEquals(initialVotes, pollVoteListState.votes.value)
    }
}
