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
import io.getstream.feeds.android.client.api.model.ModerationConfigData
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.internal.model.mapping.toRequest
import io.getstream.feeds.android.network.models.QueryModerationConfigsRequest

/**
 * A query configuration for fetching moderation configurations.
 *
 * This model defines the parameters used to fetch moderation configurations, including pagination
 * settings, sorting options, and filtering conditions.
 *
 * @property filter Filter conditions for the moderation configuration query. See
 *   [ModerationConfigsFilterField] for available filter fields and their supported operators.
 * @property limit The maximum number of moderation configurations to return.
 * @property next The pagination cursor for fetching the next page of configurations.
 * @property previous The pagination cursor for fetching the previous page of configurations.
 * @property sort The sorting criteria for configurations.
 */
public data class ModerationConfigsQuery(
    public val filter: ModerationConfigsFilter? = null,
    public val limit: Int? = null,
    public val next: String? = null,
    public val previous: String? = null,
    public val sort: List<ModerationConfigSort>? = null,
)

public typealias ModerationConfigsFilter =
    Filter<ModerationConfigData, ModerationConfigsFilterField>

internal typealias ModerationConfigsQueryConfig =
    QueryConfiguration<ModerationConfigData, ModerationConfigsFilterField, ModerationConfigSort>

public data class ModerationConfigsFilterField(
    override val remote: String,
    override val localValue: (ModerationConfigData) -> Any?,
) : FilterField<ModerationConfigData> {
    public companion object {
        /**
         * Filter by configuration key.
         *
         * Supported operators: `equal`, `in`, `autocomplete`
         */
        public val key: ModerationConfigsFilterField =
            ModerationConfigsFilterField("key", ModerationConfigData::key)

        /**
         * Filter by creation timestamp.
         *
         * Supported operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
         */
        public val createdAt: ModerationConfigsFilterField =
            ModerationConfigsFilterField("created_at", ModerationConfigData::createdAt)

        /**
         * Filter by last update timestamp.
         *
         * Supported operators: `equal`, `greater`, `greaterOrEqual`, `less`, `lessOrEqual`
         */
        public val updatedAt: ModerationConfigsFilterField =
            ModerationConfigsFilterField("updated_at", ModerationConfigData::updatedAt)

        /**
         * Filter by team.
         *
         * Supported operators: `equal`, `in`
         */
        public val team: ModerationConfigsFilterField =
            ModerationConfigsFilterField("team", ModerationConfigData::team)
    }
}

/**
 * Represents a sorting configuration for querying moderation configurations.
 *
 * @param field The field by which to sort the moderation configurations.
 * @param direction The direction of the sort.
 */
public class ModerationConfigSort(field: ModerationConfigsSortField, direction: SortDirection) :
    Sort<ModerationConfigData>(field, direction) {

    public companion object {

        /**
         * The default sorting for moderation configuration queries. Sorts by creation date in
         * descending order (newest first).
         */
        public val Default: List<ModerationConfigSort> =
            listOf(
                ModerationConfigSort(ModerationConfigsSortField.CreatedAt, SortDirection.REVERSE)
            )
    }
}

/**
 * Represents a field that can be used for sorting moderation configurations.
 *
 * This type provides a type-safe way to specify which field should be used when sorting moderation
 * configuration results.
 */
public sealed interface ModerationConfigsSortField : SortField<ModerationConfigData> {

    /**
     * Sort by the unique key of the configuration. This field allows sorting configurations by
     * their key (alphabetical order).
     */
    public data object Key :
        ModerationConfigsSortField,
        SortField<ModerationConfigData> by SortField.create("id", ModerationConfigData::key)

    /**
     * Sort by the creation timestamp of the configuration. This field allows sorting configurations
     * by when they were created (newest/oldest first).
     */
    public data object CreatedAt :
        ModerationConfigsSortField,
        SortField<ModerationConfigData> by SortField.create(
            "created_at",
            ModerationConfigData::createdAt,
        )

    /**
     * Sort by the last update timestamp of the configuration. This field allows sorting
     * configurations by when they were last updated (newest/oldest first).
     */
    public data object UpdatedAt :
        ModerationConfigsSortField,
        SortField<ModerationConfigData> by SortField.create(
            "updated_at",
            ModerationConfigData::updatedAt,
        )
}

/** Converts this [ModerationConfigsQuery] to a [QueryModerationConfigsRequest]. */
internal fun ModerationConfigsQuery.toRequest(): QueryModerationConfigsRequest =
    QueryModerationConfigsRequest(
        filter = filter?.toRequest(),
        limit = limit,
        next = next,
        prev = previous,
        sort = sort?.map { it.toRequest() },
    )
