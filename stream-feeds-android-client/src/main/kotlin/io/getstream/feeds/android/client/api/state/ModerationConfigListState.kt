package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.ModerationConfigData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.ModerationConfigsQuery
import kotlinx.coroutines.flow.StateFlow

/**
 * An observable object representing the current state of a moderation configuration list.
 *
 * This class manages the state of moderation configurations, including the list of configurations,
 * pagination information, and real-time updates from WebSocket events.
 * It automatically handles configuration additions, updates, and deletions.
 */
public interface ModerationConfigListState {

    /**
     * The query configuration used to fetch configurations.
     */
    public val query: ModerationConfigsQuery

    /**
     * All the paginated moderation configurations.
     *
     * This property contains the current list of configurations based on the query
     * configuration. The list is automatically updated when new configurations are
     * added, existing configurations are updated or deleted.
     */
    public val configs: StateFlow<List<ModerationConfigData>>

    /**
     * Last pagination information from the most recent API response.
     *
     * This property contains the pagination metadata from the last successful
     * API request. It's used to determine if more configurations can be loaded and
     * to construct subsequent pagination requests.
     */
    public val pagination: PaginationData?

    /**
     * Indicates whether more configurations can be loaded.
     *
     * This property is true if there are more configurations available to load based on
     * the current pagination information. It helps in determining whether to fetch
     * additional configurations.
     */
    public val canLoadMore: Boolean
        get() = pagination?.next != null
}