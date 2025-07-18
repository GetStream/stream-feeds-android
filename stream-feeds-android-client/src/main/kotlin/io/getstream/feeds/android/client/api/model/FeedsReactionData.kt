package io.getstream.feeds.android.client.api.model

import io.getstream.feeds.android.client.internal.model.mapping.toDate
import io.getstream.feeds.android.core.generated.models.FeedsReactionResponse
import java.util.Date

/**
 * Data class representing a feed reaction.
 *
 * @property activityId The ID of the activity this reaction is associated with.
 * @property createdAt The date and time when the reaction was created.
 * @property custom Optional custom data as a map.
 * @property type The type of the reaction.
 * @property updatedAt The date and time when the reaction was last updated.
 * @property user The user who made the reaction.
 */
public data class FeedsReactionData(
    val activityId: String,
    val createdAt: Date,
    val custom: Map<String, Any?>?,
    val type: String,
    val updatedAt: Date,
    val user: UserData,
) {

    /**
     * Unique identifier for the reaction, generated from the activity ID and user ID.
     */
    public val id: String
        get() = "${activityId}${user.id}"
}

/**
 * Converts a [FeedsReactionResponse] to a [FeedsReactionData] model.
 */
internal fun FeedsReactionResponse.toModel(): FeedsReactionData = FeedsReactionData(
    activityId = activityId,
    createdAt = createdAt.toDate(),
    custom = custom,
    type = type,
    updatedAt = updatedAt.toDate(),
    user = user.toModel(),
)
