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

package io.getstream.feeds.android.client.internal.model

import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.FollowStatus
import io.getstream.feeds.android.network.models.FollowResponse

internal val FollowData.isFollower: Boolean
    get() = status == FollowStatus.Accepted

internal val FollowData.isFollowing: Boolean
    get() = status == FollowStatus.Accepted

internal val FollowData.isFollowRequest: Boolean
    get() = status == FollowStatus.Pending

internal fun FollowData.isFollowerOf(fid: FeedId): Boolean {
    return isFollower && targetFeed.fid == fid
}

internal fun FollowData.isFollowing(fid: FeedId): Boolean {
    return isFollowing && sourceFeed.fid == fid
}

/**
 * Converts a [io.getstream.feeds.android.network.models.FollowResponse] to a
 * [io.getstream.feeds.android.client.api.model.FollowData] model.
 */
internal fun FollowResponse.toModel(): FollowData =
    FollowData(
        createdAt = createdAt,
        custom = custom,
        followerRole = followerRole,
        pushPreference = pushPreference.value,
        requestAcceptedAt = requestAcceptedAt,
        requestRejectedAt = requestRejectedAt,
        sourceFeed = sourceFeed.toModel(),
        status =
            when (status) {
                FollowResponse.Status.Accepted -> FollowStatus.Accepted
                FollowResponse.Status.Pending -> FollowStatus.Pending
                FollowResponse.Status.Rejected -> FollowStatus.Rejected
                is FollowResponse.Status.Unknown ->
                    FollowStatus.Unknown(
                        unknownValue = (status as FollowResponse.Status.Unknown).unknownValue
                    )
            },
        targetFeed = targetFeed.toModel(),
        updatedAt = updatedAt,
    )

/**
 * Converts a [io.getstream.feeds.android.network.models.FollowResponse.Status] to a
 * [io.getstream.feeds.android.client.api.model.FollowStatus] representation.
 */
internal fun FollowResponse.Status.toModel(): FollowStatus =
    when (this) {
        FollowResponse.Status.Accepted -> FollowStatus.Accepted
        FollowResponse.Status.Pending -> FollowStatus.Pending
        FollowResponse.Status.Rejected -> FollowStatus.Rejected
        is FollowResponse.Status.Unknown -> FollowStatus.Unknown(unknownValue)
    }
