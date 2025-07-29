package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.state.PollVoteListState
import io.getstream.feeds.android.client.api.state.query.PollVotesQuery
import io.getstream.feeds.android.client.api.state.query.PollVotesSort
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * An observable state object that manages the current state of a poll vote list.
 *
 * @property query The query used to fetch poll votes.
 */
internal class PollVoteListStateImpl(
    override val query: PollVotesQuery,
) : PollVoteListMutableState {

    private val _votes: MutableStateFlow<List<PollVoteData>> = MutableStateFlow(emptyList())

    internal var queryConfig: QueryConfiguration<PollVotesSort>? = null
        private set

    private var _pagination: PaginationData? = null

    private val votesSorting: List<PollVotesSort>
        get() = query.sort ?: PollVotesSort.Default

    override val votes: StateFlow<List<PollVoteData>>
        get() = _votes

    override val pagination: PaginationData?
        get() = _pagination

    override fun onQueryMorePollVotes(
        result: PaginationResult<PollVoteData>,
        queryConfig: QueryConfiguration<PollVotesSort>
    ) {
        _pagination = result.pagination
        // Update the query configuration for future queries
        this.queryConfig = queryConfig
        // Merge the new votes with the existing ones (keeping the sort order)
        _votes.value = _votes.value.mergeSorted(result.models, PollVoteData::id, votesSorting)
    }

    override fun pollVoteRemoved(voteId: String) {
        _votes.value = _votes.value.filter { it.id != voteId }
    }

    override fun pollVoteUpdated(vote: PollVoteData) {
        _votes.value = _votes.value.map {
            if (it.id == vote.id) {
                // Update the existing vote with the new data
                vote
            } else {
                it
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

/**
 * Interface for handling updates to the poll vote list state.
 */
internal interface PollVoteListStateUpdates {

    /**
     * Handles updates to the poll vote list state when new poll votes are fetched.
     *
     * @param result The result containing the new poll votes and pagination information.
     * @param queryConfig The configuration used for the query, including sorting options.
     */
    fun onQueryMorePollVotes(
        result: PaginationResult<PollVoteData>,
        queryConfig: QueryConfiguration<PollVotesSort>
    )

    /**
     * Handles the removal of a poll vote from the list.
     */
    fun pollVoteRemoved(voteId: String)

    /**
     * Handles updates to an existing poll vote in the list.
     *
     * @param vote The updated poll vote data.
     */
    fun pollVoteUpdated(vote: PollVoteData)
}