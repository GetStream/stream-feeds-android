package io.getstream.feeds.android.client.api.state

import io.getstream.android.core.query.Filter
import io.getstream.android.core.query.Sort
import io.getstream.android.core.query.SortDirection
import io.getstream.android.core.query.SortField
import io.getstream.android.core.query.toMap
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.internal.model.mapping.toRequest
import io.getstream.feeds.android.core.generated.models.QueryCommentReactionsRequest

/**
 * A query configuration for fetching reactions for a specific comment.
 *
 * This struct defines the parameters used to fetch reactions for a comment,
 * including pagination settings, sorting options, and filtering capabilities.
 *
 * ## Features
 *
 * - **Pagination**: Supports `next` and `previous` cursors for efficient pagination
 * - **Sorting**: Configurable sorting options for reaction ordering
 * - **Filtering**: Supports filtering by reaction type, user ID, and creation date
 *
 * @param commentId The unique identifier of the comment to fetch reactions for.
 * @param filter Optional filter criteria to apply to the reactions. Use this to narrow down results
 * based on specific criteria.
 * @param limit The maximum number of reactions to return in a single request. If not specified,
 * the API will use its default limit.
 * @param next Pagination cursor for fetching the next page of results. This is typically provided
 * in the response of a previous request.
 * @param previous Pagination cursor for fetching the previous page of results. This is typically
 * provided in the response of a previous request.
 * @param sort Array of sorting criteria to apply to the comment reactions. If not specified, the
 * API will use its default sorting.
 */
public data class CommentReactionsQuery(
    public val commentId: String,
    public val filter: Filter? = null,
    public val limit: Int? = null,
    public val next: String? = null,
    public val previous: String? = null,
    public val sort: List<CommentReactionsSort>? = null,
)

/**
 * A sort configuration for comment reactions.
 *
 * @param field The field to sort by, such as creation date.
 * @param direction The direction of the sort, either ascending or descending.
 */
public class CommentReactionsSort(field: CommentReactionSortField, direction: SortDirection) :
    Sort<FeedsReactionData>(field, direction) {

    public companion object {

        /**
         * The default sorting for comment reactions queries.
         * Sorts by creation date in descending order (newest first).
         */
        public val Default: List<CommentReactionsSort> = listOf(
            CommentReactionsSort(CommentReactionSortField.CreatedAt, SortDirection.REVERSE),
        )
    }
}

/**
 * Represents the sorting options available for comment reactions.
 */
public sealed interface CommentReactionSortField : SortField<FeedsReactionData> {

    /**
     * Sort by the creation timestamp of the reaction.
     * This field allows sorting reactions by when they were created (newest/oldest first).
     */
    public object CreatedAt : CommentReactionSortField,
        SortField<FeedsReactionData> by SortField.create("created_at", FeedsReactionData::createdAt)
}

/**
 * Converts a [CommentReactionsQuery] to the corresponding [QueryCommentReactionsRequest].
 */
internal fun CommentReactionsQuery.toRequest(): QueryCommentReactionsRequest =
    QueryCommentReactionsRequest(
        filter = filter?.toMap(),
        limit = limit,
        next = next,
        prev = previous,
        sort = sort?.map { it.toRequest() },
    )
