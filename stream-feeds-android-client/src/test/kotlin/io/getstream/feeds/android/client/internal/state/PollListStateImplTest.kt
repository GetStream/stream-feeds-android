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

import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.state.query.PollsQuery
import io.getstream.feeds.android.client.api.state.query.PollsSort
import io.getstream.feeds.android.client.internal.state.query.PollsQueryConfig
import io.getstream.feeds.android.client.internal.test.TestData.defaultPaginationResult
import io.getstream.feeds.android.client.internal.test.TestData.pollData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class PollListStateImplTest {
    private val query = PollsQuery(limit = 10)
    private val pollListState = PollListStateImpl(query)

    @Test
    fun `on initial state, then return empty polls and null pagination`() = runTest {
        assertEquals(emptyList<PollData>(), pollListState.polls.value)
        assertNull(pollListState.pagination)
    }

    @Test
    fun `on queryMorePolls, then update polls and pagination`() = runTest {
        val polls = listOf(pollData("poll-1", "Test Poll"), pollData("poll-2", "Test Poll 2"))
        val paginationResult = defaultPaginationResult(polls)

        pollListState.onQueryMorePolls(paginationResult, queryConfig)

        assertEquals(polls, pollListState.polls.value)
        assertEquals("next-cursor", pollListState.pagination?.next)
        assertEquals(queryConfig, pollListState.queryConfig)
    }

    @Test
    fun `on pollUpdated, then update specific poll`() = runTest {
        val initialPolls =
            listOf(pollData("poll-1", "Test Poll"), pollData("poll-2", "Test Poll 2"))
        val paginationResult = defaultPaginationResult(initialPolls)
        pollListState.onQueryMorePolls(paginationResult, queryConfig)

        val updatedPoll = pollData("poll-1", "Updated Poll", description = "Updated description")
        pollListState.onPollUpdated(updatedPoll)

        val updatedPolls = pollListState.polls.value
        assertEquals(updatedPoll, updatedPolls.find { it.id == updatedPoll.id })
        assertEquals(initialPolls[1], updatedPolls.find { it.id == initialPolls[1].id })
    }

    @Test
    fun `on pollUpdated with non-existent poll, then keep existing polls unchanged`() = runTest {
        val initialPolls =
            listOf(pollData("poll-1", "Test Poll"), pollData("poll-2", "Test Poll 2"))
        val paginationResult = defaultPaginationResult(initialPolls)
        pollListState.onQueryMorePolls(paginationResult, queryConfig)

        val nonExistentPoll = pollData("non-existent", "Non-existent Poll")
        pollListState.onPollUpdated(nonExistentPoll)

        assertEquals(initialPolls, pollListState.polls.value)
    }

    companion object {
        private val queryConfig = PollsQueryConfig(filter = null, sort = PollsSort.Default)
    }
}
