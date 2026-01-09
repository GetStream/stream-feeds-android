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

import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.state.query.PollsQuery
import io.getstream.feeds.android.client.api.state.query.PollsSort
import io.getstream.feeds.android.client.internal.state.query.PollsQueryConfig
import io.getstream.feeds.android.client.internal.test.TestData.defaultPaginationResult
import io.getstream.feeds.android.client.internal.test.TestData.pollData
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class PollListStateImplTest {
    private val currentUserId = "user-1"
    private val query = PollsQuery(limit = 10)
    private val pollListState = PollListStateImpl(currentUserId, query)

    @Test
    fun `on initial state, then return empty polls and null pagination`() = runTest {
        assertEquals(emptyList<PollData>(), pollListState.polls.value)
        assertNull(pollListState.pagination)
    }

    @Test
    fun `on queryMorePolls, then update polls and pagination`() = runTest {
        val polls = defaultPolls
        val paginationResult = defaultPaginationResult(polls)

        pollListState.onQueryMorePolls(paginationResult, queryConfig)

        assertEquals(polls, pollListState.polls.value)
        assertEquals("next-cursor", pollListState.pagination?.next)
        assertEquals(queryConfig, pollListState.queryConfig)
    }

    @Test
    fun `on onPollUpdated, then update specific poll`() = runTest {
        val initialPolls = setupInitialState()

        val updatedPoll = pollData("poll-1", "Updated Poll", description = "Updated description")
        pollListState.onPollUpdated(updatedPoll)

        val expectedPolls = listOf(updatedPoll, initialPolls[1])
        assertEquals(expectedPolls, pollListState.polls.value)
    }

    @Test
    fun `on onPollUpdated with non-matching poll, then keep polls unchanged`() = runTest {
        val initialPolls = setupInitialState()

        val nonMatchingPoll = pollData("non-matching", "Non-matching Poll")
        pollListState.onPollUpdated(nonMatchingPoll)

        assertEquals(initialPolls, pollListState.polls.value)
    }

    @Test
    fun `on onPollDeleted, remove poll from list`() = runTest {
        val initialPolls = setupInitialState()

        pollListState.onPollDeleted("poll-1")

        val expectedPolls = listOf(initialPolls[1])
        assertEquals(expectedPolls, pollListState.polls.value)
    }

    @Test
    fun `on onPollDeleted with non-matching poll, keep polls unchanged`() = runTest {
        val initialPolls = setupInitialState()

        pollListState.onPollDeleted("non-matching")

        assertEquals(initialPolls, pollListState.polls.value)
    }

    @Test
    fun `onPollVoteUpserted with current user vote, update poll and add to ownVotes`() = runTest {
        val initialPoll = pollData("poll-1", "Test Poll")
        setupInitialState(listOf(initialPoll))

        val vote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
        val updatedPoll = pollData("poll-1", "Updated Poll")
        pollListState.onPollVoteUpserted(updatedPoll, vote)

        assertEquals(listOf(updatedPoll.copy(ownVotes = listOf(vote))), pollListState.polls.value)
    }

    @Test
    fun `onPollVoteUpserted with different user vote, update poll and keep ownVotes`() = runTest {
        val existingVote = pollVoteData("existing-vote", "poll-1", "option-1", currentUserId)
        val initialPoll = pollData("poll-1", "Test Poll", ownVotes = listOf(existingVote))
        setupInitialState(listOf(initialPoll))

        val vote = pollVoteData("vote-1", "poll-1", "option-1", "other-user")
        val updatedPoll = pollData("poll-1", "Updated Poll")
        pollListState.onPollVoteUpserted(updatedPoll, vote)

        val expectedPoll = updatedPoll.copy(ownVotes = initialPoll.ownVotes)
        assertEquals(listOf(expectedPoll), pollListState.polls.value)
    }

    @Test
    fun `onPollVoteUpserted with non-matching poll, keep existing polls unchanged`() = runTest {
        val initialPolls = setupInitialState()

        val vote = pollVoteData("vote-1", "non-matching", "option-1", currentUserId)
        val nonMatchingPoll = pollData("non-matching", "Non-matching Poll")
        pollListState.onPollVoteUpserted(nonMatchingPoll, vote)

        assertEquals(initialPolls, pollListState.polls.value)
    }

    @Test
    fun `onPollVoteRemoved with current user vote, update poll and remove from ownVotes`() =
        runTest {
            val existingVote = pollVoteData("vote-1", "poll-1", "option-1", currentUserId)
            val initialPoll = pollData("poll-1", "Test Poll", ownVotes = listOf(existingVote))
            setupInitialState(listOf(initialPoll))

            val updatedPoll = pollData("poll-1", "Updated Poll")
            pollListState.onPollVoteRemoved(updatedPoll, existingVote)

            val expectedPoll = updatedPoll.copy(ownVotes = emptyList())
            assertEquals(listOf(expectedPoll), pollListState.polls.value)
        }

    @Test
    fun `onPollVoteRemoved with different user vote, update poll and keep ownVotes`() = runTest {
        val existingVote = pollVoteData("existing-vote", "poll-1", "option-1", currentUserId)
        val initialPoll = pollData("poll-1", "Test Poll", ownVotes = listOf(existingVote))
        setupInitialState(listOf(initialPoll))

        val vote = pollVoteData("vote-1", "poll-1", "option-1", "other-user")
        val updatedPoll = pollData("poll-1", "Updated Poll")
        pollListState.onPollVoteRemoved(updatedPoll, vote)

        val expectedPoll = updatedPoll.copy(ownVotes = listOf(existingVote))
        assertEquals(listOf(expectedPoll), pollListState.polls.value)
    }

    @Test
    fun `onPollVoteRemoved with non-matching poll, keep polls unchanged`() = runTest {
        val initialPolls = setupInitialState()

        val vote = pollVoteData("vote-1", "non-matching", "option-1", currentUserId)
        val nonMatchingPoll = pollData("non-matching", "Non-matching Poll")
        pollListState.onPollVoteRemoved(nonMatchingPoll, vote)

        assertEquals(initialPolls, pollListState.polls.value)
    }

    private fun setupInitialState(polls: List<PollData> = defaultPolls): List<PollData> {
        val paginationResult = defaultPaginationResult(polls)
        pollListState.onQueryMorePolls(paginationResult, queryConfig)
        return polls
    }

    companion object {
        private val defaultPolls =
            listOf(pollData("poll-1", "Test Poll"), pollData("poll-2", "Test Poll 2"))
        private val queryConfig = PollsQueryConfig(filter = null, sort = PollsSort.Default)
    }
}
