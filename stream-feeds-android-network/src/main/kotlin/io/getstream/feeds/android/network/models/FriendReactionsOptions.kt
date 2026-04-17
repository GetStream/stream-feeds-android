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
import kotlin.io.*

/**
 * Options to control fetching reactions from friends (users you follow or have mutual follows
 * with).
 */
public data class FriendReactionsOptions(
    @Json(name = "enabled") public val enabled: kotlin.Boolean? = null,
    @Json(name = "limit") public val limit: kotlin.Int? = null,
    @Json(name = "type") public val type: Type? = null,
) {

    /** Type Enum */
    public sealed class Type(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): Type =
                when (s) {
                    "following" -> Following
                    "mutual" -> Mutual
                    else -> Unknown(s)
                }
        }

        public object Following : Type("following")

        public object Mutual : Type("mutual")

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
