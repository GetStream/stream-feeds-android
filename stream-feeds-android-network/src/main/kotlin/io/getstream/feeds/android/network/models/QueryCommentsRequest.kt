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
import kotlin.collections.Map
import kotlin.io.*

/**  */
public data class QueryCommentsRequest(
    @Json(name = "filter")
    public val filter: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),
    @Json(name = "limit") public val limit: kotlin.Int? = null,
    @Json(name = "next") public val next: kotlin.String? = null,
    @Json(name = "prev") public val prev: kotlin.String? = null,
    @Json(name = "sort") public val sort: Sort? = null,
) {

    /** Sort Enum */
    public sealed class Sort(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): Sort =
                when (s) {
                    "best" -> Best
                    "controversial" -> Controversial
                    "first" -> First
                    "last" -> Last
                    "top" -> Top
                    else -> Unknown(s)
                }
        }

        public object Best : Sort("best")

        public object Controversial : Sort("controversial")

        public object First : Sort("first")

        public object Last : Sort("last")

        public object Top : Sort("top")

        public data class Unknown(val unknownValue: kotlin.String) : Sort(unknownValue)

        public class SortAdapter : JsonAdapter<Sort>() {
            @FromJson
            override fun fromJson(reader: JsonReader): Sort? {
                val s = reader.nextString() ?: return null
                return Sort.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: Sort?) {
                writer.value(value?.value)
            }
        }
    }
}
