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
import io.getstream.android.core.api.sort.Sort
import io.getstream.android.core.api.sort.SortDirection
import io.getstream.android.core.api.sort.SortField
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedMemberData

/**
 * A query for retrieving feed members with filtering, sorting, and pagination options.
 *
 * Use this class to configure how feed members should be fetched from the Stream Feeds API. You can
 * specify filters to narrow down results, sorting options, and pagination parameters.
 *
 * @property fid The feed ID to fetch members for.
 * @property filter Optional filter to apply to the members query. Use this to narrow down results
 *   based on specific criteria. See [MembersFilterField] for available filter fields and their
 *   supported operators.
 * @property sort Array of sorting criteria to apply to the members. If not specified, the API will
 *   use its default sorting.
 * @property limit Maximum number of members to return in a single request. If not specified, the
 *   API will use its default limit.
 * @property next Pagination cursor for fetching the next page of results. This is typically
 *   provided in the response of a previous request.
 * @property previous Pagination cursor for fetching the previous page of results. This is typically
 *   provided in the response of a previous request.
 */
public data class MembersQuery(
    public val fid: FeedId,
    public val filter: MembersFilter? = null,
    public val sort: List<MembersSort>? = null,
    public val limit: Int? = null,
    public val next: String? = null,
    public val previous: String? = null,
)

/**
 * A type alias representing a filter specifically for [FeedMemberData] using [MembersFilterField].
 */
public typealias MembersFilter = Filter<FeedMemberData, MembersFilterField>

/** Represents a field that can be used to filter feed members. */
public data class MembersFilterField(
    override val remote: String,
    override val localValue: (FeedMemberData) -> Any?,
) : FilterField<FeedMemberData> {
    public companion object {
        /**
         * Filter by creation timestamp.
         *
         * Supported operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
         */
        public val createdAt: MembersFilterField =
            MembersFilterField("created_at", FeedMemberData::createdAt)

        /**
         * Filter by member role.
         *
         * Supported operators: `equal`, `in`
         */
        public val role: MembersFilterField = MembersFilterField("role", FeedMemberData::role)

        /**
         * Filter by member status.
         *
         * Supported operators: `equal`, `in`
         */
        public val status: MembersFilterField = MembersFilterField("status") { it.status.value }

        /**
         * Filter by last update timestamp.
         *
         * Supported operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
         */
        public val updatedAt: MembersFilterField =
            MembersFilterField("updated_at", FeedMemberData::updatedAt)

        /**
         * Filter by user ID.
         *
         * Supported operators: `equal`, `in`
         */
        public val userId: MembersFilterField = MembersFilterField("user_id") { it.user.id }
    }
}

/**
 * Represents a sorting operation for feed members.
 *
 * @property field The field by which to sort the members.
 * @property direction The direction of the sort operation.
 */
public class MembersSort(field: MembersSortField, direction: SortDirection) :
    Sort<FeedMemberData>(field, direction) {

    public companion object {

        /**
         * Default sorting configuration for feed members.
         *
         * This uses the `CreatedAt` field in reverse order, meaning the most recently added members
         * will appear first.
         */
        public val Default: List<MembersSort> =
            listOf(MembersSort(MembersSortField.CreatedAt, SortDirection.REVERSE))
    }
}

/**
 * Represents a field that can be used for sorting members.
 *
 * This type provides a type-safe way to specify which field should be used when sorting members
 * results.
 */
public sealed interface MembersSortField : SortField<FeedMemberData> {

    /**
     * Sort by the creation timestamp of the member. This field allows sorting members by when they
     * were added to the feed (newest/oldest first).
     */
    public data object CreatedAt :
        MembersSortField,
        SortField<FeedMemberData> by SortField.create("created_at", FeedMemberData::createdAt)

    /**
     * Sort by the last update timestamp of the member. This field allows sorting members by when
     * they were last updated (newest/oldest first).
     */
    public data object UpdatedAt :
        MembersSortField,
        SortField<FeedMemberData> by SortField.create("updated_at", FeedMemberData::updatedAt)

    /**
     * Sort by the user ID of the member. This field allows sorting members alphabetically by user
     * ID.
     */
    public data object UserId :
        MembersSortField, SortField<FeedMemberData> by SortField.create("user_id", { it.user.id })
}

/** Converts a [MembersQuery] to a [QueryFeedMembersRequest]. */
