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

/** Represents channel in chat */
@com.squareup.moshi.JsonClass(generateAdapter = true)
public data class ChannelResponse(
    @Json(name = "cid") public val cid: String,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "disabled") public val disabled: Boolean,
    @Json(name = "frozen") public val frozen: Boolean,
    @Json(name = "id") public val id: String,
    @Json(name = "type") public val type: String,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "custom") public val custom: Map<String, Any?> = emptyMap(),
    @Json(name = "active_member_count") public val activeMemberCount: Int? = null,
    @Json(name = "auto_translation_enabled") public val autoTranslationEnabled: Boolean? = null,
    @Json(name = "auto_translation_language") public val autoTranslationLanguage: String? = null,
    @Json(name = "blocked") public val blocked: Boolean? = null,
    @Json(name = "cooldown") public val cooldown: Int? = null,
    @Json(name = "deleted_at") public val deletedAt: java.util.Date? = null,
    @Json(name = "hidden") public val hidden: Boolean? = null,
    @Json(name = "hide_messages_before") public val hideMessagesBefore: java.util.Date? = null,
    @Json(name = "last_message_at") public val lastMessageAt: java.util.Date? = null,
    @Json(name = "member_count") public val memberCount: Int? = null,
    @Json(name = "message_count") public val messageCount: Int? = null,
    @Json(name = "mute_expires_at") public val muteExpiresAt: java.util.Date? = null,
    @Json(name = "muted") public val muted: Boolean? = null,
    @Json(name = "team") public val team: String? = null,
    @Json(name = "truncated_at") public val truncatedAt: java.util.Date? = null,
    @Json(name = "filter_tags") public val filterTags: List<String>? = null,
    @Json(name = "members")
    public val members: List<io.getstream.feeds.android.network.models.ChannelMemberResponse>? =
        null,
    @Json(name = "own_capabilities")
    public val ownCapabilities:
        List<io.getstream.feeds.android.network.models.ChannelOwnCapability>? =
        null,
    @Json(name = "config")
    public val config: io.getstream.feeds.android.network.models.ChannelConfigWithInfo? = null,
    @Json(name = "created_by")
    public val createdBy: io.getstream.feeds.android.network.models.UserResponse? = null,
    @Json(name = "fork")
    public val fork: io.getstream.feeds.android.network.models.ChannelForkSummary? = null,
    @Json(name = "truncated_by")
    public val truncatedBy: io.getstream.feeds.android.network.models.UserResponse? = null,
)
