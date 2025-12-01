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

/** Block list contains restricted words */
public data class CreateBlockListRequest(
    @Json(name = "name") public val name: kotlin.String,
    @Json(name = "words") public val words: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "is_leet_check_enabled") public val isLeetCheckEnabled: kotlin.Boolean? = null,
    @Json(name = "is_plural_check_enabled") public val isPluralCheckEnabled: kotlin.Boolean? = null,
    @Json(name = "team") public val team: kotlin.String? = null,
    @Json(name = "type") public val type: Type? = null,
) {

    /** Type Enum */
    public sealed class Type(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): Type =
                when (s) {
                    "domain" -> Domain
                    "domain_allowlist" -> DomainAllowlist
                    "email" -> Email
                    "email_allowlist" -> EmailAllowlist
                    "regex" -> Regex
                    "word" -> Word
                    else -> Unknown(s)
                }
        }

        public object Domain : Type("domain")

        public object DomainAllowlist : Type("domain_allowlist")

        public object Email : Type("email")

        public object EmailAllowlist : Type("email_allowlist")

        public object Regex : Type("regex")

        public object Word : Type("word")

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
