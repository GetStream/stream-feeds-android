package io.getstream.feeds.android.client.api.state.query

import io.getstream.android.core.query.Filter
import io.getstream.android.core.query.Sort
import io.getstream.android.core.query.SortDirection
import io.getstream.android.core.query.SortField
import io.getstream.android.core.query.toRequest
import io.getstream.feeds.android.client.api.model.BookmarkFolderData
import io.getstream.feeds.android.client.internal.model.mapping.toRequest
import io.getstream.feeds.android.core.generated.models.QueryBookmarkFoldersRequest

/**
 * A query for retrieving bookmark folders with filtering, sorting, and pagination options.
 *
 * Use this struct to configure how bookmark folders should be fetched from the Stream Feeds API.
 * You can specify filters to narrow down results, sorting options, and pagination parameters.
 *
 * @property filter Optional filter to apply to the bookmark folders query. Use this to narrow down results
 * based on specific criteria. Supported filters:
 * - field: `folder_id`, operators: `equal`, `in`
 * - field: `folder_name`, operators: `equal`, `in`, `contains`
 * - field: `user_id`, operators: `equal`, `in`
 * - field: `created_at`, operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
 * - field: `updated_at`, operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
 * @property sort Array of sorting criteria to apply to the bookmark folders. If not specified, the API
 * will use its default sorting.
 * @property limit Maximum number of bookmark folders to return in a single request. If not specified,
 * the API will use its default limit.
 * @property next Pagination cursor for fetching the next page of results. This is typically
 * provided in the response of a previous request.
 * @property previous Pagination cursor for fetching the previous page of results. This is typically
 * provided in the response of a previous request.
 */
public data class BookmarkFoldersQuery(
    public val filter: Filter? = null,
    public val sort: List<BookmarkFoldersSort>? = null,
    public val limit: Int? = null,
    public val next: String? = null,
    public val previous: String? = null,
)

/**
 * Represents a sorting operation for bookmark folders.
 *
 * @property field The field by which to sort the bookmark folders.
 * @property direction The direction of the sort operation.
 */
public class BookmarkFoldersSort(field: BookmarkFoldersSortField, direction: SortDirection) :
  Sort<BookmarkFolderData>(field, direction) {

    public companion object {

        /**
         * Default sorting configuration for bookmark folders.
         *
         * This uses the `CreatedAt` field in reverse order, meaning the most recently created
         * bookmark folders will appear first.
         */
        public val Default: List<BookmarkFoldersSort> = listOf(
            BookmarkFoldersSort(BookmarkFoldersSortField.CreatedAt, SortDirection.REVERSE)
        )
    }
  }

/**
 * Represents a field that can be used for sorting bookmark folders.
 *
 * This type provides a type-safe way to specify which field should be used when sorting bookmark folders
 * results.
 */
public sealed interface BookmarkFoldersSortField : SortField<BookmarkFolderData> {

    /**
     * Sort by the creation timestamp of the bookmark folder.
     * This field allows sorting bookmark folders by when they were created (newest/oldest first).
     */
    public data object CreatedAt : BookmarkFoldersSortField,
        SortField<BookmarkFolderData> by SortField.create("created_at", BookmarkFolderData::createdAt)

    /**
     * Sort by the last update timestamp of the bookmark folder.
     * This field allows sorting bookmark folders by when they were last updated (newest/oldest first).
     */
    public data object UpdatedAt : BookmarkFoldersSortField,
        SortField<BookmarkFolderData> by SortField.create("updated_at", BookmarkFolderData::updatedAt)
}

/**
 * Converts the [BookmarkFoldersQuery] to a [QueryBookmarkFoldersRequest].
 */
internal fun BookmarkFoldersQuery.toRequest(): QueryBookmarkFoldersRequest {
    return QueryBookmarkFoldersRequest(
        filter = filter?.toRequest(),
        sort = sort?.map { it.toRequest() },
        limit = limit,
        next = next,
        prev = previous
    )
}