package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.state.PollListState
import io.getstream.feeds.android.client.api.state.query.PollsQuery
import io.getstream.feeds.android.client.api.state.query.PollsSort
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * An observable state object that manages the current state of a poll list.
 *
 * This class provides a way to fetch and manage a collection of polls with support for filtering,
 * sorting, and pagination. It maintains an observable state that can be used in UI components.
 *
 * @property query The query used to fetch the polls.
 */
internal class PollListStateImpl(
    override val query: PollsQuery,
): PollListMutableState {

    private val _polls: MutableStateFlow<List<PollData>> = MutableStateFlow(emptyList())

    internal var queryConfig : QueryConfiguration<PollsSort>? = null
        private set

    private var _pagination: PaginationData? = null

    private val pollsSorting: List<PollsSort>
        get() = query.sort ?: PollsSort.Default

    override val polls: StateFlow<List<PollData>>
        get() = _polls

    override val pagination: PaginationData?
        get() = _pagination

    override fun onQueryMorePolls(
        result: PaginationResult<PollData>,
        queryConfig: QueryConfiguration<PollsSort>
    ) {
        _pagination = result.pagination
        // Update the query configuration for future queries
        this.queryConfig = queryConfig
        // Merge the new polls with the existing ones (keeping the sort order)
        _polls.value = _polls.value.mergeSorted(result.models, PollData::id, pollsSorting)
    }

    override fun onPollUpdated(poll: PollData) {
        _polls.value = _polls.value.map {
            if (it.id == poll.id) {
                // Update the existing poll with the new data
                poll
            } else {
                it
            }
        }
    }
}

/**
 * Mutable state object that manages the current state of a poll list.
 *
 * This interface extends [PollListState] and [PollListStateUpdates] interfaces,
 * allowing for both state observation and updates.
 */
internal interface PollListMutableState: PollListState, PollListStateUpdates

/**
 * Interface for handling updates to the poll list state.
 */
internal interface PollListStateUpdates {

    /**
     * Handles when a new list of polls is fetched.
     *
     * @param result The result containing the list of polls.
     * @param queryConfig The configuration used for the query, including sorting options.
     */
    fun onQueryMorePolls(
        result: PaginationResult<PollData>,
        queryConfig: QueryConfiguration<PollsSort>
    )

    /**
     * Called when a poll is updated.
     *
     * @param poll The updated poll data.
     */
    fun onPollUpdated(poll: PollData)
}