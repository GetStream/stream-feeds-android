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

/** Represents any chat message */
public data class MessageResponse(
    @Json(name = "cid") public val cid: kotlin.String,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "deleted_reply_count") public val deletedReplyCount: kotlin.Int,
    @Json(name = "html") public val html: kotlin.String,
    @Json(name = "id") public val id: kotlin.String,
    @Json(name = "pinned") public val pinned: kotlin.Boolean,
    @Json(name = "reply_count") public val replyCount: kotlin.Int,
    @Json(name = "shadowed") public val shadowed: kotlin.Boolean,
    @Json(name = "silent") public val silent: kotlin.Boolean,
    @Json(name = "text") public val text: kotlin.String,
    @Json(name = "type") public val type: Type,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "attachments")
    public val attachments:
        kotlin.collections.List<io.getstream.feeds.android.network.models.Attachment> =
        emptyList(),
    @Json(name = "latest_reactions")
    public val latestReactions:
        kotlin.collections.List<io.getstream.feeds.android.network.models.ReactionResponse> =
        emptyList(),
    @Json(name = "mentioned_users")
    public val mentionedUsers:
        kotlin.collections.List<io.getstream.feeds.android.network.models.UserResponse> =
        emptyList(),
    @Json(name = "own_reactions")
    public val ownReactions:
        kotlin.collections.List<io.getstream.feeds.android.network.models.ReactionResponse> =
        emptyList(),
    @Json(name = "restricted_visibility")
    public val restrictedVisibility: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "custom")
    public val custom: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),
    @Json(name = "reaction_counts")
    public val reactionCounts: kotlin.collections.Map<kotlin.String, kotlin.Int> = emptyMap(),
    @Json(name = "reaction_scores")
    public val reactionScores: kotlin.collections.Map<kotlin.String, kotlin.Int> = emptyMap(),
    @Json(name = "user") public val user: io.getstream.feeds.android.network.models.UserResponse,
    @Json(name = "command") public val command: kotlin.String? = null,
    @Json(name = "deleted_at") public val deletedAt: java.util.Date? = null,
    @Json(name = "deleted_for_me") public val deletedForMe: kotlin.Boolean? = null,
    @Json(name = "message_text_updated_at") public val messageTextUpdatedAt: java.util.Date? = null,
    @Json(name = "mml") public val mml: kotlin.String? = null,
    @Json(name = "parent_id") public val parentId: kotlin.String? = null,
    @Json(name = "pin_expires") public val pinExpires: java.util.Date? = null,
    @Json(name = "pinned_at") public val pinnedAt: java.util.Date? = null,
    @Json(name = "poll_id") public val pollId: kotlin.String? = null,
    @Json(name = "quoted_message_id") public val quotedMessageId: kotlin.String? = null,
    @Json(name = "show_in_channel") public val showInChannel: kotlin.Boolean? = null,
    @Json(name = "thread_participants")
    public val threadParticipants:
        kotlin.collections.List<io.getstream.feeds.android.network.models.UserResponse>? =
        emptyList(),
    @Json(name = "draft")
    public val draft: io.getstream.feeds.android.network.models.DraftResponse? = null,
    @Json(name = "i18n")
    public val i18n: kotlin.collections.Map<kotlin.String, kotlin.String>? = emptyMap(),
    @Json(name = "image_labels")
    public val imageLabels:
        kotlin.collections.Map<kotlin.String, kotlin.collections.List<kotlin.String>>? =
        emptyMap(),
    @Json(name = "member")
    public val member: io.getstream.feeds.android.network.models.ChannelMemberResponse? = null,
    @Json(name = "moderation")
    public val moderation: io.getstream.feeds.android.network.models.ModerationV2Response? = null,
    @Json(name = "pinned_by")
    public val pinnedBy: io.getstream.feeds.android.network.models.UserResponse? = null,
    @Json(name = "poll")
    public val poll: io.getstream.feeds.android.network.models.PollResponseData? = null,
    @Json(name = "quoted_message")
    public val quotedMessage: io.getstream.feeds.android.network.models.MessageResponse? = null,
    @Json(name = "reaction_groups")
    public val reactionGroups:
        kotlin.collections.Map<
            kotlin.String,
            io.getstream.feeds.android.network.models.ReactionGroupResponse,
        >? =
        emptyMap(),
    @Json(name = "reminder")
    public val reminder: io.getstream.feeds.android.network.models.ReminderResponseData? = null,
    @Json(name = "shared_location")
    public val sharedLocation:
        io.getstream.feeds.android.network.models.SharedLocationResponseData? =
        null,
) {

    /** Type Enum */
    public sealed class Type(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): Type =
                when (s) {
                    "deleted" -> Deleted
                    "ephemeral" -> Ephemeral
                    "error" -> Error
                    "regular" -> Regular
                    "reply" -> Reply
                    "system" -> System
                    else -> Unknown(s)
                }
        }

        public object Deleted : Type("deleted")

        public object Ephemeral : Type("ephemeral")

        public object Error : Type("error")

        public object Regular : Type("regular")

        public object Reply : Type("reply")

        public object System : Type("system")

        public data class Unknown(val unknownValue: kotlin.String) : Type(unknownValue)

        public class TypeAdapter : JsonAdapter<Type>() {
            @FromJson
            override fun fromJson(reader: JsonReader): Type? {
                val s = reader.nextString() ?: return null
                return Type.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: Type?) {
                writer.value(value?.value)
            }
        }
    }
}
