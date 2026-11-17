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

/** [Which follow relationship friend reactions are drawn from.] */
/** FriendReactionType Enum */
public sealed class FriendReactionType(public val value: String) {
    override fun toString(): String = value

    public companion object {
        public fun fromString(s: String): FriendReactionType =
            when (s) {
                "following" -> Following
                "mutual" -> Mutual
                else -> Unknown(s)
            }
    }

    public object Following : FriendReactionType("following")

    public object Mutual : FriendReactionType("mutual")

    public data class Unknown(val unknownValue: String) : FriendReactionType(unknownValue)

    public class FriendReactionTypeAdapter : JsonAdapter<FriendReactionType>() {
        @FromJson
        override fun fromJson(reader: JsonReader): FriendReactionType? {
            val s = reader.nextString() ?: return null
            return FriendReactionType.fromString(s)
        }

        @ToJson
        override fun toJson(writer: JsonWriter, value: FriendReactionType?) {
            writer.value(value?.value)
        }
    }
}
