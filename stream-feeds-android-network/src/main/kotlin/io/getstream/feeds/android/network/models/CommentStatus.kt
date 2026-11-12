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
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

/** [Moderation status of a comment.] */
/** CommentStatus Enum */
public sealed class CommentStatus(public val value: String) {
    override fun toString(): String = value

    public companion object {
        public fun fromString(s: String): CommentStatus =
            when (s) {
                "active" -> Active
                "deleted" -> Deleted
                "hidden" -> Hidden
                "removed" -> Removed
                "shadow_blocked" -> ShadowBlocked
                else -> Unknown(s)
            }
    }

    public object Active : CommentStatus("active")

    public object Deleted : CommentStatus("deleted")

    public object Hidden : CommentStatus("hidden")

    public object Removed : CommentStatus("removed")

    public object ShadowBlocked : CommentStatus("shadow_blocked")

    public data class Unknown(val unknownValue: String) : CommentStatus(unknownValue)

    public class CommentStatusAdapter : JsonAdapter<CommentStatus>() {
        @FromJson
        override fun fromJson(reader: JsonReader): CommentStatus? {
            val s = reader.nextString() ?: return null
            return CommentStatus.fromString(s)
        }

        @ToJson
        override fun toJson(writer: JsonWriter, value: CommentStatus?) {
            writer.value(value?.value)
        }
    }
}
