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
public data class ChannelMemberResponse(
    @Json(name = "banned") public val banned: Boolean,
    @Json(name = "channel_role") public val channelRole: String,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "notifications_muted") public val notificationsMuted: Boolean,
    @Json(name = "shadow_banned") public val shadowBanned: Boolean,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "user_id") public val userId: String,
    @Json(name = "custom") public val custom: Map<String, Any?> = emptyMap(),
    @Json(name = "archived_at") public val archivedAt: java.util.Date? = null,
    @Json(name = "ban_expires") public val banExpires: java.util.Date? = null,
    @Json(name = "ban_from_future_channels") public val banFromFutureChannels: Boolean? = null,
    @Json(name = "deleted_at") public val deletedAt: java.util.Date? = null,
    @Json(name = "future_channel_ban_expires")
    public val futureChannelBanExpires: java.util.Date? = null,
    @Json(name = "invite_accepted_at") public val inviteAcceptedAt: java.util.Date? = null,
    @Json(name = "invite_rejected_at") public val inviteRejectedAt: java.util.Date? = null,
    @Json(name = "invited") public val invited: Boolean? = null,
    @Json(name = "is_moderator") public val isModerator: Boolean? = null,
    @Json(name = "pinned_at") public val pinnedAt: java.util.Date? = null,
    @Json(name = "role") public val role: String? = null,
    @Json(name = "status") public val status: String? = null,
    @Json(name = "deleted_messages") public val deletedMessages: List<String>? = null,
    @Json(name = "user")
    public val user: io.getstream.feeds.android.network.models.UserResponse? = null,
)
