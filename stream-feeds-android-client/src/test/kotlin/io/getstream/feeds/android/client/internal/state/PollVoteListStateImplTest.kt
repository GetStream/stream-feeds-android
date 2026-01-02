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

import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.state.query.PollVotesQuery
import io.getstream.feeds.android.client.api.state.query.PollVotesSort
import io.getstream.feeds.android.client.internal.state.query.PollVotesQueryConfig
import io.getstream.feeds.android.client.internal.test.TestData.defaultPaginationResult
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
        val paginationResult = defaultPaginationResult(votes)

        pollVoteListState.onQueryMorePollVotes(paginationResult, queryConfig)

        assertEquals(votes, pollVoteListState.votes.value)
        assertEquals("next-cursor", pollVoteListState.pagination?.next)
        assertEquals(queryConfig, pollVoteListState.queryConfig)
    }

    @Test
    fun `on pollVoteRemoved, then remove specific vote`() = runTest {
        val initialVotes =
            listOf(pollVoteData(), pollVoteData("vote-2", "poll-1", "option-2", "user-2"))
        val paginationResult = defaultPaginationResult(initialVotes)
        pollVoteListState.onQueryMorePollVotes(paginationResult, queryConfig)

        pollVoteListState.pollVoteRemoved(initialVotes.first().id)

        val remainingVotes = pollVoteListState.votes.value
        assertEquals(initialVotes.drop(1), remainingVotes)
    }

    @Test
    fun `on pollVoteUpserted with new vote, then add vote to list in sorted order`() = runTest {
        val initial =
            listOf(pollVoteData("vote-1"), pollVoteData("vote-3", "poll-1", "option-3", "user-3"))
        val pagination = defaultPaginationResult(initial)
        pollVoteListState.onQueryMorePollVotes(pagination, queryConfig)

        val newVote = pollVoteData("vote-2", "poll-1", "option-2", "user-2")
        pollVoteListState.pollVoteUpserted(newVote)

        val expectedVotes = listOf(initial[0], newVote, initial[1])
        assertEquals(expectedVotes, pollVoteListState.votes.value)
    }

    @Test
    fun `on pollVoteUpserted with existing vote, then update existing vote`() = runTest {
        val initial =
            pollVoteData("vote-1", "poll-1", "option-1", "user-1", answerText = "Original")
        val pagination = defaultPaginationResult(listOf(initial))
        pollVoteListState.onQueryMorePollVotes(pagination, queryConfig)

        val updated = pollVoteData("vote-1", "poll-1", "option-1", "user-1", answerText = "Updated")
        pollVoteListState.pollVoteUpserted(updated)

        assertEquals(listOf(updated), pollVoteListState.votes.value)
    }

    @Test
    fun `onPollDeleted when called, then clear all votes`() = runTest {
        val votes = listOf(pollVoteData(), pollVoteData("vote-2", "poll-1", "option-2", "user-2"))
        val paginationResult = defaultPaginationResult(votes)

        pollVoteListState.onQueryMorePollVotes(paginationResult, queryConfig)
        pollVoteListState.onPollDeleted()

        assertEquals(emptyList<PollVoteData>(), pollVoteListState.votes.value)
    }

    companion object {
        private val queryConfig = PollVotesQueryConfig(filter = null, sort = PollVotesSort.Default)
    }
}
