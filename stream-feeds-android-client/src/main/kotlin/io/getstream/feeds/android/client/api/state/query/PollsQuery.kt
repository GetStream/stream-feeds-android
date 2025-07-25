package io.getstream.feeds.android.client.api.state.query

import io.getstream.android.core.query.Filter
import io.getstream.android.core.query.Sort
import io.getstream.android.core.query.SortDirection
import io.getstream.android.core.query.SortField
import io.getstream.feeds.android.client.api.model.PollData

/**
 * A query for retrieving polls with filtering, sorting, and pagination options.
 *
 * Use this struct to configure how polls should be fetched from the Stream Feeds API.
 * You can specify filters to narrow down results, sorting options, and pagination parameters.
 *
 * @property filter Optional filter to apply to the polls query. Use this to narrow down results
 * based on specific criteria.
 * @property limit Maximum number of polls to return in a single request. If not specified, the API
 * will use its default limit.
 * @param next Pagination cursor for fetching the next page of results. This is typically provided
 * in the response of a previous request.
 * @property previous Pagination cursor for fetching the previous page of results. This is typically
 * provided in the response of a previous request.
 * @property sort Array of sorting criteria to apply to the polls. If not specified, the API will
 * use its default sorting.
 */
public data class PollsQuery(
    public val filter: Filter? = null,
    public val limit: Int? = null,
    public val next: String? = null,
    public val previous: String? = null,
    public val sort: List<PollsSort>? = null
)


/**
 * Represents a sort specification for polls.
 *
 * @param field The field by which to sort the polls.
 * @param direction The direction of the sort operation (ascending or descending).
 */
public class PollsSort(field: PollsSortField, direction: SortDirection) :
    Sort<PollData>(field, direction) {

    public companion object {

        /**
         * Default sorting configuration for polls.
         *
         * This uses the `CreatedAt` field in reverse order, meaning the most recently created
         * polls will appear first.
         */
        public val Default: List<PollsSort> = listOf(
            PollsSort(PollsSortField.CreatedAt, SortDirection.REVERSE)
        )
    }
}
/**
 * Represents a field that can be used for sorting polls.
 *
 * This type provides a type-safe way to specify which field should be used
 * when sorting polls results.
 */
public sealed interface PollsSortField : SortField<PollData> {

    /**
     * Sort by the creation timestamp of the poll.
     * This field allows sorting polls by when they were created (newest/oldest first).
     */
    public data object CreatedAt : PollsSortField,
        SortField<PollData> by SortField.create("created_at", PollData::createdAt)

    /**
     * Sort by the last update timestamp of the poll.
     * This field allows sorting polls by when they were last updated (newest/oldest first).
     */
    public data object UpdatedAt : PollsSortField,
        SortField<PollData> by SortField.create("updated_at", PollData::updatedAt)

    /**
     * Sort by the number of votes the poll has received.
     * This field allows sorting polls by popularity (most/least voted).
     */
    public data object VoteCount : PollsSortField,
        SortField<PollData> by SortField.create("vote_count", PollData::voteCount)

    /**
     * Sort by the name of the poll.
     * This field allows sorting polls alphabetically by name.
     */
    public data object Name : PollsSortField,
        SortField<PollData> by SortField.create("name", PollData::name)

    /**
     * Sort by the unique identifier of the poll.
     * This field allows sorting polls by their unique ID.
     */
    public data object Id : PollsSortField,
        SortField<PollData> by SortField.create("id", PollData::id)

    /**
     * Sort by whether the poll is closed.
     * This field allows sorting polls by their closed status.
     */
    public data object IsClosed : PollsSortField,
        SortField<PollData> by SortField.create(
            "is_closed",
            localValue = { if (it.isClosed) 1 else 0 })

}