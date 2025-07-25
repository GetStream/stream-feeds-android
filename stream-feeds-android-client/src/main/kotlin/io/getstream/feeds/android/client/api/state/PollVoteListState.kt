package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.state.query.PollVotesQuery
import kotlinx.coroutines.flow.StateFlow

public interface PollVoteListState {

    /**
     * The query used to fetch the poll votes.
     */
    public val query: PollVotesQuery

    /**
     * All the paginated poll votes.
     */
    public val votes: StateFlow<List<PollVoteData>>

    /**
     * Last pagination information.
     */
    public val pagination: PaginationData?

    /**
     * Indicates whether there are more poll votes available to load.
     */
    public val canLoadMore: Boolean
        get() = pagination?.next != null
}
