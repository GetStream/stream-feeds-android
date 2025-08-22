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

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import kotlin.collections.*
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.io.*

/**  */
public data class ChannelMember(
    @Json(name = "banned") public val banned: kotlin.Boolean,
    @Json(name = "channel_role") public val channelRole: kotlin.String,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "notifications_muted") public val notificationsMuted: kotlin.Boolean,
    @Json(name = "shadow_banned") public val shadowBanned: kotlin.Boolean,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "custom")
    public val custom: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),
    @Json(name = "archived_at") public val archivedAt: java.util.Date? = null,
    @Json(name = "ban_expires") public val banExpires: java.util.Date? = null,
    @Json(name = "deleted_at") public val deletedAt: java.util.Date? = null,
    @Json(name = "invite_accepted_at") public val inviteAcceptedAt: java.util.Date? = null,
    @Json(name = "invite_rejected_at") public val inviteRejectedAt: java.util.Date? = null,
    @Json(name = "invited") public val invited: kotlin.Boolean? = null,
    @Json(name = "is_moderator") public val isModerator: kotlin.Boolean? = null,
    @Json(name = "pinned_at") public val pinnedAt: java.util.Date? = null,
    @Json(name = "role") public val role: Role? = null,
    @Json(name = "status") public val status: kotlin.String? = null,
    @Json(name = "user_id") public val userId: kotlin.String? = null,
    @Json(name = "deleted_messages")
    public val deletedMessages: kotlin.collections.List<kotlin.String>? = emptyList(),
    @Json(name = "user")
    public val user: io.getstream.feeds.android.network.models.UserResponse? = null,
) {

    /** Role Enum */
    public sealed class Role(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): Role =
                when (s) {
                    "admin" -> Admin
                    "member" -> Member
                    "moderator" -> Moderator
                    "owner" -> Owner
                    else -> Unknown(s)
                }
        }

        public object Admin : Role("admin")

        public object Member : Role("member")

        public object Moderator : Role("moderator")

        public object Owner : Role("owner")

        public data class Unknown(val unknownValue: kotlin.String) : Role(unknownValue)

        public class RoleAdapter : JsonAdapter<Role>() {
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
