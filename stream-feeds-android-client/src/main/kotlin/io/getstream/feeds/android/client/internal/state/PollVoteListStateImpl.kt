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
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.state.PollVoteListState
import io.getstream.feeds.android.client.api.state.query.PollVotesQuery
import io.getstream.feeds.android.client.api.state.query.PollVotesSort
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.state.query.PollVotesQueryConfig
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import io.getstream.feeds.android.client.internal.utils.upsertSorted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * An observable state object that manages the current state of a poll vote list.
 *
 * @property query The query used to fetch poll votes.
 */
internal class PollVoteListStateImpl(override val query: PollVotesQuery) :
    PollVoteListMutableState {

    private val _votes: MutableStateFlow<List<PollVoteData>> = MutableStateFlow(emptyList())

    internal var queryConfig: PollVotesQueryConfig? = null
        private set

    private var _pagination: PaginationData? = null

    private val votesSorting: List<PollVotesSort>
        get() = query.sort ?: PollVotesSort.Default

    override val votes: StateFlow<List<PollVoteData>>
        get() = _votes.asStateFlow()

    override val pagination: PaginationData?
        get() = _pagination

    override fun onQueryMorePollVotes(
        result: PaginationResult<PollVoteData>,
        queryConfig: PollVotesQueryConfig,
    ) {
        _pagination = result.pagination
        // Update the query configuration for future queries
        this.queryConfig = queryConfig
        // Merge the new votes with the existing ones (keeping the sort order)
        _votes.update { current ->
            current.mergeSorted(result.models, PollVoteData::id, votesSorting)
        }
    }

    override fun pollVoteAdded(vote: PollVoteData) {
        _votes.update { current -> current.upsertSorted(vote, PollVoteData::id, votesSorting) }
    }

    override fun pollVoteRemoved(voteId: String) {
        _votes.update { current -> current.filter { it.id != voteId } }
    }

    override fun pollVoteUpdated(vote: PollVoteData) {
        _votes.update { current ->
            current.map {
                if (it.id == vote.id) {
                    // Update the existing vote with the new data
                    vote
                } else {
                    it
                }
            }
        }
    }
}

/**
 * Mutable state interface for managing the poll vote list state.
 *
 * This interface extends the [PollVoteListState] and [PollVoteListStateUpdates] interfaces,
 * allowing for both state observation and updates.
 */
internal interface PollVoteListMutableState : PollVoteListState, PollVoteListStateUpdates

/** Interface for handling updates to the poll vote list state. */
internal interface PollVoteListStateUpdates {

    /**
     * Handles updates to the poll vote list state when new poll votes are fetched.
     *
     * @param result The result containing the new poll votes and pagination information.
     * @param queryConfig The configuration used for the query, including sorting options.
     */
    fun onQueryMorePollVotes(
        result: PaginationResult<PollVoteData>,
        queryConfig: PollVotesQueryConfig,
    )

    /** Handles the addition of a new poll vote to the list. */
    fun pollVoteAdded(vote: PollVoteData)

    /** Handles the removal of a poll vote from the list. */
    fun pollVoteRemoved(voteId: String)

    /**
     * Handles updates to an existing poll vote in the list.
     *
     * @param vote The updated poll vote data.
     */
    fun pollVoteUpdated(vote: PollVoteData)
}
