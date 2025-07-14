/*
 * Copyright (c) 2014-2024 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-video-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package io.getstream.feeds.android.core.generated.models

import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.*
import kotlin.io.*
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

/**
 * 
 */

data class QueryCommentsRequest (
    @Json(name = "filter")
    val filter: kotlin.collections.Map<kotlin.String, Any?>,

    @Json(name = "limit")
    val limit: kotlin.Int? = null,

    @Json(name = "next")
    val next: kotlin.String? = null,

    @Json(name = "prev")
    val prev: kotlin.String? = null,

    @Json(name = "sort")
    val sort: Sort? = null
)
{
    
    /**
    * Sort Enum
    */
    sealed class Sort(val value: kotlin.String) {
            override fun toString(): String = value

            companion object {
                fun fromString(s: kotlin.String): Sort = when (s) {
                    "best" -> Best
                    "controversial" -> Controversial
                    "first" -> First
                    "last" -> Last
                    "top" -> Top
                    else -> Unknown(s)
                }
            }
            object Best : Sort("best")
            object Controversial : Sort("controversial")
            object First : Sort("first")
            object Last : Sort("last")
            object Top : Sort("top")
            data class Unknown(val unknownValue: kotlin.String) : Sort(unknownValue)
        

        class SortAdapter : JsonAdapter<Sort>() {
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
