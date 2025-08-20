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

package io.getstream.feeds.android.core.generated.models

import com.squareup.moshi.Json
import kotlin.collections.*
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.io.*

/**  */
data class Channel(
    @Json(name = "auto_translation_language") val autoTranslationLanguage: kotlin.String,
    @Json(name = "cid") val cid: kotlin.String,
    @Json(name = "created_at") val createdAt: org.threeten.bp.OffsetDateTime,
    @Json(name = "disabled") val disabled: kotlin.Boolean,
    @Json(name = "frozen") val frozen: kotlin.Boolean,
    @Json(name = "id") val id: kotlin.String,
    @Json(name = "type") val type: kotlin.String,
    @Json(name = "updated_at") val updatedAt: org.threeten.bp.OffsetDateTime,
    @Json(name = "custom") val custom: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),
    @Json(name = "auto_translation_enabled") val autoTranslationEnabled: kotlin.Boolean? = null,
    @Json(name = "cooldown") val cooldown: kotlin.Int? = null,
    @Json(name = "deleted_at") val deletedAt: org.threeten.bp.OffsetDateTime? = null,
    @Json(name = "last_campaigns") val lastCampaigns: kotlin.String? = null,
    @Json(name = "last_message_at") val lastMessageAt: org.threeten.bp.OffsetDateTime? = null,
    @Json(name = "member_count") val memberCount: kotlin.Int? = null,
    @Json(name = "team") val team: kotlin.String? = null,
    @Json(name = "active_live_locations")
    val activeLiveLocations:
        kotlin.collections.List<io.getstream.feeds.android.core.generated.models.SharedLocation>? =
        emptyList(),
    @Json(name = "invites")
    val invites:
        kotlin.collections.List<io.getstream.feeds.android.core.generated.models.ChannelMember>? =
        emptyList(),
    @Json(name = "members")
    val members:
        kotlin.collections.List<io.getstream.feeds.android.core.generated.models.ChannelMember>? =
        emptyList(),
    @Json(name = "config")
    val config: io.getstream.feeds.android.core.generated.models.ChannelConfig? = null,
    @Json(name = "config_overrides")
    val configOverrides: io.getstream.feeds.android.core.generated.models.ConfigOverrides? = null,
    @Json(name = "created_by")
    val createdBy: io.getstream.feeds.android.core.generated.models.User? = null,
    @Json(name = "truncated_by")
    val truncatedBy: io.getstream.feeds.android.core.generated.models.User? = null,
)
