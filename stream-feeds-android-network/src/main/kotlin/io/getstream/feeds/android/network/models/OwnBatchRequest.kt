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

/**
 * Request to get own_follows, own_capabilities, and/or own_membership for multiple feeds. If fields
 * is not specified, all three fields are returned.
 */
public data class OwnBatchRequest(
    @Json(name = "feeds") public val feeds: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "fields") public val fields: Fields? = null,
) {

    /** Fields Enum */
    public sealed class Fields(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): Fields =
                when (s) {
                    "own_capabilities" -> OwnCapabilities
                    "own_follows" -> OwnFollows
                    "own_membership" -> OwnMembership
                    else -> Unknown(s)
                }
        }

        public object OwnCapabilities : Fields("own_capabilities")

        public object OwnFollows : Fields("own_follows")

        public object OwnMembership : Fields("own_membership")

        public data class Unknown(val unknownValue: kotlin.String) : Fields(unknownValue)

        public class FieldsAdapter : JsonAdapter<Fields>() {
            @FromJson
            override fun fromJson(reader: JsonReader): Fields? {
                val s = reader.nextString() ?: return null
                return Fields.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: Fields?) {
                writer.value(value?.value)
            }
        }
    }
}
