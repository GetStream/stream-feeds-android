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
@file:Suppress("ArrayInDataClass", "EnumEntryName", "RemoveRedundantQualifierName", "UnusedImport")

package io.getstream.feeds.android.network.models

import com.squareup.moshi.Json
import kotlin.collections.*
import kotlin.collections.List
import kotlin.io.*

/** Basic response information */
public data class GetOrCreateFeedResponse(
    @Json(name = "created") public val created: kotlin.Boolean,
    @Json(name = "duration") public val duration: kotlin.String,
    @Json(name = "activities")
    public val activities:
        kotlin.collections.List<io.getstream.feeds.android.network.models.ActivityResponse> =
        emptyList(),
    @Json(name = "aggregated_activities")
    public val aggregatedActivities:
        kotlin.collections.List<
            io.getstream.feeds.android.network.models.AggregatedActivityResponse
        > =
        emptyList(),
    @Json(name = "followers")
    public val followers:
        kotlin.collections.List<io.getstream.feeds.android.network.models.FollowResponse> =
        emptyList(),
    @Json(name = "following")
    public val following:
        kotlin.collections.List<io.getstream.feeds.android.network.models.FollowResponse> =
        emptyList(),
    @Json(name = "members")
    public val members:
        kotlin.collections.List<io.getstream.feeds.android.network.models.FeedMemberResponse> =
        emptyList(),
    @Json(name = "own_capabilities")
    public val ownCapabilities:
        kotlin.collections.List<io.getstream.feeds.android.network.models.FeedOwnCapability> =
        emptyList(),
    @Json(name = "pinned_activities")
    public val pinnedActivities:
        kotlin.collections.List<io.getstream.feeds.android.network.models.ActivityPinResponse> =
        emptyList(),
    @Json(name = "feed") public val feed: io.getstream.feeds.android.network.models.FeedResponse,
    @Json(name = "next") public val next: kotlin.String? = null,
    @Json(name = "prev") public val prev: kotlin.String? = null,
    @Json(name = "own_follows")
    public val ownFollows:
        kotlin.collections.List<io.getstream.feeds.android.network.models.FollowResponse>? =
        emptyList(),
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
    @Json(name = "own_membership")
    public val ownMembership: io.getstream.feeds.android.network.models.FeedMemberResponse? = null,
)
