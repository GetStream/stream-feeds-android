/*
 * Copyright (c) 2014-2026 Stream.io Inc. All rights reserved.
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

import io.getstream.android.core.api.filter.Filter
import io.getstream.android.core.api.filter.FilterField
import io.getstream.android.core.api.sort.Sort
import io.getstream.android.core.api.sort.SortDirection
import io.getstream.android.core.api.sort.SortField
import io.getstream.feeds.android.client.api.model.CollectionData
import java.util.Date

/**
 * A query for retrieving collections with filtering, sorting, and pagination options.
 *
 * Client-side requests return the current user's collections and the collections that have no
 * owner.
 *
 * @property filter Optional filter to apply to the collections query. See [CollectionsFilterField]
 *   for available filter fields and their supported operators.
 * @property sort Array of sorting criteria to apply to the collections. If not specified,
 *   collections are sorted by creation date, newest first.
 * @property limit Maximum number of collections to return in a single request, at most 100. If not
 *   specified, the API will use its default limit.
 * @property next Pagination cursor for fetching the next page of results. This is typically
 *   provided in the response of a previous request.
 * @property previous Pagination cursor for fetching the previous page of results. This is typically
 *   provided in the response of a previous request.
 */
public data class CollectionsQuery(
    public val filter: CollectionsFilter? = null,
    public val sort: List<CollectionsSort>? = null,
    public val limit: Int? = null,
    public val next: String? = null,
    public val previous: String? = null,
)

/**
 * A type alias representing a filter specifically for [CollectionData] using
 * [CollectionsFilterField].
 */
public typealias CollectionsFilter = Filter<CollectionData, CollectionsFilterField>

/** Represents a field that can be used to filter collections. */
public data class CollectionsFilterField(
    override val remote: String,
    override val localValue: (CollectionData) -> Any?,
) : FilterField<CollectionData> {
    public companion object {
        /**
         * Filter by the identifier of the collection.
         *
         * Supported operators: `equal`, `in`
         */
        public val id: CollectionsFilterField = CollectionsFilterField("id", CollectionData::id)

        /**
         * Filter by the name of the collection.
         *
         * Supported operators: `equal`, `in`
         */
        public val name: CollectionsFilterField =
            CollectionsFilterField("name", CollectionData::name)

        /**
         * Filter by creation timestamp.
         *
         * Supported operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
         */
        public val createdAt: CollectionsFilterField =
            CollectionsFilterField("created_at", CollectionData::createdAt)

        /**
         * Filter by last update timestamp.
         *
         * Supported operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
         */
        public val updatedAt: CollectionsFilterField =
            CollectionsFilterField("updated_at", CollectionData::updatedAt)
    }
}

/**
 * Represents a sorting operation for collections.
 *
 * @property field The field by which to sort the collections.
 * @property direction The direction of the sort operation.
 */
public class CollectionsSort(field: CollectionsSortField, direction: SortDirection) :
    Sort<CollectionData>(field, direction) {

    public companion object {
        /**
         * Default sorting configuration for collections.
         *
         * This uses the `CreatedAt` field in reverse order, meaning the most recently created
         * collections will appear first.
         */
        public val Default: List<CollectionsSort> =
            listOf(CollectionsSort(CollectionsSortField.CreatedAt, SortDirection.REVERSE))
    }
}

/**
 * Represents a field that can be used for sorting collections.
 *
 * This type provides a type-safe way to specify which field should be used when sorting collections
 * results.
 */
public sealed interface CollectionsSortField : SortField<CollectionData> {

    /** Sort by the creation timestamp of the collection. */
    public data object CreatedAt :
        CollectionsSortField,
        SortField<CollectionData> by SortField.create("created_at", { it.createdAt ?: Date(0) })

    /** Sort by the identifier of the collection. */
    public data object Id :
        CollectionsSortField,
        SortField<CollectionData> by SortField.create("id", CollectionData::id)

    /** Sort by the last update timestamp of the collection. */
    public data object UpdatedAt :
        CollectionsSortField,
        SortField<CollectionData> by SortField.create("updated_at", { it.updatedAt ?: Date(0) })
}
