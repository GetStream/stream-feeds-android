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
import io.getstream.android.core.api.model.location.BoundingBox
import io.getstream.android.core.api.model.location.CircularRegion
import io.getstream.android.core.api.sort.Sort
import io.getstream.android.core.api.sort.SortDirection
import io.getstream.android.core.api.sort.SortField
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.internal.model.toCoordinate
import io.getstream.feeds.android.client.internal.state.query.unsupportedLocalValue

/**
 * A query for retrieving activities with filtering, sorting, and pagination options.
 *
 * Use this model to configure how activities should be fetched from the Stream Feeds API. You can
 * specify filters to narrow down results, sorting options, and pagination parameters.
 *
 * Example usage:
 * ```kotlin
 * val query = ActivitiesQuery(
 *   filter = ActivitiesFilterField.Id.equal("activity-id-1"),
 *   sort = listOf(ActivitiesSort(ActivitiesSortField.CreatedAt, SortDirection.REVERSE)),
 *   limit = 20,
 * )
 * ```
 *
 * @property filter Optional filter to apply to the activities query. Use this to narrow down
 *   results based on specific criteria. See [ActivitiesFilterField] for available filter fields and
 *   their supported operators.
 * @property sort Array of sorting criteria to apply to the activities. If not specified, the API
 *   will use its default sorting.
 * @property limit Maximum number of activities to return in a single request. If not specified, the
 *   API will use its default limit.
 * @property next Pagination cursor for fetching the next page of results. This is typically
 *   provided in the response of a previous request.
 * @property previous Pagination cursor for fetching the previous page of results. This is typically
 *   provided in the response of a previous request.
 */
public data class ActivitiesQuery(
    public val filter: ActivitiesFilter? = null,
    public val sort: List<ActivitiesSort>? = null,
    public val limit: Int? = null,
    public val next: String? = null,
    public val previous: String? = null,
)

/**
 * A type alias representing a filter specifically for [ActivityData] using [ActivitiesFilterField].
 */
public typealias ActivitiesFilter = Filter<ActivityData, ActivitiesFilterField>

/** Represents a field that can be used to filter activities. */
public data class ActivitiesFilterField(
    override val remote: String,
    override val localValue: (ActivityData) -> Any?,
) : FilterField<ActivityData> {
    public companion object {
        /**
         * Filter by activity creation timestamp.
         *
         * Supported operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
         */
        public val createdAt: ActivitiesFilterField =
            ActivitiesFilterField("created_at", ActivityData::createdAt)

        /**
         * Filter by activity ID.
         *
         * Supported operators: `equal`, `in`
         */
        public val id: ActivitiesFilterField = ActivitiesFilterField("id", ActivityData::id)

        /**
         * Filter by the feed ID(s) that the activity belongs to.
         *
         * Supported operators: `equal`, `in`
         */
        public val feed: ActivitiesFilterField =
            ActivitiesFilterField("feed", unsupportedLocalValue)

        /**
         * Filter by activity filter tags.
         *
         * Supported operators: `equal`, `in`, `contains`
         */
        public val filterTags: ActivitiesFilterField =
            ActivitiesFilterField("filter_tags", ActivityData::filterTags)

        /**
         * Filter by activity popularity score.
         *
         * Supported operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
         */
        public val popularity: ActivitiesFilterField =
            ActivitiesFilterField("popularity", ActivityData::popularity)

        /**
         * Filter by activity search data.
         *
         * Supported operators: `q`, `autocomplete`
         */
        public val searchData: ActivitiesFilterField =
            ActivitiesFilterField("search_data", ActivityData::searchData)

        /**
         * Filter by activity text content.
         *
         * Supported operators: `equal`, `q`, `autocomplete`
         */
        public val text: ActivitiesFilterField = ActivitiesFilterField("text", ActivityData::text)

        /**
         * Filter by activity type.
         *
         * Supported operators: `equal`, `in`
         */
        public val activityType: ActivitiesFilterField =
            ActivitiesFilterField("activity_type", ActivityData::type)

        /**
         * Filter by user ID who created the activity.
         *
         * Supported operators: `equal`, `in`
         */
        public val userId: ActivitiesFilterField = ActivitiesFilterField("user_id") { it.user.id }

        /**
         * Filter by activity expiration timestamp.
         *
         * Supported operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`,
         * `exists`
         */
        public val expiresAt: ActivitiesFilterField =
            ActivitiesFilterField("expires_at", ActivityData::expiresAt)

        /**
         * Filter by activity interest tags.
         *
         * Supported operators: `equal`, `in`, `contains`
         */
        public val interestTags: ActivitiesFilterField =
            ActivitiesFilterField("interest_tags", ActivityData::interestTags)

        /**
         * Filter by proximity to a location.
         *
         * Requires matching against [CircularRegion] or a [Map] with `lat` (latitude), `lng`
         * (longitude), and `distance` (in kilometers).
         *
         * Supported operators: `equal`
         */
        public val near: ActivitiesFilterField =
            ActivitiesFilterField("near") { it.location?.toCoordinate() }

        /**
         * Filter by activities within a bounding box.
         *
         * Can be matched against [BoundingBox] or a [Map] with `ne_lat`, `ne_lng` (northeast
         * corner), `sw_lat`, `sw_lng` (southwest corner).
         *
         * Supported operators: `equal`
         */
        public val withinBounds: ActivitiesFilterField =
            ActivitiesFilterField("within_bounds") { it.location?.toCoordinate() }
    }
}

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
