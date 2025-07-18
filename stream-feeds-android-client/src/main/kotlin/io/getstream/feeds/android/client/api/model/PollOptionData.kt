package io.getstream.feeds.android.client.api.model

import io.getstream.feeds.android.core.generated.models.PollOptionResponseData

/**
 * Data class representing a poll option.
 *
 * @property custom Custom data as a map.
 * @property id Unique identifier for the poll option.
 * @property text The text of the poll option.
 */
public data class PollOptionData(
    val custom: Map<String, Any?>,
    val id: String,
    val text: String
)

/**
 * Converts a [PollOptionResponseData] to a [PollOptionData] model.
 */
internal fun PollOptionResponseData.toModel(): PollOptionData = PollOptionData(
    custom = custom,
    id = id,
    text = text
)
