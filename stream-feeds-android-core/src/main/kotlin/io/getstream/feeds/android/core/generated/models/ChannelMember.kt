/*
 * Copyright (c) 2014-2024 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-video-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package io.getstream.feeds.android.core.generated.models

import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.*
import kotlin.io.*
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

/**
 * 
 */

data class ChannelMember (
    @Json(name = "banned")
    val banned: kotlin.Boolean,

    @Json(name = "channel_role")
    val channelRole: kotlin.String,

    @Json(name = "created_at")
    val createdAt: org.threeten.bp.OffsetDateTime,

    @Json(name = "notifications_muted")
    val notificationsMuted: kotlin.Boolean,

    @Json(name = "shadow_banned")
    val shadowBanned: kotlin.Boolean,

    @Json(name = "updated_at")
    val updatedAt: org.threeten.bp.OffsetDateTime,

    @Json(name = "custom")
    val custom: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),

    @Json(name = "archived_at")
    val archivedAt: org.threeten.bp.OffsetDateTime? = null,

    @Json(name = "ban_expires")
    val banExpires: org.threeten.bp.OffsetDateTime? = null,

    @Json(name = "deleted_at")
    val deletedAt: org.threeten.bp.OffsetDateTime? = null,

    @Json(name = "invite_accepted_at")
    val inviteAcceptedAt: org.threeten.bp.OffsetDateTime? = null,

    @Json(name = "invite_rejected_at")
    val inviteRejectedAt: org.threeten.bp.OffsetDateTime? = null,

    @Json(name = "invited")
    val invited: kotlin.Boolean? = null,

    @Json(name = "is_moderator")
    val isModerator: kotlin.Boolean? = null,

    @Json(name = "pinned_at")
    val pinnedAt: org.threeten.bp.OffsetDateTime? = null,

    @Json(name = "role")
    val role: Role? = null,

    @Json(name = "status")
    val status: kotlin.String? = null,

    @Json(name = "user_id")
    val userId: kotlin.String? = null,

    @Json(name = "user")
    val user: io.getstream.feeds.android.core.generated.models.UserResponse? = null
)
{
    
    /**
    * Role Enum
    */
    sealed class Role(val value: kotlin.String) {
            override fun toString(): String = value

            companion object {
                fun fromString(s: kotlin.String): Role = when (s) {
                    "admin" -> Admin
                    "member" -> Member
                    "moderator" -> Moderator
                    "owner" -> Owner
                    else -> Unknown(s)
                }
            }
            object Admin : Role("admin")
            object Member : Role("member")
            object Moderator : Role("moderator")
            object Owner : Role("owner")
            data class Unknown(val unknownValue: kotlin.String) : Role(unknownValue)
        

        class RoleAdapter : JsonAdapter<Role>() {
            @FromJson
            override fun fromJson(reader: JsonReader): Role? {
                val s = reader.nextString() ?: return null
                return Role.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: Role?) {
                writer.value(value?.value)
            }
        }
    }    
}
