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

import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.network.models.FeedOwnData
import io.getstream.feeds.android.network.models.FeedResponse
import io.getstream.feeds.android.network.models.FollowResponse

/** Converts a [FeedResponse] to a [FeedData] model. */
internal fun FeedResponse.toModel(): FeedData =
    FeedData(
        createdAt = createdAt,
        createdBy = createdBy.toModel(),
        custom = custom,
        deletedAt = deletedAt,
        description = description,
        fid = FeedId(feed),
        filterTags = filterTags,
        followerCount = followerCount,
        followingCount = followingCount,
        groupId = groupId,
        id = id,
        memberCount = memberCount,
        ownCapabilities = ownCapabilities?.toSet().orEmpty(),
        ownFollows = ownFollows?.map(FollowResponse::toModel).orEmpty(),
        ownMembership = ownMembership?.toModel(),
        name = name,
        pinCount = pinCount,
        updatedAt = updatedAt,
        visibility = visibility,
    )

/**
 * Extension function to update the feed while preserving "own" data because own data from WS events
 * is not reliable.
 */
internal fun FeedData.update(updated: FeedData): FeedData =
    updated.copy(
        ownCapabilities = this.ownCapabilities,
        ownFollows = this.ownFollows,
        ownMembership = this.ownMembership,
    )

internal fun FeedData.ownValues(): FeedOwnValues =
    FeedOwnValues(capabilities = ownCapabilities, follows = ownFollows, membership = ownMembership)

internal fun FeedOwnData.toModel(): FeedOwnValues =
    FeedOwnValues(
        capabilities = ownCapabilities?.toSet().orEmpty(),
        follows = ownFollows?.map(FollowResponse::toModel).orEmpty(),
        membership = ownMembership?.toModel(),
    )
