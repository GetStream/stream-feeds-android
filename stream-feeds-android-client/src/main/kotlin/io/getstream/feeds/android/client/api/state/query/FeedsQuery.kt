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
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.internal.model.mapping.toRequest
import io.getstream.feeds.android.client.internal.state.query.unsupportedLocalValue
import io.getstream.feeds.android.network.models.QueryFeedsRequest

/**
 * A query for retrieving feeds with filtering, sorting, and pagination options.
 *
 * Use this model to configure how feeds should be fetched from the Stream Feeds API. You can
 * specify filters to narrow down results, sorting options, and pagination parameters.
 *
 * Example usage:
 * ```kotlin
 * val query = FeedsQuery(
 *   filter = FeedsFilterField.visibility.equal("public"),
 *   sort = listOf(FeedsSort(FeedsSortField.CreatedAt, SortDirection.REVERSE)),
 *   limit = 20,
 * )
 * ```
 *
 * @property filter Optional filter to apply to the feeds query. Use this to narrow down results
 *   based on specific criteria. See [FeedsFilterField] for available filter fields and their
 *   supported operators.
 * @property sort Array of sorting criteria to apply to the feeds. If not specified, the API will
 *   use its default sorting.
 * @property limit Maximum number of feeds to return in a single request. If not specified, the API
 *   will use its default limit.
 * @property next Pagination cursor for fetching the next page of results. This is typically
 *   provided in the response of a previous request.
 * @property previous Pagination cursor for fetching the previous page of results. This is typically
 *   provided in the response of a previous request.
 * @property watch Whether to watch for real-time updates on the feeds. Defaults to true for
 *   real-time functionality.
 */
public data class FeedsQuery(
    public val filter: FeedsFilter? = null,
    public val sort: List<FeedsSort>? = null,
    public val limit: Int? = null,
    public val next: String? = null,
    public val previous: String? = null,
    public val watch: Boolean = true,
)

/** A type alias representing a filter specifically for [FeedData] using [FeedsFilterField]. */
public typealias FeedsFilter = Filter<FeedData, FeedsFilterField>

internal typealias FeedsQueryConfig = QueryConfiguration<FeedData, FeedsFilterField, FeedsSort>

/** Represents a field that can be used to filter feeds. */
public data class FeedsFilterField(
    override val remote: String,
    override val localValue: (FeedData) -> Any?,
) : FilterField<FeedData> {
    public companion object {
        /**
         * Filter by feed ID.
         *
         * Supported operators: `equal`, `in`
         */
        public val id: FeedsFilterField = FeedsFilterField("id", FeedData::id)

        /**
         * Filter by group ID.
         *
         * Supported operators: `equal`, `in`
         */
        public val groupId: FeedsFilterField = FeedsFilterField("group_id", FeedData::groupId)

        /**
         * Filter by feed.
         *
         * Supported operators: `equal`, `in`
         */
        public val feed: FeedsFilterField = FeedsFilterField("feed") { it.fid.rawValue }

        /**
         * Filter by creation timestamp.
         *
         * Supported operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
         */
        public val createdAt: FeedsFilterField = FeedsFilterField("created_at", FeedData::createdAt)

        /**
         * Filter by creator's user ID.
         *
         * Supported operators: `equal`, `in`
         */
        public val createdById: FeedsFilterField =
            FeedsFilterField("created_by_id") { it.createdBy.id }

        /**
         * Filter by creator's name.
         *
         * Supported operators: `equal`, `q`, `autocomplete`
         */
        public val createdByName: FeedsFilterField =
            FeedsFilterField("created_by.name") { it.createdBy.name }

        /**
         * Filter by feed description.
         *
         * Supported operators: `equal`, `q`, `autocomplete`
         */
        public val description: FeedsFilterField =
            FeedsFilterField("description", FeedData::description)

        /**
         * Filter by follower count.
         *
         * Supported operators: `equal`, `notEqual`, `greater`, `greaterOrEqual`, `less`,
         * `lessOrEqual`
         */
        public val followerCount: FeedsFilterField =
            FeedsFilterField("follower_count", FeedData::followerCount)

        /**
         * Filter by following count.
         *
         * Supported operators: `equal`, `notEqual`, `greater`, `greaterOrEqual`, `less`,
         * `lessOrEqual`
         */
        public val followingCount: FeedsFilterField =
            FeedsFilterField("following_count", FeedData::followingCount)

        /**
         * Filter by member count.
         *
         * Supported operators: `equal`, `notEqual`, `greater`, `greaterOrEqual`, `less`,
         * `lessOrEqual`
         */
        public val memberCount: FeedsFilterField =
            FeedsFilterField("member_count", FeedData::memberCount)

        /**
         * Filter by members.
         *
         * Supported operators: `in`
         */
        public val members: FeedsFilterField =
            FeedsFilterField(
                "members",
                // Local filtering is unsupported as we don't know the feed members
                unsupportedLocalValue,
            )

        /**
         * Filter by feed name.
         *
         * Supported operators: `equal`, `q`, `autocomplete`
         */
        public val name: FeedsFilterField = FeedsFilterField("name", FeedData::name)

        /**
         * Filter by last update timestamp.
         *
         * Supported operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
         */
        public val updatedAt: FeedsFilterField = FeedsFilterField("updated_at", FeedData::updatedAt)

        /**
         * Filter by feed visibility.
         *
         * Supported operators: `equal`, `in`
         */
        public val visibility: FeedsFilterField =
            FeedsFilterField("visibility", FeedData::visibility)

        /**
         * Filter by following feeds.
         *
         * Supported operators: `in`
         */
        public val followingFeeds: FeedsFilterField =
            FeedsFilterField(
                "following_feeds",
                // Local filtering is unsupported as we don't know the feeds that follow a feed
                unsupportedLocalValue,
            )

        /**
         * Filter by filter tags.
         *
         * Supported operators: `equal`, `in`, `contains`
         */
        public val filterTags: FeedsFilterField =
            FeedsFilterField("filter_tags", FeedData::filterTags)
    }
}

