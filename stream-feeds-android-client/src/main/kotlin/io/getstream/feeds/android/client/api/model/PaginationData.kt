package io.getstream.feeds.android.client.api.model

import io.getstream.feeds.android.core.generated.models.PagerResponse

/**
 * Data class representing pagination information for a paginated response.
 *
 * @property next The cursor for the next page, if available (more results available).
 * @property previous The cursor for the previous page, if available (more results available).
 */
public data class PaginationData(
    public val next: String? = null,
    public val previous: String? = null,
) {

    public companion object {
        /**
         * An empty pagination data instance, indicating no next or previous pages.
         */
        public val EMPTY: PaginationData = PaginationData()
    }
}

/**
 * Data class representing a paginated result containing a list of models and pagination data.
 *
 * @param T The type of the models in the list.
 * @property models The list of models in the paginated result.
 * @property pagination The pagination data for the result.
 */
internal data class PaginationResult<T>(
    val models: List<T>,
    val pagination: PaginationData,
)

/**
 * Extension function to convert a [PagerResponse] to a [PaginationData] model.
 *
 * @return A [PaginationData] instance containing the next and previous cursors.
 */
internal fun PagerResponse.toModel(): PaginationData = PaginationData(
    next = next,
    previous = prev,
)
