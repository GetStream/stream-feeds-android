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
public data class CreateQueueRequest(
    @Json(name = "name") public val name: String,
    @Json(name = "type") public val type: Type,
    @Json(name = "description") public val description: String? = null,
    @Json(name = "sort") public val sort: List<Map<String, Any?>>? = null,
    @Json(name = "filters") public val filters: Map<String, Any?>? = null,
) {

    /** Type Enum */
    public sealed class Type(public val value: String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: String): Type =
                when (s) {
                    "operational_queue" -> OperationalQueue
                    "personal_view" -> PersonalView
                    else -> Unknown(s)
                }
        }

        public object OperationalQueue : Type("operational_queue")

        public object PersonalView : Type("personal_view")

        public data class Unknown(val unknownValue: String) : Type(unknownValue)

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
