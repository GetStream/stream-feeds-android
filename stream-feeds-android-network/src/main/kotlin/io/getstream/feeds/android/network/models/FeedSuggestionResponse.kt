/*
 * Copyright (c) 2014-2026 Stream.io Inc. All rights reserved.
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

@file:Suppress("ArrayInDataClass", "EnumEntryName", "RemoveRedundantQualifierName", "UnusedImport")

package io.getstream.feeds.android.network.models

import com.squareup.moshi.Json

/**  */
@com.squareup.moshi.JsonClass(generateAdapter = true)
public data class FeedSuggestionResponse(
    @Json(name = "activity_count") public val activityCount: Int,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "description") public val description: String,
    @Json(name = "feed") public val feed: String,
    @Json(name = "follower_count") public val followerCount: Int,
    @Json(name = "following_count") public val followingCount: Int,
    @Json(name = "group_id") public val groupId: String,
    @Json(name = "id") public val id: String,
    @Json(name = "member_count") public val memberCount: Int,
    @Json(name = "name") public val name: String,
    @Json(name = "pin_count") public val pinCount: Int,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "created_by")
    public val createdBy: io.getstream.feeds.android.network.models.UserResponse,
    @Json(name = "deleted_at") public val deletedAt: java.util.Date? = null,
    @Json(name = "reason") public val reason: String? = null,
    @Json(name = "recommendation_score") public val recommendationScore: Double? = null,
    @Json(name = "visibility")
    public val visibility: io.getstream.feeds.android.network.models.FeedVisibility? = null,
    @Json(name = "filter_tags") public val filterTags: List<String>? = null,
    @Json(name = "own_capabilities")
    public val ownCapabilities: List<io.getstream.feeds.android.network.models.FeedOwnCapability>? =
        null,
    @Json(name = "own_followings")
    public val ownFollowings: List<io.getstream.feeds.android.network.models.FollowResponse>? =
        null,
    @Json(name = "own_follows")
    public val ownFollows: List<io.getstream.feeds.android.network.models.FollowResponse>? = null,
    @Json(name = "algorithm_scores") public val algorithmScores: Map<String, Double>? = null,
    @Json(name = "custom") public val custom: Map<String, Any?>? = null,
    @Json(name = "location")
    public val location: io.getstream.feeds.android.network.models.Location? = null,
    @Json(name = "own_membership")
    public val ownMembership: io.getstream.feeds.android.network.models.FeedMemberResponse? = null,
)
