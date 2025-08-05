package io.getstream.feeds.android.client.api.state.query

import io.getstream.android.core.query.Filter
import io.getstream.android.core.query.Sort
import io.getstream.android.core.query.SortDirection
import io.getstream.android.core.query.SortField
import io.getstream.android.core.query.toRequest
import io.getstream.feeds.android.client.api.model.ModerationConfigData
import io.getstream.feeds.android.client.internal.model.mapping.toRequest
import io.getstream.feeds.android.core.generated.models.QueryModerationConfigsRequest


/**
 * A query configuration for fetching moderation configurations.
 *
 * This model defines the parameters used to fetch moderation configurations, including
 * pagination settings, sorting options, and filtering conditions.
 *
 * @property filter Filter conditions for the moderation configuration query.
 * @property limit The maximum number of moderation configurations to return.
 * @property next The pagination cursor for fetching the next page of configurations.
 * @property previous The pagination cursor for fetching the previous page of configurations.
 * @property sort The sorting criteria for configurations.
 */
public data class ModerationConfigsQuery(
    public val filter: Filter? = null,
    public val limit: Int? = null,
    public val next: String? = null,
    public val previous: String? = null,
    public val sort: List<ModerationConfigSort>? = null,
)

/**
 * Represents a sorting configuration for querying moderation configurations.
 *
 * @param field The field by which to sort the moderation configurations.
 * @param direction The direction of the sort.
 */
public class ModerationConfigSort(
    field: ModerationConfigsSortField,
    direction: SortDirection,
) : Sort<ModerationConfigData>(field, direction) {

    public companion object {

        /**
         * The default sorting for moderation configuration queries.
         * Sorts by creation date in descending order (newest first).
         */
        public val Default: List<ModerationConfigSort> = listOf(
            ModerationConfigSort(ModerationConfigsSortField.CreatedAt, SortDirection.REVERSE),
        )
    }
}

/**
 * Represents a field that can be used for sorting moderation configurations.
 *
 * This type provides a type-safe way to specify which field should be used
 * when sorting moderation configuration results.
 */
public sealed interface ModerationConfigsSortField : SortField<ModerationConfigData> {

    /**
     * Sort by the unique key of the configuration.
     * This field allows sorting configurations by their key (alphabetical order).
     */
    public data object Key : ModerationConfigsSortField,
        SortField<ModerationConfigData> by SortField.create("id", ModerationConfigData::key)

    /**
     * Sort by the creation timestamp of the configuration.
     * This field allows sorting configurations by when they were created (newest/oldest first).
     */
    public data object CreatedAt : ModerationConfigsSortField,
        SortField<ModerationConfigData> by SortField.create(
            "created_at",
            ModerationConfigData::createdAt
        )

    /**
     * Sort by the last update timestamp of the configuration.
     * This field allows sorting configurations by when they were last updated (newest/oldest first).
     */
    public data object UpdatedAt : ModerationConfigsSortField,
        SortField<ModerationConfigData> by SortField.create(
            "updated_at",
            ModerationConfigData::updatedAt
        )
}

/**
 * Converts this [ModerationConfigsQuery] to a [QueryModerationConfigsRequest].
 */
internal fun ModerationConfigsQuery.toRequest(): QueryModerationConfigsRequest =
    QueryModerationConfigsRequest(
        filter = filter?.toRequest(),
        limit = limit,
        next = next,
        prev = previous,
        sort = sort?.map { it.toRequest() },
    )
