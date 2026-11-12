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
public data class GetOrCreateFeedResponse(
    @Json(name = "created") public val created: Boolean,
    @Json(name = "duration") public val duration: String,
    @Json(name = "activities")
    public val activities: List<io.getstream.feeds.android.network.models.ActivityResponse> =
        emptyList(),
    @Json(name = "aggregated_activities")
    public val aggregatedActivities:
        List<io.getstream.feeds.android.network.models.AggregatedActivityResponse> =
        emptyList(),
    @Json(name = "followers")
    public val followers: List<io.getstream.feeds.android.network.models.FollowResponse> =
        emptyList(),
    @Json(name = "following")
    public val following: List<io.getstream.feeds.android.network.models.FollowResponse> =
        emptyList(),
    @Json(name = "members")
    public val members: List<io.getstream.feeds.android.network.models.FeedMemberResponse> =
        emptyList(),
    @Json(name = "pinned_activities")
    public val pinnedActivities:
        List<io.getstream.feeds.android.network.models.ActivityPinResponse> =
        emptyList(),
    @Json(name = "feed") public val feed: io.getstream.feeds.android.network.models.FeedResponse,
    @Json(name = "next") public val next: String? = null,
    @Json(name = "prev") public val prev: String? = null,
    @Json(name = "request_id") public val requestId: String? = null,
    @Json(name = "degraded_selectors") public val degradedSelectors: List<String>? = null,
    @Json(name = "followers_pagination")
    public val followersPagination: io.getstream.feeds.android.network.models.PagerResponse? = null,
    @Json(name = "following_pagination")
    public val followingPagination: io.getstream.feeds.android.network.models.PagerResponse? = null,
    @Json(name = "member_pagination")
    public val memberPagination: io.getstream.feeds.android.network.models.PagerResponse? = null,
    @Json(name = "notification_status")
    public val notificationStatus:
        io.getstream.feeds.android.network.models.NotificationStatusResponse? =
        null,
)
