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
import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.internal.model.mapping.toRequest
import io.getstream.feeds.android.core.generated.models.QueryFollowsRequest

/**
 * A query for retrieving follows with filtering, sorting, and pagination options.
 *
 * Use this struct to configure how follows should be fetched from the Stream Feeds API. You can
 * specify filters to narrow down results, sorting options, and pagination parameters.
 *
 * @property filter Optional filter to apply to the follows query. Use this to narrow down results
 *   based on specific criteria.
 * @property sort Array of sorting criteria to apply to the follows. If not specified, the API will
 *   use its default sorting.
 * @property limit Maximum number of follows to return in a single request. If not specified, the
 *   API will use its default limit.
 * @property next Pagination cursor for fetching the next page of results. This is typically
 *   provided in the response of a previous request.
 * @property previous Pagination cursor for fetching the previous page of results. This is typically
 *   provided in the response of a previous request.
 */
public data class FollowsQuery(
    public val filter: Filter? = null,
    public val sort: List<FollowsSort>? = null,
    public val limit: Int? = null,
    public val next: String? = null,
    public val previous: String? = null,
)

/**
 * Represents a sorting operation for follows.
 *
 * This class allows you to specify how follows should be sorted when querying them.
 *
 * @property field The field by which to sort the follows.
 * @property direction The direction of the sort operation (ascending or descending).
 */
public class FollowsSort(field: FollowsSortField, direction: SortDirection) :
    Sort<FollowData>(field, direction) {

    public companion object Companion {

        /**
         * The default sorting for follows queries. Sorts by creation date in descending order
         * (newest first).
         */
        public val Default: List<FollowsSort> =
            listOf(FollowsSort(FollowsSortField.CreatedAt, SortDirection.REVERSE))
    }
}

/**
 * Represents a field that can be used for sorting follows.
 *
 * This type provides a type-safe way to specify which field should be used when sorting follows
 * results.
 */
public sealed interface FollowsSortField : SortField<FollowData> {

    /**
     * Sort by the creation timestamp of the follow relationship.
     *
     * This field allows sorting follows by when they were created (newest/oldest first).
     */
    public data object CreatedAt :
        FollowsSortField,
        SortField<FollowData> by SortField.create("created_at", FollowData::createdAt)
}

/** Converts the [FollowsQuery] to a [QueryFollowsRequest]. */
internal fun FollowsQuery.toRequest(): QueryFollowsRequest =
    QueryFollowsRequest(
        filter = filter?.toRequest(),
        sort = sort?.map { it.toRequest() },
        limit = limit,
        next = next,
        prev = previous,
    )
