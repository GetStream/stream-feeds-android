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
import kotlin.io.*

/**  */
public data class ChannelConfig(
    @Json(name = "automod") public val automod: Automod,
    @Json(name = "automod_behavior") public val automodBehavior: AutomodBehavior,
    @Json(name = "connect_events") public val connectEvents: kotlin.Boolean,
    @Json(name = "count_messages") public val countMessages: kotlin.Boolean,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "custom_events") public val customEvents: kotlin.Boolean,
    @Json(name = "mark_messages_pending") public val markMessagesPending: kotlin.Boolean,
    @Json(name = "max_message_length") public val maxMessageLength: kotlin.Int,
    @Json(name = "mutes") public val mutes: kotlin.Boolean,
    @Json(name = "name") public val name: kotlin.String,
    @Json(name = "polls") public val polls: kotlin.Boolean,
    @Json(name = "push_notifications") public val pushNotifications: kotlin.Boolean,
    @Json(name = "quotes") public val quotes: kotlin.Boolean,
    @Json(name = "reactions") public val reactions: kotlin.Boolean,
    @Json(name = "read_events") public val readEvents: kotlin.Boolean,
    @Json(name = "reminders") public val reminders: kotlin.Boolean,
    @Json(name = "replies") public val replies: kotlin.Boolean,
    @Json(name = "search") public val search: kotlin.Boolean,
    @Json(name = "shared_locations") public val sharedLocations: kotlin.Boolean,
    @Json(name = "skip_last_msg_update_for_system_msgs")
    public val skipLastMsgUpdateForSystemMsgs: kotlin.Boolean,
    @Json(name = "typing_events") public val typingEvents: kotlin.Boolean,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "uploads") public val uploads: kotlin.Boolean,
    @Json(name = "url_enrichment") public val urlEnrichment: kotlin.Boolean,
    @Json(name = "user_message_reminders") public val userMessageReminders: kotlin.Boolean,
    @Json(name = "commands")
    public val commands: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "blocklist") public val blocklist: kotlin.String? = null,
    @Json(name = "blocklist_behavior") public val blocklistBehavior: BlocklistBehavior? = null,
    @Json(name = "partition_size") public val partitionSize: kotlin.Int? = null,
    @Json(name = "partition_ttl") public val partitionTtl: kotlin.Int? = null,
    @Json(name = "allowed_flag_reasons")
    public val allowedFlagReasons: kotlin.collections.List<kotlin.String>? = emptyList(),
    @Json(name = "blocklists")
    public val blocklists:
        kotlin.collections.List<io.getstream.feeds.android.network.models.BlockListOptions>? =
        emptyList(),
    @Json(name = "automod_thresholds")
    public val automodThresholds: io.getstream.feeds.android.network.models.Thresholds? = null,
) {

    /** Automod Enum */
    public sealed class Automod(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): Automod =
                when (s) {
                    "AI" -> AI
                    "disabled" -> Disabled
                    "simple" -> Simple
                    else -> Unknown(s)
                }
        }

        public object AI : Automod("AI")

        public object Disabled : Automod("disabled")

        public object Simple : Automod("simple")

        public data class Unknown(val unknownValue: kotlin.String) : Automod(unknownValue)

        public class AutomodAdapter : JsonAdapter<Automod>() {
            @FromJson
            override fun fromJson(reader: JsonReader): Automod? {
                val s = reader.nextString() ?: return null
                return Automod.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: Automod?) {
                writer.value(value?.value)
            }
        }
    }

    /** AutomodBehavior Enum */
    public sealed class AutomodBehavior(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): AutomodBehavior =
                when (s) {
                    "block" -> Block
                    "flag" -> Flag
                    "shadow_block" -> ShadowBlock
                    else -> Unknown(s)
                }
        }

        public object Block : AutomodBehavior("block")

        public object Flag : AutomodBehavior("flag")

        public object ShadowBlock : AutomodBehavior("shadow_block")

        public data class Unknown(val unknownValue: kotlin.String) : AutomodBehavior(unknownValue)

        public class AutomodBehaviorAdapter : JsonAdapter<AutomodBehavior>() {
            @FromJson
            override fun fromJson(reader: JsonReader): AutomodBehavior? {
                val s = reader.nextString() ?: return null
                return AutomodBehavior.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: AutomodBehavior?) {
                writer.value(value?.value)
            }
        }
    }

    /** BlocklistBehavior Enum */
    public sealed class BlocklistBehavior(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): BlocklistBehavior =
                when (s) {
                    "block" -> Block
                    "flag" -> Flag
                    "shadow_block" -> ShadowBlock
                    else -> Unknown(s)
                }
        }

        public object Block : BlocklistBehavior("block")

        public object Flag : BlocklistBehavior("flag")

        public object ShadowBlock : BlocklistBehavior("shadow_block")

        public data class Unknown(val unknownValue: kotlin.String) :
            BlocklistBehavior(unknownValue)

        public class BlocklistBehaviorAdapter : JsonAdapter<BlocklistBehavior>() {
            @FromJson
            override fun fromJson(reader: JsonReader): BlocklistBehavior? {
                val s = reader.nextString() ?: return null
                return BlocklistBehavior.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: BlocklistBehavior?) {
                writer.value(value?.value)
            }
        }
    }
}
