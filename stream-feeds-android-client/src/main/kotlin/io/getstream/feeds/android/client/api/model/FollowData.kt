/*
 * Copyright (c) 2014-2025 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-feeds-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.getstream.feeds.android.client.api.model

import java.util.Date

/**
 * Model representing a follow relationship between feeds.
 *
 * @property createdAt The date and time when the follow was created.
 * @property custom A map of custom attributes associated with the follow.
 * @property followerRole The role of the follower in the follow relationship.
 * @property pushPreference The push notification preference for the follow.
 * @property requestAcceptedAt The date and time when the follow request was accepted, if
 *   applicable.
 * @property requestRejectedAt The date and time when the follow request was rejected, if
 *   applicable.
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
     * and the creation timestamp. Used for simpler identification of follow relationships.
     */
    public val id: String
        get() = "${sourceFeed.fid}${targetFeed.fid}${createdAt.time}"
}

/**
 * Sealed class representing the status of a follow relationship.
 *
 * @property value The string representation of the follow status.
 */
public sealed class FollowStatus(public val value: String) {

    /** Represents a follow relationship that has been accepted. */
    public object Accepted : FollowStatus("accepted")

    /** Represents a follow relationship that is pending approval. */
    public object Pending : FollowStatus("pending")

    /** Represents a follow relationship that has been rejected. */
    public object Rejected : FollowStatus("rejected")

    /** Represents an unknown follow status. */
    public data class Unknown(val unknownValue: String) : FollowStatus(unknownValue)
}
