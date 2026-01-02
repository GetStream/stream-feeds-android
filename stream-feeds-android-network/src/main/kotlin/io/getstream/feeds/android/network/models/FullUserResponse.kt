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
import kotlin.collections.*
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.io.*

/**  */
public data class FullUserResponse(
    @Json(name = "banned") public val banned: kotlin.Boolean,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "id") public val id: kotlin.String,
    @Json(name = "invisible") public val invisible: kotlin.Boolean,
    @Json(name = "language") public val language: kotlin.String,
    @Json(name = "online") public val online: kotlin.Boolean,
    @Json(name = "role") public val role: kotlin.String,
    @Json(name = "shadow_banned") public val shadowBanned: kotlin.Boolean,
    @Json(name = "total_unread_count") public val totalUnreadCount: kotlin.Int,
    @Json(name = "unread_channels") public val unreadChannels: kotlin.Int,
    @Json(name = "unread_count") public val unreadCount: kotlin.Int,
    @Json(name = "unread_threads") public val unreadThreads: kotlin.Int,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "blocked_user_ids")
    public val blockedUserIds: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "devices")
    public val devices:
        kotlin.collections.List<io.getstream.feeds.android.network.models.DeviceResponse> =
        emptyList(),
    @Json(name = "mutes")
    public val mutes:
        kotlin.collections.List<io.getstream.feeds.android.network.models.UserMuteResponse> =
        emptyList(),
    @Json(name = "teams") public val teams: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "custom")
    public val custom: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),
    @Json(name = "avg_response_time") public val avgResponseTime: kotlin.Int? = null,
    @Json(name = "ban_expires") public val banExpires: java.util.Date? = null,
    @Json(name = "deactivated_at") public val deactivatedAt: java.util.Date? = null,
    @Json(name = "deleted_at") public val deletedAt: java.util.Date? = null,
    @Json(name = "image") public val image: kotlin.String? = null,
    @Json(name = "last_active") public val lastActive: java.util.Date? = null,
    @Json(name = "name") public val name: kotlin.String? = null,
    @Json(name = "revoke_tokens_issued_before")
    public val revokeTokensIssuedBefore: java.util.Date? = null,
    @Json(name = "latest_hidden_channels")
    public val latestHiddenChannels: kotlin.collections.List<kotlin.String>? = emptyList(),
    @Json(name = "teams_role")
    public val teamsRole: kotlin.collections.Map<kotlin.String, kotlin.String>? = emptyMap(),
)
