package io.getstream.feeds.android.client.api.model

import io.getstream.feeds.android.client.internal.model.mapping.toDate
import io.getstream.feeds.android.core.generated.models.ActivityPinResponse
import io.getstream.feeds.android.core.generated.models.PinActivityResponse
import java.util.Date

/**
 * A data model representing a pinned activity in the Stream Feeds system.
 *
 * This class contains all the information about an activity that has been pinned
 * to a specific feed, including the activity content, feed context, and timing information.
 * Pinned activities are typically displayed prominently at the top of feeds to highlight
 * important or featured content.
 *
 * @property activity The activity that has been pinned. This property contains the full
 * activity data for the pinned item, allowing access to all activity information including
 * content, reactions, comments, and metadata without additional lookups.
 * @property createdAt The date and time when the activity was pinned to the feed.
 * @property fid The feed identifier where this activity is pinned. This property specifies
 * which feed the activity is pinned to, as activities can be pinned to multiple feeds
 * independently.
 * @property updatedAt The date and time when the pin was last updated. This tracks any
 * modifications to the pin configuration or metadata.
 * @property userId The identifier of the user who pinned the activity. This property tracks
 * which user performed the pinning action, which is useful for permissions, auditing,
 * and managing pin ownership.
 */
public data class ActivityPinData(
    val activity: ActivityData,
    val createdAt: Date,
    val fid: FeedId,
    val updatedAt: Date,
    val userId: String
) {
    /**
     * Unique identifier for the pinned activity, generated from the feed ID, activity ID,
     * and user ID. This identifier is used for simpler identification of pinned activities.
     */
    public val id: String
        get() = "${fid.rawValue}${activity.id}${userId}"
}

/**
 * Converts an [ActivityPinResponse] to an [ActivityPinData] model.
 */
internal fun ActivityPinResponse.toModel(): ActivityPinData = ActivityPinData(
    activity = activity.toModel(),
    createdAt = Date(createdAt.toInstant().toEpochMilli()),
    fid = FeedId(feed),
    updatedAt = Date(updatedAt.toInstant().toEpochMilli()),
    userId = user.id,
)

/**
 * Converts a [PinActivityResponse] to an [ActivityPinData] model.
 *
 * This conversion is used when pinning an activity to a feed, where the response
 * contains the pinned activity details.
 */
internal fun PinActivityResponse.toModel(): ActivityPinData = ActivityPinData(
    activity = activity.toModel(),
    createdAt = createdAt.toDate(),
    fid = FeedId(fid),
    updatedAt = createdAt.toDate(), // no updated_at
    userId = userId,
)