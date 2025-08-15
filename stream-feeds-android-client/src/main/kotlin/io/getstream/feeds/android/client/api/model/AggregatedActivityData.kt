package io.getstream.feeds.android.client.api.model

import io.getstream.feeds.android.client.internal.model.mapping.toDate
import io.getstream.feeds.android.core.generated.models.AggregatedActivityResponse
import java.util.Date

/**
 * Represents aggregated activity data in a feed.
 *
 * This class encapsulates a list of activities, their count, the group they belong to,
 * the score of the aggregation, and metadata such as creation and update timestamps.
 *
 * @property activities The list of activities included in this aggregation.
 * @property activityCount The total number of activities in this aggregation.
 * @property createdAt The date and time when this aggregation was created.
 * @property group The group identifier for this aggregation.
 * @property score The score associated with this aggregation.
 * @property updatedAt The date and time when this aggregation was last updated.
 * @property userCount The number of unique users involved in these activities.
 */
public data class AggregatedActivityData(
    public val activities: List<ActivityData>,
    public val activityCount: Int,
    public val createdAt: Date,
    public val group: String,
    public val score: Float,
    public val updatedAt: Date,
    public val userCount: Int,
) {

    /**
     * Returns a unique identifier for this aggregated activity data.
     *
     * The identifier is constructed from the first activity's ID, or a combination of
     * the activity count, user count, score, creation date, and group if no activities are present.
     */
    public val id: String
        get() = activities.firstOrNull()?.id
            ?: "$activityCount-$userCount-$score-$createdAt-($group)"
}

/**
 * Converts an [AggregatedActivityResponse] to an [AggregatedActivityData] model.
 */
internal fun AggregatedActivityResponse.toModel(): AggregatedActivityData {
    return AggregatedActivityData(
        activities = activities.map { it.toModel() },
        activityCount = activityCount,
        createdAt = createdAt.toDate(),
        group = group,
        score = score,
        updatedAt = updatedAt.toDate(),
        userCount = userCount,
    )
}