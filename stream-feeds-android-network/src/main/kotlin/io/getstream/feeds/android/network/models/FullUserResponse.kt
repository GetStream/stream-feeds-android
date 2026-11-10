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
public data class FullUserResponse(
    @Json(name = "banned") public val banned: Boolean,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "id") public val id: String,
    @Json(name = "invisible") public val invisible: Boolean,
    @Json(name = "language") public val language: String,
    @Json(name = "online") public val online: Boolean,
    @Json(name = "role") public val role: String,
    @Json(name = "shadow_banned") public val shadowBanned: Boolean,
    @Json(name = "total_unread_count") public val totalUnreadCount: Int,
    @Json(name = "unread_channels") public val unreadChannels: Int,
    @Json(name = "unread_count") public val unreadCount: Int,
    @Json(name = "unread_threads") public val unreadThreads: Int,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "blocked_user_ids") public val blockedUserIds: List<String> = emptyList(),
    @Json(name = "channel_mutes")
    public val channelMutes: List<io.getstream.feeds.android.network.models.ChannelMute> =
        emptyList(),
    @Json(name = "devices")
    public val devices: List<io.getstream.feeds.android.network.models.DeviceResponse> =
        emptyList(),
    @Json(name = "mutes")
    public val mutes: List<io.getstream.feeds.android.network.models.UserMuteResponse> =
        emptyList(),
    @Json(name = "teams") public val teams: List<String> = emptyList(),
    @Json(name = "custom") public val custom: Map<String, Any?> = emptyMap(),
    @Json(name = "avg_response_time") public val avgResponseTime: Int? = null,
    @Json(name = "ban_expires") public val banExpires: java.util.Date? = null,
    @Json(name = "deactivated_at") public val deactivatedAt: java.util.Date? = null,
    @Json(name = "deleted_at") public val deletedAt: java.util.Date? = null,
    @Json(name = "image") public val image: String? = null,
    @Json(name = "last_active") public val lastActive: java.util.Date? = null,
    @Json(name = "name") public val name: String? = null,
    @Json(name = "latest_hidden_channels") public val latestHiddenChannels: List<String>? = null,
    @Json(name = "teams_role") public val teamsRole: Map<String, String>? = null,
)
