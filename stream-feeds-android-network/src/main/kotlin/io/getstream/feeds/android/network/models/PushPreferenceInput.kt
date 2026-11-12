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

/**  */
@com.squareup.moshi.JsonClass(generateAdapter = true)
public data class PushPreferenceInput(
    @Json(name = "channel_cid") public val channelCid: String? = null,
    @Json(name = "chat_level") public val chatLevel: ChatLevel? = null,
    @Json(name = "disabled_until") public val disabledUntil: java.util.Date? = null,
    @Json(name = "feeds_level") public val feedsLevel: FeedsLevel? = null,
    @Json(name = "remove_disable") public val removeDisable: Boolean? = null,
    @Json(name = "remove_schedule") public val removeSchedule: Boolean? = null,
    @Json(name = "user_id") public val userId: String? = null,
    @Json(name = "chat_preferences")
    public val chatPreferences: io.getstream.feeds.android.network.models.ChatPreferencesInput? =
        null,
    @Json(name = "feeds_preferences")
    public val feedsPreferences: io.getstream.feeds.android.network.models.FeedsPreferences? = null,
    @Json(name = "schedule")
    public val schedule: io.getstream.feeds.android.network.models.PushScheduleInput? = null,
) {

    /** ChatLevel Enum */
    public sealed class ChatLevel(public val value: String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: String): ChatLevel =
                when (s) {
                    "all" -> All
                    "all_mentions" -> AllMentions
                    "default" -> Default
                    "direct_mentions" -> DirectMentions
                    "mentions" -> Mentions
                    "none" -> None
                    else -> Unknown(s)
                }
        }

        public object All : ChatLevel("all")

        public object AllMentions : ChatLevel("all_mentions")

        public object Default : ChatLevel("default")

        public object DirectMentions : ChatLevel("direct_mentions")

        public object Mentions : ChatLevel("mentions")

        public object None : ChatLevel("none")

        public data class Unknown(val unknownValue: String) : ChatLevel(unknownValue)

        public class ChatLevelAdapter : JsonAdapter<ChatLevel>() {
            @FromJson
            override fun fromJson(reader: JsonReader): ChatLevel? {
                val s = reader.nextString() ?: return null
                return ChatLevel.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: ChatLevel?) {
                writer.value(value?.value)
            }
        }
    }

    /** FeedsLevel Enum */
    public sealed class FeedsLevel(public val value: String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: String): FeedsLevel =
                when (s) {
                    "all" -> All
                    "default" -> Default
                    "none" -> None
                    else -> Unknown(s)
                }
        }

        public object All : FeedsLevel("all")

        public object Default : FeedsLevel("default")

        public object None : FeedsLevel("none")

        public data class Unknown(val unknownValue: String) : FeedsLevel(unknownValue)

        public class FeedsLevelAdapter : JsonAdapter<FeedsLevel>() {
            @FromJson
            override fun fromJson(reader: JsonReader): FeedsLevel? {
                val s = reader.nextString() ?: return null
                return FeedsLevel.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: FeedsLevel?) {
                writer.value(value?.value)
            }
        }
    }
}
