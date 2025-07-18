package io.getstream.feeds.android.client.api.state

import io.getstream.android.core.query.Filter
import io.getstream.android.core.query.Sort
import io.getstream.android.core.query.SortDirection
import io.getstream.android.core.query.SortField
import io.getstream.feeds.android.client.api.model.ActivityData

/**
 * A query for retrieving activities with filtering, sorting, and pagination options.
 *
 * Use this model to configure how activities should be fetched from the Stream Feeds API.
 * You can specify filters to narrow down results, sorting options, and pagination parameters.
 *
 * Example usage:
 * ```kotlin
 * val query = ActivitiesQuery(
 *   filter = Filter.eq("id", "activity-id-1"),
 *   sort = listOf(ActivitiesSort(ActivitiesSortField.CreatedAt, SortDirection.REVERSE)),
 *   limit = 20,
 * )
 *
 * @property filter Optional filter to apply to the activities query. Use this to narrow down
 * results based on specific criteria.
 * @property sort Array of sorting criteria to apply to the activities. If not specified, the API
 * will use its default sorting.
 * @property next Pagination cursor for fetching the next page of results. This is typically
 * provided in the response of a previous request.
 * @property previous Pagination cursor for fetching the previous page of results. This is typically
 * provided in the response of a previous request.
 * @property limit Maximum number of activities to return in a single request. If not specified,
 * the API will use its default limit.
 */
public data class ActivitiesQuery(
    public val filter: Filter? = null,
    public val sort: List<ActivitiesSort>,
    public val next: String? = null,
    public val previous: String? = null,
    public val limit: Int? = null,
)

/**
 * Represents a sorting operation for activities.
 *
 * @property field The field by which to sort the activities.
 * @property direction The direction of the sort operation.
 */
public class ActivitiesSort(field: ActivitiesSortField, direction: SortDirection) :
    Sort<ActivityData>(field, direction) {

        public companion object {

            /**
             * Default sorting configuration for activities.
             *
             * This uses the `CreatedAt` field in reverse order, meaning the most recent activities
             * will appear first.
             */
            public val Default: List<ActivitiesSort> = listOf(
                ActivitiesSort(ActivitiesSortField.CreatedAt, SortDirection.REVERSE)
            )
        }
    }

/**
 * Defines the fields by which activities can be sorted.
 *
 * This interface extends [SortField] and provides specific fields for sorting feed data.
 * Each field corresponds to a property of the [ActivityData] model, allowing for flexible
 * sorting options when querying activities.
 */
public sealed interface ActivitiesSortField : SortField<ActivityData> {

    /**
     * Sort by the creation timestamp of the activity.
     * This field allows sorting activities by when they were created (newest/oldest first).
     */
    public data object CreatedAt : ActivitiesSortField,
        SortField<ActivityData> by SortField.create("created_at", ActivityData::createdAt)

    /**
     * Sort by the popularity score of the activity.
     * This field allows sorting activities by popularity (most/least popular first).
     */
    public data object Popularity : ActivitiesSortField,
        SortField<ActivityData> by SortField.create("popularity", ActivityData::popularity)
}