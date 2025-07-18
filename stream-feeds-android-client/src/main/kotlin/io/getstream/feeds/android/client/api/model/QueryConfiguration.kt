package io.getstream.feeds.android.client.api.model

import io.getstream.android.core.query.Filter
import io.getstream.android.core.query.Sort

/**
 * Wrapper around the query configuration consisting of a [Filter] and a list of [Sort] operations.
 *
 * @property filter The filter for the query.
 * @property sort The list of sorting operations for the query.
 */
internal data class QueryConfiguration<T>(
    val filter: Filter?,
    val sort: List<Sort<T>>?,
) {

    companion object {
        /**
         * An empty query configuration with no filter and no sort operations.
         * This can be used as a default or placeholder configuration.
         */
        val Empty = QueryConfiguration<Any>(null, null)
    }
}
