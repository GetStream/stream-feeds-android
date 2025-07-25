package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.state.query.FeedsQuery

/**
 * Represents a list of feeds with a query and state.
 */
public interface FeedList {

    /**
     * The query used to fetch feeds.
     */
    public val query: FeedsQuery

    /**
     * An observable object representing the current state of the feed list.
     */
    public val state: FeedListState

    /**
     * Fetches the initial list of feeds based on the current query configuration.
     * This method loads the first page of feeds according to the query's filters, sorting, and
     * limit parameters.
     *
     * @return A [Result] containing a list of [FeedData] if successful, or an error if the
     * request fails.
     */
    public suspend fun get(): Result<List<FeedData>>

    /**
     * Loads the next page of feeds if more are available.
     *
     * This method fetches additional feeds using the pagination information from the previous
     * request. If no more feeds are available, an empty array is returned.
     *
     * @return A [Result] containing a list of [FeedData] if successful, or an error if the
     * request fails or there are no more feeds to load.
     */
    public suspend fun queryMoreFeeds(limit: Int? = null): Result<List<FeedData>>
}