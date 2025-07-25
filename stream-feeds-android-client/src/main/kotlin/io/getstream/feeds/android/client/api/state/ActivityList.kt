package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.state.query.ActivitiesQuery

/**
 * A paginated list of activities that supports real-time updates and filtering.
 *
 * `ActivityList` provides a convenient way to fetch, paginate, and observe activities
 * with automatic real-time updates via WebSocket events. It manages the state of activities
 * and provides methods for loading more activities as needed.
 */
public interface ActivityList {

    /**
     * The query configuration used for fetching activities.
     *
     * This property contains the filtering, sorting, and pagination parameters
     * that define how activities should be fetched and displayed.
     */
    public val query: ActivitiesQuery

    /**
     * An observable object representing the current state of the activity list.
     */
    public val state: ActivityListState

    /**
     * Fetches the initial set of activities based on the current query configuration.
     *
     * This method retrieves the first page of activities using the filtering and sorting
     * parameters defined in the query. The results are automatically stored in the state
     * and can be observed through the [state.activities] property.
     *
     * @return A [Result] containing a list of [ActivityData] if the fetch is successful,
     * or an error if the fetch fails.
     */
    public suspend fun get(): Result<List<ActivityData>>

    /**
     * Fetches the next page of activities if available.
     *
     * This method retrieves additional activities using the pagination cursor from the
     * previous request. The new activities are automatically merged with the existing
     * activities in the state, maintaining the proper sort order.
     *
     * @param limit Optional limit for the number of activities to fetch. If not specified,
     * the default limit from the query will be used.
     * @return A [Result] containing a list of [ActivityData] if the fetch is successful,
     * or an error if the fetch fails. Returns an empty list if there are no more activities to
     * fetch.
     */
    public suspend fun queryMoreActivities(limit: Int? = null): Result<List<ActivityData>>
}
