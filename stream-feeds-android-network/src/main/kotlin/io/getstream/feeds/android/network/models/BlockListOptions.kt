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
import kotlin.io.*

/**  */
public data class BlockListOptions(
    @Json(name = "behavior") public val behavior: Behavior,
    @Json(name = "blocklist") public val blocklist: kotlin.String,
) {

    /** Behavior Enum */
    public sealed class Behavior(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): Behavior =
                when (s) {
                    "block" -> Block
                    "flag" -> Flag
                    "shadow_block" -> ShadowBlock
                    else -> Unknown(s)
                }
        }

        public object Block : Behavior("block")

        public object Flag : Behavior("flag")

        public object ShadowBlock : Behavior("shadow_block")

        public data class Unknown(val unknownValue: kotlin.String) : Behavior(unknownValue)

        public class BehaviorAdapter : JsonAdapter<Behavior>() {
            @FromJson
            override fun fromJson(reader: JsonReader): Behavior? {
                val s = reader.nextString() ?: return null
                return Behavior.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: Behavior?) {
                writer.value(value?.value)
            }
        }
    }
}
