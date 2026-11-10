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
public data class FollowResponse(
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "follower_role") public val followerRole: String,
    @Json(name = "push_preference")
    public val pushPreference: io.getstream.feeds.android.network.models.FollowPushPreference,
    @Json(name = "status")
    public val status: io.getstream.feeds.android.network.models.FollowStatus,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "source_feed")
    public val sourceFeed: io.getstream.feeds.android.network.models.FeedResponse,
    @Json(name = "target_feed")
    public val targetFeed: io.getstream.feeds.android.network.models.FeedResponse,
    @Json(name = "request_accepted_at") public val requestAcceptedAt: java.util.Date? = null,
    @Json(name = "request_rejected_at") public val requestRejectedAt: java.util.Date? = null,
    @Json(name = "custom") public val custom: Map<String, Any?>? = null,
)
