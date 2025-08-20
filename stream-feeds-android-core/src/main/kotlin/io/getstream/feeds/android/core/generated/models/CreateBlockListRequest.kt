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

package io.getstream.feeds.android.core.generated.models

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import kotlin.collections.*
import kotlin.collections.List
import kotlin.io.*

/** Block list contains restricted words */
data class CreateBlockListRequest(
    @Json(name = "name") val name: kotlin.String,
    @Json(name = "words") val words: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "team") val team: kotlin.String? = null,
    @Json(name = "type") val type: Type? = null,
) {

    /** Type Enum */
    sealed class Type(val value: kotlin.String) {
        override fun toString(): String = value

        companion object {
            fun fromString(s: kotlin.String): Type =
                when (s) {
                    "domain" -> Domain
                    "domain_allowlist" -> DomainAllowlist
                    "email" -> Email
                    "regex" -> Regex
                    "word" -> Word
                    else -> Unknown(s)
                }
        }

        object Domain : Type("domain")

        object DomainAllowlist : Type("domain_allowlist")

        object Email : Type("email")

        object Regex : Type("regex")

        object Word : Type("word")

        data class Unknown(val unknownValue: kotlin.String) : Type(unknownValue)

        class TypeAdapter : JsonAdapter<Type>() {
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
