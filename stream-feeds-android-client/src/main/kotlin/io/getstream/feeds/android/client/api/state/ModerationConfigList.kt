package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.ModerationConfigData
import io.getstream.feeds.android.client.api.state.query.ModerationConfigsQuery

/**
 * A class representing a paginated list of moderation configurations.
 *
 * ## Example Usage:
 * ```kotlin
 * // Create a moderation config list with a specific query
 * val query = ModerationConfigsQuery()
 * val moderationConfigList = feedsClient.moderationConfigList(query)
 *
 * // Fetch initial moderation configurations matching the query
 * val configs = moderationConfigList.get()
 *
 * // Load more configurations if available
 * if (moderationConfigList.state.canLoadMore) {
 *    val moreConfigs = moderationConfigList.queryMoreConfigs()
 * }
 *
 * // Observe state changes
 * moderationConfigList.state.configs.collect { configs ->
 *   println("Updated moderation configurations: ${configs.size}")
 * }
 * ```
 */
public interface ModerationConfigList {

    /**
     * The query configuration used to fetch configurations.
     */
    public val query: ModerationConfigsQuery

    /**
     * An observable object representing the current state of the configuration list.
     *
     * This property provides access to the current configurations, pagination information,
     * and real-time updates. The state automatically updates when WebSocket events
     * are received for configuration additions, updates, and deletions.
     */
    public val state: ModerationConfigListState

    /**
     * Fetches the initial set of moderation configurations.
     *
     * This method retrieves the first page of configurations based on the query configuration.
     * The results are automatically stored in the state and can be accessed through
     * the [state.configs] property.
     */
    public suspend fun get(): Result<List<ModerationConfigData>>

    /**
     * Loads the next page of configurations if more are available.
     *
     * This method fetches additional configurations using the pagination information
     * from the previous request. If no more configurations are available, an empty
     * array is returned.
     */
    public suspend fun queryMoreConfigs(limit: Int? = null): Result<List<ModerationConfigData>>
}
