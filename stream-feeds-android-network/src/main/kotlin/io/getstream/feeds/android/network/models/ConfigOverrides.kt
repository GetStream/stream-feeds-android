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
public data class ConfigOverrides(
    @Json(name = "commands")
    public val commands: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "grants")
    public val grants:
        kotlin.collections.Map<kotlin.String, kotlin.collections.List<kotlin.String>> =
        emptyMap(),
    @Json(name = "blocklist") public val blocklist: kotlin.String? = null,
    @Json(name = "blocklist_behavior") public val blocklistBehavior: BlocklistBehavior? = null,
    @Json(name = "count_messages") public val countMessages: kotlin.Boolean? = null,
    @Json(name = "max_message_length") public val maxMessageLength: kotlin.Int? = null,
    @Json(name = "quotes") public val quotes: kotlin.Boolean? = null,
    @Json(name = "reactions") public val reactions: kotlin.Boolean? = null,
    @Json(name = "replies") public val replies: kotlin.Boolean? = null,
    @Json(name = "shared_locations") public val sharedLocations: kotlin.Boolean? = null,
    @Json(name = "typing_events") public val typingEvents: kotlin.Boolean? = null,
    @Json(name = "uploads") public val uploads: kotlin.Boolean? = null,
    @Json(name = "url_enrichment") public val urlEnrichment: kotlin.Boolean? = null,
    @Json(name = "user_message_reminders") public val userMessageReminders: kotlin.Boolean? = null,
) {

    /** BlocklistBehavior Enum */
    public sealed class BlocklistBehavior(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): BlocklistBehavior =
                when (s) {
                    "block" -> Block
                    "flag" -> Flag
                    else -> Unknown(s)
                }
        }

        public object Block : BlocklistBehavior("block")

        public object Flag : BlocklistBehavior("flag")

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
