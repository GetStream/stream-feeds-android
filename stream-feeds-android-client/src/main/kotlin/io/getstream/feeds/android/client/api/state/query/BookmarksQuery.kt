/*
 * Copyright (c) 2014-2025 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-feeds-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.getstream.feeds.android.client.api.state.query

import io.getstream.android.core.query.Filter
import io.getstream.android.core.query.Sort
import io.getstream.android.core.query.SortDirection
import io.getstream.android.core.query.SortField
import io.getstream.android.core.query.toRequest
import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.internal.model.mapping.toRequest
import io.getstream.feeds.android.network.models.QueryBookmarksRequest

/**
 * A query for retrieving bookmarks with filtering, sorting, and pagination options.
 *
 * Use this struct to configure how bookmarks should be fetched from the Stream Feeds API. You can
 * specify filters to narrow down results, sorting options, and pagination parameters.
 *
 * @property filter Optional filter to apply to the bookmarks query. Use this to narrow down results
 *   based on specific criteria. Supported filters:
 * - field: `activity_id`, operators: `equal`, `in`
 * - field: `folder_id`, operators: `equal`, `in`, `exists`
 * - field: `user_id`, operators: `equal`, `in`
 * - field: `created_at`, operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
 * - field: `updated_at`, operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
 *
 * @property sort Array of sorting criteria to apply to the bookmarks. If not specified, the API
 *   will use its default sorting.
 * @property limit Maximum number of bookmarks to return in a single request. If not specified, the
 *   API will use its default limit.
 * @property next Pagination cursor for fetching the next page of results. This is typically
 *   provided in the response of a previous request.
 * @property previous Pagination cursor for fetching the previous page of results. This is typically
 *   provided in the response of a previous request.
 */
public data class BookmarksQuery(
    public val filter: Filter? = null,
    public val sort: List<BookmarksSort>? = null,
    public val limit: Int? = null,
    public val next: String? = null,
    public val previous: String? = null,
)

/**
 * Represents a sorting operation for bookmarks.
 *
 * @property field The field by which to sort the bookmarks.
 * @property direction The direction of the sort operation.
 */
public class BookmarksSort(field: BookmarksSortField, direction: SortDirection) :
    Sort<BookmarkData>(field, direction) {

    public companion object {

        /**
         * Default sorting configuration for bookmarks.
         *
         * This uses the `CreatedAt` field in reverse order, meaning the most recently created
         * bookmarks will appear first.
         */
        public val Default: List<BookmarksSort> =
            listOf(BookmarksSort(BookmarksSortField.CreatedAt, SortDirection.REVERSE))
    }
}

/**
 * Represents a field that can be used for sorting bookmarks.
 *
 * This type provides a type-safe way to specify which field should be used when sorting bookmarks
 * results.
 */
public sealed interface BookmarksSortField : SortField<BookmarkData> {

    /**
     * Sort by the creation timestamp of the bookmark. This field allows sorting bookmarks by when
     * they were created (newest/oldest first).
     */
    public data object CreatedAt :
        BookmarksSortField,
        SortField<BookmarkData> by SortField.create("created_at", BookmarkData::createdAt)

    /**
     * Sort by the last update timestamp of the bookmark. This field allows sorting bookmarks by
     * when they were last updated (newest/oldest first).
     */
    public data object UpdatedAt :
        BookmarksSortField,
        SortField<BookmarkData> by SortField.create("updated_at", BookmarkData::updatedAt)
}

/** Converts the [BookmarksQuery] to a [QueryBookmarksRequest]. */
internal fun BookmarksQuery.toRequest(): QueryBookmarksRequest {
    return QueryBookmarksRequest(
        filter = filter?.toRequest(),
        sort = sort?.map { it.toRequest() },
        limit = limit,
        next = next,
        prev = previous,
    )
}
