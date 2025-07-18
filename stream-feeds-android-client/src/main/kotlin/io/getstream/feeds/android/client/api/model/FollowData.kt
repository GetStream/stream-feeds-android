package io.getstream.feeds.android.client.api.model

import io.getstream.feeds.android.client.internal.model.mapping.toDate
import io.getstream.feeds.android.core.generated.models.FollowResponse
import java.util.Date

/**
 * Model representing a follow relationship between feeds.
 *
 * @property createdAt The date and time when the follow was created.
 * @property custom A map of custom attributes associated with the follow.
 * @property followerRole The role of the follower in the follow relationship.
 * @property pushPreference The push notification preference for the follow.
 * @property requestAcceptedAt The date and time when the follow request was accepted, if applicable.
 * @property requestRejectedAt The date and time when the follow request was rejected, if applicable.
 * @property sourceFeed The source feed that initiated the follow.
 * @property status The current status of the follow relationship.
 * @property targetFeed The target feed that is being followed.
 * @property updatedAt The date and time when the follow relationship was last updated.
 */
public data class FollowData(
    public val createdAt: Date,
    public val custom: Map<String, Any?>?,
    public val followerRole: String,
    public val pushPreference: String,
    public val requestAcceptedAt: Date?,
    public val requestRejectedAt: Date? = null,
    public val sourceFeed: FeedData,
    public val status: FollowStatus,
    public val targetFeed: FeedData,
    public val updatedAt: Date,
) {

    /**
     * Unique identifier for the follow relationship, generated from the source and target feed IDs
     * and the creation timestamp.
     * Used for simpler identification of follow relationships.
     */
    public val id: String
        get() = "${sourceFeed.fid}${targetFeed.fid}${createdAt.time}"

    internal val isFollower: Boolean
        get() = status == FollowStatus.Accepted

    internal val isFollowing: Boolean
        get() = status == FollowStatus.Accepted

    internal val isFollowRequest: Boolean
        get() = status == FollowStatus.Pending

    internal fun isFollowerOf(fid: FeedId): Boolean {
        return isFollower && targetFeed.fid == fid
    }

    internal fun isFollowing(fid: FeedId): Boolean {
        return isFollowing && sourceFeed.fid == fid
    }
}

/**
 * Sealed class representing the status of a follow relationship.
 *
 * @property string The string representation of the follow status.
 */
public sealed class FollowStatus(public val string: String) {

    /**
     * Represents a follow relationship that has been accepted.
     */
    public object Accepted : FollowStatus("accepted")

    /**
     * Represents a follow relationship that is pending approval.
     */
    public object Pending : FollowStatus("pending")

    /**
     * Represents a follow relationship that has been rejected.
     */
    public object Rejected : FollowStatus("rejected")

    /**
     * Represents an unknown follow status.
     */
    public data class Unknown(val unknownValue: String) : FollowStatus(unknownValue)
}

/**
 * Converts a [FollowResponse] to a [FollowData] model.
 */
internal fun FollowResponse.toModel(): FollowData = FollowData(
    createdAt = createdAt.toDate(),
    custom = custom,
    followerRole = followerRole,
    pushPreference = pushPreference.value,
    requestAcceptedAt = requestAcceptedAt?.toDate(),
    requestRejectedAt = requestRejectedAt?.toDate(),
    sourceFeed = sourceFeed.toModel(),
    status = when (status) {
        FollowResponse.Status.Accepted -> FollowStatus.Accepted
        FollowResponse.Status.Pending -> FollowStatus.Pending
        FollowResponse.Status.Rejected -> FollowStatus.Rejected
        is FollowResponse.Status.Unknown -> FollowStatus.Unknown(
            unknownValue = (status as FollowResponse.Status.Unknown).unknownValue,
        )
    },
    targetFeed = targetFeed.toModel(),
    updatedAt = updatedAt.toDate(),
)

/**
 * Converts a [FollowResponse.PushPreference] to a [String] representation.
 */
internal fun FollowResponse.Status.toModel(): FollowStatus = when (this) {
    FollowResponse.Status.Accepted -> FollowStatus.Accepted
    FollowResponse.Status.Pending -> FollowStatus.Pending
    FollowResponse.Status.Rejected -> FollowStatus.Rejected
    is FollowResponse.Status.Unknown -> FollowStatus.Unknown(unknownValue)
}