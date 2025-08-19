package io.getstream.feeds.android.client.api.state.query

import io.getstream.android.core.query.Filter
import io.getstream.android.core.query.Sort
import io.getstream.android.core.query.SortDirection
import io.getstream.android.core.query.SortField
import io.getstream.android.core.query.toRequest
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedMemberData
import io.getstream.feeds.android.client.internal.model.mapping.toRequest
import io.getstream.feeds.android.core.generated.models.QueryFeedMembersRequest

/**
 * A query for retrieving feed members with filtering, sorting, and pagination options.
 *
 * Use this class to configure how feed members should be fetched from the Stream Feeds API.
 * You can specify filters to narrow down results, sorting options, and pagination parameters.
 *
 * @property fid The feed ID to fetch members for.
 * @property filter Optional filter to apply to the members query. Use this to narrow down results
 * based on specific criteria. Supported filters:
 * - field: `created_at`, operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
 * - field: `role`, operators: `equal`, `in`
 * - field: `status`, operators: `equal`, `in`
 * - field: `updated_at`, operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
 * - field: `user_id`, operators: `equal`, `in`
 * - field: `fid`, operators: `equal`, `in`
 * - field: `request`, operators: `equal`
 * @property sort Array of sorting criteria to apply to the members. If not specified, the API will
 * use its default sorting.
 * @property limit Maximum number of members to return in a single request. If not specified, the
 * API will use its default limit.
 * @property next Pagination cursor for fetching the next page of results. This is typically
 * provided in the response of a previous request.
 * @property previous Pagination cursor for fetching the previous page of results. This is typically
 * provided in the response of a previous request.
 */
public data class MembersQuery(
    public val fid: FeedId,
    public val filter: Filter? = null,
    public val sort: List<MembersSort>? = null,
    public val limit: Int? = null,
    public val next: String? = null,
    public val previous: String? = null,
)


/**
 * Represents a sorting operation for feed members.
 *
 * @property field The field by which to sort the members.
 * @property direction The direction of the sort operation.
 */
public class MembersSort(field: MembersSortField, direction: SortDirection) :
    Sort<FeedMemberData>(field, direction) {

    public companion object {

        /**
         * Default sorting configuration for feed members.
         *
         * This uses the `CreatedAt` field in reverse order, meaning the most recently added
         * members will appear first.
         */
        public val Default: List<MembersSort> = listOf(
            MembersSort(MembersSortField.CreatedAt, SortDirection.REVERSE)
        )
    }
}

/**
 * Represents a field that can be used for sorting members.
 *
 * This type provides a type-safe way to specify which field should be used when sorting members
 * results.
 */
public sealed interface MembersSortField : SortField<FeedMemberData> {

    /**
     * Sort by the creation timestamp of the member.
     * This field allows sorting members by when they were added to the feed (newest/oldest first).
     */
    public data object CreatedAt : MembersSortField,
        SortField<FeedMemberData> by SortField.create("created_at", FeedMemberData::createdAt)

    /**
     * Sort by the last update timestamp of the member.
     * This field allows sorting members by when they were last updated (newest/oldest first).
     */
    public data object UpdatedAt : MembersSortField,
        SortField<FeedMemberData> by SortField.create("updated_at", FeedMemberData::updatedAt)

    /**
     * Sort by the user ID of the member.
     * This field allows sorting members alphabetically by user ID.
     */
    public data object UserId : MembersSortField,
        SortField<FeedMemberData> by SortField.create("user_id", { it.user.id })
}

/**
 * Converts a [MembersQuery] to a [QueryFeedMembersRequest].
 */
internal fun MembersQuery.toRequest(): QueryFeedMembersRequest = QueryFeedMembersRequest(
    limit = limit,
    next = next,
    prev = previous,
    sort = sort?.map { it.toRequest() },
    filter = filter?.toRequest(),
)
