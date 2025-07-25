package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.state.query.PollsQuery

/**
 * A list of polls that can be queried and paginated.
 *
 * This class provides a way to fetch and manage a collection of polls with support for filtering,
 * sorting, and pagination. It maintains an observable state that can be used in UI components.
 */
public interface PollList {

    /**
     * The query used to fetch the polls.
     */
    public val query: PollsQuery

    /**
     * An observable object representing the current state of the poll list.
     */
    public val state: PollListState

    /**
     * Fetches the initial list of polls based on the current query.
     *
     * This method retrieves the first page of polls matching the query criteria.
     * The results are automatically stored in the state and can be accessed via the [state.polls]
     * property.
     *
     * @return A [Result] containing a list of [PollData] if successful, or an error if the request
     * fails.
     */
    public suspend fun get(): Result<List<PollData>>

    /**
     * Loads more polls using the next page token from the previous query.
     *
     * This method fetches additional polls if there are more available based on the current query.
     * The new polls are automatically merged with the existing ones in the state.
     *
     * @param limit Optional limit for the number of polls to fetch. If not provided,
     * the default limit will be used.
     * @return A [Result] containing a list of [PollData] if successful, or an error if the request
     * fails. Returns an empty list if there are no more polls to fetch.
     */
    public suspend fun queryMorePolls(limit: Int? = null): Result<List<PollData>>
}
