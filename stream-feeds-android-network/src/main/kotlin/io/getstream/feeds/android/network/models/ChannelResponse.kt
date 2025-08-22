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

/** Represents channel in chat */
public data class ChannelResponse(
    @Json(name = "cid") public val cid: kotlin.String,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "disabled") public val disabled: kotlin.Boolean,
    @Json(name = "frozen") public val frozen: kotlin.Boolean,
    @Json(name = "id") public val id: kotlin.String,
    @Json(name = "type") public val type: kotlin.String,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "custom")
    public val custom: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),
    @Json(name = "auto_translation_enabled")
    public val autoTranslationEnabled: kotlin.Boolean? = null,
    @Json(name = "auto_translation_language")
    public val autoTranslationLanguage: kotlin.String? = null,
    @Json(name = "blocked") public val blocked: kotlin.Boolean? = null,
    @Json(name = "cooldown") public val cooldown: kotlin.Int? = null,
    @Json(name = "deleted_at") public val deletedAt: java.util.Date? = null,
    @Json(name = "hidden") public val hidden: kotlin.Boolean? = null,
    @Json(name = "hide_messages_before") public val hideMessagesBefore: java.util.Date? = null,
    @Json(name = "last_message_at") public val lastMessageAt: java.util.Date? = null,
    @Json(name = "member_count") public val memberCount: kotlin.Int? = null,
    @Json(name = "message_count") public val messageCount: kotlin.Int? = null,
    @Json(name = "mute_expires_at") public val muteExpiresAt: java.util.Date? = null,
    @Json(name = "muted") public val muted: kotlin.Boolean? = null,
    @Json(name = "team") public val team: kotlin.String? = null,
    @Json(name = "truncated_at") public val truncatedAt: java.util.Date? = null,
    @Json(name = "members")
    public val members:
        kotlin.collections.List<io.getstream.feeds.android.network.models.ChannelMember>? =
        emptyList(),
    @Json(name = "own_capabilities")
    public val ownCapabilities:
        kotlin.collections.List<io.getstream.feeds.android.network.models.ChannelOwnCapability>? =
        emptyList(),
    @Json(name = "config")
    public val config: io.getstream.feeds.android.network.models.ChannelConfigWithInfo? = null,
    @Json(name = "created_by")
    public val createdBy: io.getstream.feeds.android.network.models.UserResponse? = null,
    @Json(name = "truncated_by")
    public val truncatedBy: io.getstream.feeds.android.network.models.UserResponse? = null,
)
