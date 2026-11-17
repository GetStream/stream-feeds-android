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
public data class ChatPreferencesInput(
    @Json(name = "channel_mentions") public val channelMentions: ChannelMentions? = null,
    @Json(name = "default_preference") public val defaultPreference: DefaultPreference? = null,
    @Json(name = "direct_mentions") public val directMentions: DirectMentions? = null,
    @Json(name = "group_mentions") public val groupMentions: GroupMentions? = null,
    @Json(name = "here_mentions") public val hereMentions: HereMentions? = null,
    @Json(name = "role_mentions") public val roleMentions: RoleMentions? = null,
    @Json(name = "thread_replies") public val threadReplies: ThreadReplies? = null,
) {

    /** ChannelMentions Enum */
    public sealed class ChannelMentions(public val value: String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: String): ChannelMentions =
                when (s) {
                    "all" -> All
                    "none" -> None
                    else -> Unknown(s)
                }
        }

        public object All : ChannelMentions("all")

        public object None : ChannelMentions("none")

        public data class Unknown(val unknownValue: String) : ChannelMentions(unknownValue)

        public class ChannelMentionsAdapter : JsonAdapter<ChannelMentions>() {
            @FromJson
            override fun fromJson(reader: JsonReader): ChannelMentions? {
                val s = reader.nextString() ?: return null
                return ChannelMentions.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: ChannelMentions?) {
                writer.value(value?.value)
            }
        }
    }

    /** DefaultPreference Enum */
    public sealed class DefaultPreference(public val value: String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: String): DefaultPreference =
                when (s) {
                    "all" -> All
                    "none" -> None
                    else -> Unknown(s)
                }
        }

        public object All : DefaultPreference("all")

        public object None : DefaultPreference("none")

        public data class Unknown(val unknownValue: String) : DefaultPreference(unknownValue)

        public class DefaultPreferenceAdapter : JsonAdapter<DefaultPreference>() {
            @FromJson
            override fun fromJson(reader: JsonReader): DefaultPreference? {
                val s = reader.nextString() ?: return null
                return DefaultPreference.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: DefaultPreference?) {
                writer.value(value?.value)
            }
        }
    }

    /** DirectMentions Enum */
    public sealed class DirectMentions(public val value: String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: String): DirectMentions =
                when (s) {
                    "all" -> All
                    "none" -> None
                    else -> Unknown(s)
                }
        }

        public object All : DirectMentions("all")

        public object None : DirectMentions("none")

        public data class Unknown(val unknownValue: String) : DirectMentions(unknownValue)

        public class DirectMentionsAdapter : JsonAdapter<DirectMentions>() {
            @FromJson
            override fun fromJson(reader: JsonReader): DirectMentions? {
                val s = reader.nextString() ?: return null
                return DirectMentions.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: DirectMentions?) {
                writer.value(value?.value)
            }
        }
    }

    /** GroupMentions Enum */
    public sealed class GroupMentions(public val value: String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: String): GroupMentions =
                when (s) {
                    "all" -> All
                    "none" -> None
                    else -> Unknown(s)
                }
        }

        public object All : GroupMentions("all")

        public object None : GroupMentions("none")

        public data class Unknown(val unknownValue: String) : GroupMentions(unknownValue)

        public class GroupMentionsAdapter : JsonAdapter<GroupMentions>() {
            @FromJson
            override fun fromJson(reader: JsonReader): GroupMentions? {
                val s = reader.nextString() ?: return null
                return GroupMentions.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: GroupMentions?) {
                writer.value(value?.value)
            }
        }
    }

    /** HereMentions Enum */
    public sealed class HereMentions(public val value: String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: String): HereMentions =
                when (s) {
                    "all" -> All
                    "none" -> None
                    else -> Unknown(s)
                }
        }

        public object All : HereMentions("all")

        public object None : HereMentions("none")

        public data class Unknown(val unknownValue: String) : HereMentions(unknownValue)

        public class HereMentionsAdapter : JsonAdapter<HereMentions>() {
            @FromJson
            override fun fromJson(reader: JsonReader): HereMentions? {
                val s = reader.nextString() ?: return null
                return HereMentions.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: HereMentions?) {
                writer.value(value?.value)
            }
        }
    }

    /** RoleMentions Enum */
    public sealed class RoleMentions(public val value: String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: String): RoleMentions =
                when (s) {
                    "all" -> All
                    "none" -> None
                    else -> Unknown(s)
                }
        }

        public object All : RoleMentions("all")

        public object None : RoleMentions("none")

        public data class Unknown(val unknownValue: String) : RoleMentions(unknownValue)

        public class RoleMentionsAdapter : JsonAdapter<RoleMentions>() {
            @FromJson
            override fun fromJson(reader: JsonReader): RoleMentions? {
                val s = reader.nextString() ?: return null
                return RoleMentions.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: RoleMentions?) {
                writer.value(value?.value)
            }
        }
    }

    /** ThreadReplies Enum */
    public sealed class ThreadReplies(public val value: String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: String): ThreadReplies =
                when (s) {
                    "all" -> All
                    "none" -> None
                    else -> Unknown(s)
                }
        }

        public object All : ThreadReplies("all")

        public object None : ThreadReplies("none")

        public data class Unknown(val unknownValue: String) : ThreadReplies(unknownValue)

        public class ThreadRepliesAdapter : JsonAdapter<ThreadReplies>() {
            @FromJson
            override fun fromJson(reader: JsonReader): ThreadReplies? {
                val s = reader.nextString() ?: return null
                return ThreadReplies.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: ThreadReplies?) {
                writer.value(value?.value)
            }
        }
    }
}
