package io.getstream.feeds.android.client.api.state.query

import io.getstream.android.core.query.Filter
import io.getstream.android.core.query.Sort
import io.getstream.android.core.query.SortDirection
import io.getstream.android.core.query.SortField
import io.getstream.android.core.query.toRequest
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.internal.model.mapping.toRequest
import io.getstream.feeds.android.core.generated.models.QueryFeedsRequest


/**
 * A query for retrieving feeds with filtering, sorting, and pagination options.
 *
 * Use this model to configure how feeds should be fetched from the Stream Feeds API.
 * You can specify filters to narrow down results, sorting options, and pagination parameters.
 *
 * Example usage:
 * ```kotlin
 * val query = FeedsQuery(
 *   filter = Filter.eq("visibility", "public"),
 *   sort = listOf(FeedsSort(FeedsSortField.CreatedAt, SortDirection.REVERSE)),
 *   limit = 20,
 * )
 * ```
 *
 * @property filter Optional filter to apply to the feeds query. Use this to narrow down results
 * based on specific criteria. Supported filters:
 * - field: `id`, operators: `equal`, `in`
 * - field: `group_id`, operators: `equal`, `in`
 * - field: `feed`, operators: `equal`, `in`
 * - field: `created_at`, operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
 * - field: `created_by_id`, operators: `equal`, `in`
 * - field: `created_by.name`, operators: `equal`, `q`, `autocomplete`
 * - field: `description`, operators: `equal`, `q`, `autocomplete`
 * - field: `follower_count`, operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
 * - field: `following_count`, operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
 * - field: `member_count`, operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
 * - field: `members`, operators: `in`
 * - field: `name`, operators: `equal`, `q`, `autocomplete`
 * - field: `updated_at`, operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
 * - field: `visibility`, operators: `equal`, `in`
 * - field: `following_users`, operators: `in`
 * - field: `following_feeds`, operators: `in`
 * - field: `filter_tags`, operators: `in`
 * @property sort Array of sorting criteria to apply to the feeds. If not specified, the API will
 * use its default sorting.
 * @property limit Maximum number of feeds to return in a single request. If not specified, the API
 * will use its default limit.
 * @property next Pagination cursor for fetching the next page of results. This is typically
 * provided in the response of a previous request.
 * @property previous Pagination cursor for fetching the previous page of results. This is typically
 * provided in the response of a previous request.
 * @property watch Whether to watch for real-time updates on the feeds. Defaults to true for
 * real-time functionality.
 */
public data class FeedsQuery(
    public val filter: Filter? = null,
    public val sort: List<FeedsSort>? = null,
    public val limit: Int? = null,
    public val next: String? = null,
    public val previous: String? = null,
    public val watch: Boolean = true,
)

/**
 * Represents a sorting operation for feeds.
 *
 * @property field The field by which to sort the feeds.
 * @property direction The direction of the sort operation.
 */
public class FeedsSort(field: FeedsSortField, direction: SortDirection) :
    Sort<FeedData>(field, direction) {

        public companion object {

            /**
             * The default sorting for feeds queries.
             * Sorts by creation date in ascending order (oldest first).
             */
            public val Default: List<FeedsSort> = listOf(
                FeedsSort(FeedsSortField.CreatedAt, SortDirection.FORWARD)
            )
        }
    }

/**
 * Defines the fields by which feeds can be sorted.
 *
 * This interface extends [SortField] and provides specific fields for sorting feed data.
 * Each field corresponds to a property of the [FeedData] model, allowing for flexible
 * sorting options when querying feeds.
 */
public sealed interface FeedsSortField : SortField<FeedData> {

    /**
     * Sort by the creation timestamp of the feed.
     * This field allows sorting feeds by when they were created (newest/oldest first).
     */
    public data object CreatedAt : FeedsSortField,
        SortField<FeedData> by SortField.create("created_at", FeedData::createdAt)

    /**
     * Sort by the last update timestamp of the feed.
     * This field allows sorting feeds by when they were last updated (newest/oldest first).
     */
    public data object UpdatedAt : FeedsSortField,
        SortField<FeedData> by SortField.create("updated_at", FeedData::updatedAt)

    /**
     * Sort by the number of members in the feed.
     * This field allows sorting feeds by member count (most/least members).
     */
    public data object MemberCount : FeedsSortField,
        SortField<FeedData> by SortField.create("member_count", FeedData::memberCount)

    /**
     * Sort by the number of followers the feed has.
     * This field allows sorting feeds by popularity (most/least followed).
     */
    public data object FollowerCount : FeedsSortField,
        SortField<FeedData> by SortField.create("follower_count", FeedData::followerCount)

    /**
     * Sort by the number of feeds this feed is following.
     * This field allows sorting feeds by how many feeds they follow.
     */
    public data object FollowingCount : FeedsSortField,
        SortField<FeedData> by SortField.create("following_count", FeedData::followingCount)
}

/**
 * Converts the [FeedsQuery] to a [QueryFeedsRequest].
 *
 * This function maps the properties of the [FeedsQuery] to the corresponding fields in the
 * [QueryFeedsRequest], allowing it to be used in API requests.
 *
 * @return A [QueryFeedsRequest] object with the properties from this query.
 */
internal fun FeedsQuery.toRequest(): QueryFeedsRequest = QueryFeedsRequest(
    limit = limit,
    next = next,
    prev = previous,
    watch = watch,
    sort = sort?.map { it.toRequest() },
    filter = filter?.toRequest(),
)

