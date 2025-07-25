package io.getstream.feeds.android.client.api.state.query

import io.getstream.android.core.query.Filter

// TODO: Implement once available in the API
public data class ModerationConfigsQuery(
    public val filter: Filter? = null,
    public val limit: Int? = null,
    public val next: String? = null,
    public val previous: String? = null,
    public val sort: List<ModerationConfigsSortField>? = null,
)

public sealed interface ModerationConfigsSortField