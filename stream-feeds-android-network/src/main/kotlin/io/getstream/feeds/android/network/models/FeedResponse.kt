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
import kotlin.collections.Map
import kotlin.io.*

/**  */
public data class FeedResponse(
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "description") public val description: kotlin.String,
    @Json(name = "feed") public val feed: kotlin.String,
    @Json(name = "follower_count") public val followerCount: kotlin.Int,
    @Json(name = "following_count") public val followingCount: kotlin.Int,
    @Json(name = "group_id") public val groupId: kotlin.String,
    @Json(name = "id") public val id: kotlin.String,
    @Json(name = "member_count") public val memberCount: kotlin.Int,
    @Json(name = "name") public val name: kotlin.String,
    @Json(name = "pin_count") public val pinCount: kotlin.Int,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "created_by")
    public val createdBy: io.getstream.feeds.android.network.models.UserResponse,
    @Json(name = "deleted_at") public val deletedAt: java.util.Date? = null,
    @Json(name = "visibility") public val visibility: kotlin.String? = null,
    @Json(name = "filter_tags")
    public val filterTags: kotlin.collections.List<kotlin.String>? = emptyList(),
    @Json(name = "own_follows")
    public val ownFollows:
        kotlin.collections.List<io.getstream.feeds.android.network.models.FollowResponse>? =
        emptyList(),
    @Json(name = "custom")
    public val custom: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
)
