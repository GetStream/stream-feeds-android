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
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.internal.model.mapping.toRequest
import io.getstream.feeds.android.core.generated.models.QueryActivitiesRequest

/**
 * A query for retrieving activities with filtering, sorting, and pagination options.
 *
 * Use this model to configure how activities should be fetched from the Stream Feeds API. You can
 * specify filters to narrow down results, sorting options, and pagination parameters.
 *
 * Example usage:
 * ```kotlin
 * val query = ActivitiesQuery(
 *   filter = Filter.eq("id", "activity-id-1"),
 *   sort = listOf(ActivitiesSort(ActivitiesSortField.CreatedAt, SortDirection.REVERSE)),
 *   limit = 20,
 * )
 * ```
 *
 * @property filter Optional filter to apply to the activities query. Use this to narrow down
 * results based on specific criteria. Supported filters:
 * - field: `created_at`, operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
 * - field: `id`, operators: `equal`, `in`
 * - field: `filter_tags`, operators: `equal`, `in`, `contains`
 * - field: `popularity`, operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
 * - field: `search_data`, operators: `equal`, `q`, `autocomplete`
 * - field: `text`, operators: `equal`, `q`, `autocomplete`
 * - field: `type`, operators: `equal`, `in`
 * - field: `user_id`, operators: `equal`, `in`
 * @property sort Array of sorting criteria to apply to the activities. If not specified, the API
 * will use its default sorting.
 * @property limit Maximum number of activities to return in a single request. If not specified,
 * the API will use its default limit.
 * @property next Pagination cursor for fetching the next page of results. This is typically
 * provided in the response of a previous request.
 * @property previous Pagination cursor for fetching the previous page of results. This is typically
 * provided in the response of a previous request.
 */
public data class ActivitiesQuery(
    public val filter: Filter? = null,
    public val sort: List<ActivitiesSort>? = null,
    public val limit: Int? = null,
    public val next: String? = null,
    public val previous: String? = null,
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
         * This uses the `CreatedAt` field in reverse order, meaning the most recent activities will
         * appear first.
         */
        public val Default: List<ActivitiesSort> =
            listOf(ActivitiesSort(ActivitiesSortField.CreatedAt, SortDirection.REVERSE))
    }
}

/**
 * Defines the fields by which activities can be sorted.
 *
 * This interface extends [SortField] and provides specific fields for sorting feed data. Each field
 * corresponds to a property of the [ActivityData] model, allowing for flexible sorting options when
 * querying activities.
 */
public sealed interface ActivitiesSortField : SortField<ActivityData> {

    /**
     * Sort by the creation timestamp of the activity. This field allows sorting activities by when
     * they were created (newest/oldest first).
     */
    public data object CreatedAt :
        ActivitiesSortField,
        SortField<ActivityData> by SortField.create("created_at", ActivityData::createdAt)

    /**
     * Sort by the popularity score of the activity. This field allows sorting activities by
     * popularity (most/least popular first).
     */
    public data object Popularity :
        ActivitiesSortField,
        SortField<ActivityData> by SortField.create("popularity", ActivityData::popularity)
}

/** Converts the [ActivitiesQuery] to a [QueryActivitiesRequest]. */
internal fun ActivitiesQuery.toRequest(): QueryActivitiesRequest =
    QueryActivitiesRequest(
        filter = filter?.toRequest(),
        sort = sort?.map { it.toRequest() },
        limit = limit,
        next = next,
        prev = previous,
    )
