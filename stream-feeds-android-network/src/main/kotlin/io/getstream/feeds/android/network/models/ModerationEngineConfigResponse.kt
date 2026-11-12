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
public data class ModerationEngineConfigResponse(
    @Json(name = "model") public val model: String? = null,
    @Json(name = "provider") public val provider: Provider? = null,
    @Json(name = "reasoning_effort") public val reasoningEffort: String? = null,
    @Json(name = "strictness") public val strictness: Strictness? = null,
) {

    /** Provider Enum */
    public sealed class Provider(public val value: String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: String): Provider =
                when (s) {
                    "llm" -> Llm
                    "nlp" -> Nlp
                    "stream" -> Stream
                    else -> Unknown(s)
                }
        }

        public object Llm : Provider("llm")

        public object Nlp : Provider("nlp")

        public object Stream : Provider("stream")

        public data class Unknown(val unknownValue: String) : Provider(unknownValue)

        public class ProviderAdapter : JsonAdapter<Provider>() {
            @FromJson
            override fun fromJson(reader: JsonReader): Provider? {
                val s = reader.nextString() ?: return null
                return Provider.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: Provider?) {
                writer.value(value?.value)
            }
        }
    }

    /** Strictness Enum */
    public sealed class Strictness(public val value: String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: String): Strictness =
                when (s) {
                    "balanced" -> Balanced
                    "relaxed" -> Relaxed
                    "strict" -> Strict
                    else -> Unknown(s)
                }
        }

        public object Balanced : Strictness("balanced")

        public object Relaxed : Strictness("relaxed")

        public object Strict : Strictness("strict")

        public data class Unknown(val unknownValue: String) : Strictness(unknownValue)

        public class StrictnessAdapter : JsonAdapter<Strictness>() {
            @FromJson
            override fun fromJson(reader: JsonReader): Strictness? {
                val s = reader.nextString() ?: return null
                return Strictness.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: Strictness?) {
                writer.value(value?.value)
            }
        }
    }
}