/**
 * Represents a sorting operation for feeds.
 *
 * @property field The field by which to sort the feeds.
 * @property direction The direction of the sort operation.
 */
public class FeedsSort(field: FeedsSortField, direction: SortDirection) :
    Sort<FeedData>(field, direction) {

    public companion object {

        /**
         * The default sorting for feeds queries. Sorts by creation date in ascending order (oldest
         * first).
         */
        public val Default: List<FeedsSort> =
            listOf(FeedsSort(FeedsSortField.CreatedAt, SortDirection.FORWARD))
    }
}

/**
 * Defines the fields by which feeds can be sorted.
 *
 * This interface extends [SortField] and provides specific fields for sorting feed data. Each field
 * corresponds to a property of the [FeedData] model, allowing for flexible sorting options when
 * querying feeds.
 */
public sealed interface FeedsSortField : SortField<FeedData> {

    /**
     * Sort by the creation timestamp of the feed. This field allows sorting feeds by when they were
     * created (newest/oldest first).
     */
    public data object CreatedAt :
        FeedsSortField, SortField<FeedData> by SortField.create("created_at", FeedData::createdAt)

    /**
     * Sort by the last update timestamp of the feed. This field allows sorting feeds by when they
     * were last updated (newest/oldest first).
     */
    public data object UpdatedAt :
        FeedsSortField, SortField<FeedData> by SortField.create("updated_at", FeedData::updatedAt)

    /**
     * Sort by the number of members in the feed. This field allows sorting feeds by member count
     * (most/least members).
     */
    public data object MemberCount :
        FeedsSortField,
        SortField<FeedData> by SortField.create("member_count", FeedData::memberCount)

    /**
     * Sort by the number of followers the feed has. This field allows sorting feeds by popularity
     * (most/least followed).
     */
    public data object FollowerCount :
        FeedsSortField,
        SortField<FeedData> by SortField.create("follower_count", FeedData::followerCount)

    /**
     * Sort by the number of feeds this feed is following. This field allows sorting feeds by how
     * many feeds they follow.
     */
    public data object FollowingCount :
        FeedsSortField,
        SortField<FeedData> by SortField.create("following_count", FeedData::followingCount)
}

/**
 * Converts the [FeedsQuery] to a [QueryFeedsRequest].
 *
 * This function maps the properties of the [FeedsQuery] to the corresponding fields in the
 * [QueryFeedsRequest], allowing it to be used in API requests.
 *
 * @return A [QueryFeedsRequest] object with the properties from this query.
 */
internal fun FeedsQuery.toRequest(): QueryFeedsRequest =
    QueryFeedsRequest(
        limit = limit,
        next = next,
        prev = previous,
        watch = watch,
        sort = sort?.map { it.toRequest() },
        filter = filter?.toRequest(),
    )
