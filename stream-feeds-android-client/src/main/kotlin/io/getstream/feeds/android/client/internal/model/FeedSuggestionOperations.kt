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
import io.getstream.feeds.android.client.api.model.FeedSuggestionData
import io.getstream.feeds.android.network.models.FeedSuggestionResponse
import io.getstream.feeds.android.network.models.FollowResponse

internal fun FeedSuggestionResponse.toModel(): FeedSuggestionData =
    FeedSuggestionData(
        feed =
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
            ),
        algorithmScores = algorithmScores,
        reason = reason,
        recommendationScore = recommendationScore,
    )
