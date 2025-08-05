package io.getstream.feeds.android.client.internal.model.mapping

import io.getstream.android.core.query.Sort
import io.getstream.feeds.android.core.generated.models.SortParamRequest

/**
 * Converts a [Sort] operation to a [SortParamRequest] for use in API requests.
 *
 * @return A [SortParamRequest] representing the sort operation.
 */
internal fun Sort<*>.toRequest(): SortParamRequest = SortParamRequest(
    field = field.remote,
    direction = direction.value
)