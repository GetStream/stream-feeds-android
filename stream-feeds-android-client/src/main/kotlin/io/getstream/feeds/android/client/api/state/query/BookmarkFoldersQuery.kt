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

import io.getstream.feeds.android.client.api.model.BookmarkFolderData
import io.getstream.feeds.android.client.api.query.DefaultFilterField
import io.getstream.feeds.android.client.api.query.Filter
import io.getstream.feeds.android.client.api.query.FilterField
import io.getstream.feeds.android.client.api.query.Sort
import io.getstream.feeds.android.client.api.query.SortDirection
import io.getstream.feeds.android.client.api.query.SortField
import io.getstream.feeds.android.client.api.query.toRequest
import io.getstream.feeds.android.client.internal.model.mapping.toRequest
import io.getstream.feeds.android.network.models.QueryBookmarkFoldersRequest

/**
 * A query for retrieving bookmark folders with filtering, sorting, and pagination options.
 *
 * Use this struct to configure how bookmark folders should be fetched from the Stream Feeds API.
 * You can specify filters to narrow down results, sorting options, and pagination parameters.
 *
 * @property filter Optional filter to apply to the bookmark folders query. Use this to narrow down
 *   results based on specific criteria. See [BookmarkFoldersFilterField] for available filter
 *   fields and their supported operators.
 * @property sort Array of sorting criteria to apply to the bookmark folders. If not specified, the
 *   API will use its default sorting.
 * @property limit Maximum number of bookmark folders to return in a single request. If not
 *   specified, the API will use its default limit.
 * @property next Pagination cursor for fetching the next page of results. This is typically
 *   provided in the response of a previous request.
 * @property previous Pagination cursor for fetching the previous page of results. This is typically
 *   provided in the response of a previous request.
 */
public data class BookmarkFoldersQuery(
    public val filter: BookmarkFoldersFilter? = null,
    public val sort: List<BookmarkFoldersSort>? = null,
    public val limit: Int? = null,
    public val next: String? = null,
    public val previous: String? = null,
)

public typealias BookmarkFoldersFilter = Filter<BookmarkFoldersFilterField>

public interface BookmarkFoldersFilterField : FilterField {
    /**
     * Filter by folder ID.
     *
     * Supported operators: `equal`, `in`
     */
    public data object FolderId : BookmarkFoldersFilterField, DefaultFilterField("folder_id")

    /**
     * Filter by folder name.
     *
     * Supported operators: `equal`, `in`, `contains`
     */
    public data object FolderName : BookmarkFoldersFilterField, DefaultFilterField("folder_name")

    /**
     * Filter by user ID.
     *
     * Supported operators: `equal`, `in`
     */
    public data object UserId : BookmarkFoldersFilterField, DefaultFilterField("user_id")

    /**
     * Filter by creation timestamp.
     *
     * Supported operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
     */
    public data object CreatedAt : BookmarkFoldersFilterField, DefaultFilterField("created_at")

    /**
     * Filter by last update timestamp.
     *
     * Supported operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
     */
    public data object UpdatedAt : BookmarkFoldersFilterField, DefaultFilterField("updated_at")

    /** Filter by any arbitrary field name not covered by the predefined filter fields. */
    public data class Raw(override val raw: String) : BookmarkFoldersFilterField
}

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
        public val Default: List<BookmarkFoldersSort> =
            listOf(BookmarkFoldersSort(BookmarkFoldersSortField.CreatedAt, SortDirection.REVERSE))
    }
}

/**
 * Represents a field that can be used for sorting bookmark folders.
 *
 * This type provides a type-safe way to specify which field should be used when sorting bookmark
 * folders results.
 */
public sealed interface BookmarkFoldersSortField : SortField<BookmarkFolderData> {

    /**
     * Sort by the creation timestamp of the bookmark folder. This field allows sorting bookmark
     * folders by when they were created (newest/oldest first).
     */
    public data object CreatedAt :
        BookmarkFoldersSortField,
        SortField<BookmarkFolderData> by SortField.create(
            "created_at",
            BookmarkFolderData::createdAt,
        )

    /**
     * Sort by the last update timestamp of the bookmark folder. This field allows sorting bookmark
     * folders by when they were last updated (newest/oldest first).
     */
    public data object UpdatedAt :
        BookmarkFoldersSortField,
        SortField<BookmarkFolderData> by SortField.create(
            "updated_at",
            BookmarkFolderData::updatedAt,
        )
}

/** Converts the [BookmarkFoldersQuery] to a [QueryBookmarkFoldersRequest]. */
internal fun BookmarkFoldersQuery.toRequest(): QueryBookmarkFoldersRequest {
    return QueryBookmarkFoldersRequest(
        filter = filter?.toRequest(),
        sort = sort?.map { it.toRequest() },
        limit = limit,
        next = next,
        prev = previous,
    )
}
