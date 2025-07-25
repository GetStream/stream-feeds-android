package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.state.query.PollsQuery
import kotlinx.coroutines.flow.StateFlow

public interface PollListState {

    /**
     * The query used to fetch the polls.
     */
    public val query: PollsQuery

    /**
     * All the paginated polls.
     */
    public val polls: StateFlow<List<PollData>>

    /**
     * Last pagination information.
     */
    public val pagination: PaginationData?

    /**
     * Indicates whether there are more polls available to load.
     */
    public val canLoadMore: Boolean
        get() = pagination?.next != null
}
