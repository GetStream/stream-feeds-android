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
import kotlin.collections.Map
import kotlin.io.*

/**  */
public data class FeedsPreferences(
    @Json(name = "comment") public val comment: Comment? = null,
    @Json(name = "comment_reaction") public val commentReaction: CommentReaction? = null,
    @Json(name = "comment_reply") public val commentReply: CommentReply? = null,
    @Json(name = "follow") public val follow: Follow? = null,
    @Json(name = "mention") public val mention: Mention? = null,
    @Json(name = "reaction") public val reaction: Reaction? = null,
    @Json(name = "custom_activity_types")
    public val customActivityTypes: kotlin.collections.Map<kotlin.String, kotlin.String>? =
        emptyMap(),
) {

    /** Comment Enum */
    public sealed class Comment(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): Comment =
                when (s) {
                    "all" -> All
                    "none" -> None
                    else -> Unknown(s)
                }
        }

        public object All : Comment("all")

        public object None : Comment("none")

        public data class Unknown(val unknownValue: kotlin.String) : Comment(unknownValue)

        public class CommentAdapter : JsonAdapter<Comment>() {
            @FromJson
            override fun fromJson(reader: JsonReader): Comment? {
                val s = reader.nextString() ?: return null
                return Comment.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: Comment?) {
                writer.value(value?.value)
            }
        }
    }

    /** CommentReaction Enum */
    public sealed class CommentReaction(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): CommentReaction =
                when (s) {
                    "all" -> All
                    "none" -> None
                    else -> Unknown(s)
                }
        }

        public object All : CommentReaction("all")

        public object None : CommentReaction("none")

        public data class Unknown(val unknownValue: kotlin.String) : CommentReaction(unknownValue)

        public class CommentReactionAdapter : JsonAdapter<CommentReaction>() {
            @FromJson
            override fun fromJson(reader: JsonReader): CommentReaction? {
                val s = reader.nextString() ?: return null
                return CommentReaction.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: CommentReaction?) {
                writer.value(value?.value)
            }
        }
    }

    /** CommentReply Enum */
    public sealed class CommentReply(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): CommentReply =
                when (s) {
                    "all" -> All
                    "none" -> None
                    else -> Unknown(s)
                }
        }

        public object All : CommentReply("all")

        public object None : CommentReply("none")

        public data class Unknown(val unknownValue: kotlin.String) : CommentReply(unknownValue)

        public class CommentReplyAdapter : JsonAdapter<CommentReply>() {
            @FromJson
            override fun fromJson(reader: JsonReader): CommentReply? {
                val s = reader.nextString() ?: return null
                return CommentReply.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: CommentReply?) {
                writer.value(value?.value)
            }
        }
    }

    /** Follow Enum */
    public sealed class Follow(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): Follow =
                when (s) {
                    "all" -> All
                    "none" -> None
                    else -> Unknown(s)
                }
        }

        public object All : Follow("all")

        public object None : Follow("none")

        public data class Unknown(val unknownValue: kotlin.String) : Follow(unknownValue)

        public class FollowAdapter : JsonAdapter<Follow>() {
            @FromJson
            override fun fromJson(reader: JsonReader): Follow? {
                val s = reader.nextString() ?: return null
                return Follow.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: Follow?) {
                writer.value(value?.value)
            }
        }
    }

    /** Mention Enum */
    public sealed class Mention(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): Mention =
                when (s) {
                    "all" -> All
                    "none" -> None
                    else -> Unknown(s)
                }
        }

        public object All : Mention("all")

        public object None : Mention("none")

        public data class Unknown(val unknownValue: kotlin.String) : Mention(unknownValue)

        public class MentionAdapter : JsonAdapter<Mention>() {
            @FromJson
            override fun fromJson(reader: JsonReader): Mention? {
                val s = reader.nextString() ?: return null
                return Mention.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: Mention?) {
                writer.value(value?.value)
            }
        }
    }

    /** Reaction Enum */
    public sealed class Reaction(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): Reaction =
                when (s) {
                    "all" -> All
                    "none" -> None
                    else -> Unknown(s)
                }
        }

        public object All : Reaction("all")

        public object None : Reaction("none")

        public data class Unknown(val unknownValue: kotlin.String) : Reaction(unknownValue)

        public class ReactionAdapter : JsonAdapter<Reaction>() {
            @FromJson
            override fun fromJson(reader: JsonReader): Reaction? {
                val s = reader.nextString() ?: return null
                return Reaction.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: Reaction?) {
                writer.value(value?.value)
            }
        }
    }
}
