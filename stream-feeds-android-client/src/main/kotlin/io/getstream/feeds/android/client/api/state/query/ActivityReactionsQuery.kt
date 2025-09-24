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

import io.getstream.android.core.api.filter.Filter
import io.getstream.android.core.api.filter.FilterField
import io.getstream.android.core.api.filter.toRequest
import io.getstream.android.core.api.sort.Sort
import io.getstream.android.core.api.sort.SortDirection
import io.getstream.android.core.api.sort.SortField
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.internal.model.mapping.toRequest
import io.getstream.feeds.android.network.models.QueryActivityReactionsRequest

/**
 * A query for retrieving activity reactions with filtering, sorting, and pagination options.
 *
 * Use this struct to configure how activity reactions should be fetched from the Stream Feeds API.
 * You can specify filters to narrow down results, sorting options, and pagination parameters.
 *
 * @property activityId The unique identifier of the activity to fetch reactions for.
 * @property filter Optional filter to apply to the activity reactions query. Use this to narrow
 *   down results based on specific criteria. See [ActivityReactionsFilterField] for available
 *   filter fields and their supported operators.
 * @property limit Optional limit for the number of reactions to fetch. If not specified, the API
 *   will use its default limit.
 * @property next Pagination cursor for fetching the next page of results. This is typically
 *   provided in the response of a previous request.
 * @property previous Pagination cursor for fetching the previous page of results. This is typically
 *   provided in the response of a previous request.
 * @property sort Array of sorting criteria to apply to the activity reactions. If not specified,
 *   the API will use its default sorting.
 */
public data class ActivityReactionsQuery(
    public val activityId: String,
    public val filter: ActivityReactionsFilter? = null,
    public val limit: Int? = null,
    public val next: String? = null,
    public val previous: String? = null,
    public val sort: List<ActivityReactionsSort>? = null,
)

/**
 * A type alias representing a filter specifically for [FeedsReactionData] using
 * [ActivityReactionsFilterField].
 */
public typealias ActivityReactionsFilter = Filter<FeedsReactionData, ActivityReactionsFilterField>

internal typealias ActivityReactionsQueryConfig =
    QueryConfiguration<FeedsReactionData, ActivityReactionsFilterField, ActivityReactionsSort>

/** Represents a field that can be used to filter activity reactions. */
public data class ActivityReactionsFilterField(
    override val remote: String,
    override val localValue: (FeedsReactionData) -> Any?,
) : FilterField<FeedsReactionData> {
    public companion object {
        /**
         * Filter by reaction type.
         *
         * Supported operators: `equal`, `in`
         */
        public val reactionType: ActivityReactionsFilterField =
            ActivityReactionsFilterField("reaction_type", FeedsReactionData::type)

        /**
         * Filter by user ID who created the reaction.
         *
         * Supported operators: `equal`, `in`
         */
        public val userId: ActivityReactionsFilterField =
            ActivityReactionsFilterField("user_id") { it.user.id }

        /**
         * Filter by creation timestamp.
         *
         * Supported operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
         */
        public val createdAt: ActivityReactionsFilterField =
            ActivityReactionsFilterField("created_at", FeedsReactionData::createdAt)
    }
}

/**
 * A sort configuration for activity reactions.
 *
 * @param field The field to sort by.
 * @param direction The direction of the sort, either ascending or descending.
 */
public class ActivityReactionsSort(field: ActivityReactionsSortField, direction: SortDirection) :
    Sort<FeedsReactionData>(field, direction) {

    public companion object {

        /**
         * Default sorting configuration for activity reactions.
         *
         * This uses the `CreatedAt` field in reverse order, meaning the most recently created
         * reactions will appear first.
         */
        public val Default: List<ActivityReactionsSort> =
            listOf(
                ActivityReactionsSort(ActivityReactionsSortField.CreatedAt, SortDirection.REVERSE)
            )
    }
}

/**
 * Represents a field that can be used for sorting activity reactions.
 *
 * This type provides a type-safe way to specify which field should be used when sorting activity
 * reactions results.
 */
public sealed interface ActivityReactionsSortField : SortField<FeedsReactionData> {

    /**
     * Sort by the creation timestamp of the reaction. This field allows sorting reactions by when
     * they were created (newest/oldest first).
     */
    public data object CreatedAt :
        ActivityReactionsSortField,
        SortField<FeedsReactionData> by SortField.create(
            "created_at",
            FeedsReactionData::createdAt,
        )
}

/**
 * Converts this [ActivityReactionsQuery] to a [QueryActivityReactionsRequest].
 *
 * This function maps the properties of the query to the corresponding request format used by the
 * Stream Feeds API.
 *
 * @return A [QueryActivityReactionsRequest] containing the same parameters as this query.
 */
internal fun ActivityReactionsQuery.toRequest(): QueryActivityReactionsRequest =
    QueryActivityReactionsRequest(
        filter = filter?.toRequest(),
        limit = limit,
        next = next,
        prev = previous,
        sort = sort?.map { it.toRequest() },
    )
