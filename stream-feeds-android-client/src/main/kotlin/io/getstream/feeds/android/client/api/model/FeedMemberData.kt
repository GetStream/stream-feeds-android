package io.getstream.feeds.android.client.api.model

import io.getstream.feeds.android.client.internal.model.mapping.toDate
import io.getstream.feeds.android.core.generated.models.FeedMemberResponse
import java.util.Date

/**
 * Model representing a member of a feed.
 *
 * @property createdAt The date and time when the member was created.
 * @property custom A map of custom attributes associated with the member.
 * @property inviteAcceptedAt The date and time when the invite was accepted, if applicable.
 * @property inviteRejectedAt The date and time when the invite was rejected, if applicable.
 * @property role The role of the member in the feed.
 * @property status The status of the member in the feed.
 * @property updatedAt The date and time when the member was last updated.
 * @property user The user data associated with the member.
 */
public data class FeedMemberData(
    public val createdAt: Date,
    public val custom: Map<String, Any?>?,
    public val inviteAcceptedAt: Date?,
    public val inviteRejectedAt: Date?,
    public val role: String,
    public val status: FeedMemberStatus,
    public val updatedAt: Date,
    public val user: UserData,
) {

    /**
     * Unique identifier of the feed member, same as the user's ID.
     */
    public val id: String
        get() = user.id
}

/**
 * Sealed class representing the status of a feed member.
 *
 * @property value The string representation of the status.
 */
public sealed class FeedMemberStatus(public val value: String) {

    /**
     * Represents a feed member.
     */
    public object Member : FeedMemberStatus("member")

    /**
     * Represents a feed whose membership is pending approval.
     */
    public object Pending : FeedMemberStatus("pending")

    /**
     * Represents a feed member whose invite has been rejected.
     */
    public object Rejected : FeedMemberStatus("rejected")

    /**
     * Represents a feed member with an unknown status.
     */
    public data class Unknown(val unknownValue: String) : FeedMemberStatus(unknownValue)
}

/**
 * Converts a [FeedMemberResponse] to a [FeedMemberData] model.
 */
internal fun FeedMemberResponse.toModel() = FeedMemberData(
    createdAt = createdAt.toDate(),
    custom = custom,
    inviteAcceptedAt = inviteAcceptedAt?.toDate(),
    inviteRejectedAt = inviteRejectedAt?.toDate(),
    role = role,
    status = status.toModel(),
    updatedAt = updatedAt.toDate(),
    user = user.toModel()
)

/**
 * Converts a [FeedMemberResponse.Status] to a [FeedMemberStatus] model.
 */
internal fun FeedMemberResponse.Status.toModel(): FeedMemberStatus = when (this) {
    FeedMemberResponse.Status.Member -> FeedMemberStatus.Member
    FeedMemberResponse.Status.Pending -> FeedMemberStatus.Pending
    FeedMemberResponse.Status.Rejected -> FeedMemberStatus.Rejected
    is FeedMemberResponse.Status.Unknown -> FeedMemberStatus.Unknown(unknownValue)
}
