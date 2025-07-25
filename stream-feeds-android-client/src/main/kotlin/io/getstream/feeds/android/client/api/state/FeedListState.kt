package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.FeedsQuery
import kotlinx.coroutines.flow.StateFlow

/**
 * An observable state object that manages the current state of a feed list.
 *
 * This class maintains the current list of feeds, pagination information, and provides real-time
 * updates when feeds are added, removed, or modified. It automatically handles WebSocket events to
 * keep the feed list synchronized.
 */
public interface FeedListState {

    /**
     * The original query configuration used to fetch feeds.
     *
     * This contains the feed ID, filters, sorting options, and pagination parameters that were used
     * to create the initial feed list.
     */
    public val query: FeedsQuery

    /**
     * All the paginated feeds currently loaded.
     */
    public val feeds: StateFlow<List<FeedData>>

    /**
     * Last pagination information.
     */
    public val pagination: PaginationData?

    /**
     * Indicates whether there are more feeds available to load.
     */
    public val canLoadMore: Boolean
        get() = pagination?.next != null

}