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

/**  */
public data class BanOptions(
    @Json(name = "delete_messages") public val deleteMessages: DeleteMessages? = null,
    @Json(name = "duration") public val duration: kotlin.Int? = null,
    @Json(name = "ip_ban") public val ipBan: kotlin.Boolean? = null,
    @Json(name = "reason") public val reason: kotlin.String? = null,
    @Json(name = "shadow_ban") public val shadowBan: kotlin.Boolean? = null,
) {

    /** DeleteMessages Enum */
    public sealed class DeleteMessages(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): DeleteMessages =
                when (s) {
                    "hard" -> Hard
                    "pruning" -> Pruning
                    "soft" -> Soft
                    else -> Unknown(s)
                }
        }

        public object Hard : DeleteMessages("hard")

        public object Pruning : DeleteMessages("pruning")

        public object Soft : DeleteMessages("soft")

        public data class Unknown(val unknownValue: kotlin.String) : DeleteMessages(unknownValue)

        public class DeleteMessagesAdapter : JsonAdapter<DeleteMessages>() {
            @FromJson
            override fun fromJson(reader: JsonReader): DeleteMessages? {
                val s = reader.nextString() ?: return null
                return DeleteMessages.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: DeleteMessages?) {
                writer.value(value?.value)
            }
        }
    }
}
