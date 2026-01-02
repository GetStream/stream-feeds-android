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
public data class EnrichedCollectionResponse(
    @Json(name = "id") public val id: kotlin.String,
    @Json(name = "name") public val name: kotlin.String,
    @Json(name = "status") public val status: Status,
    @Json(name = "created_at") public val createdAt: java.util.Date? = null,
    @Json(name = "updated_at") public val updatedAt: java.util.Date? = null,
    @Json(name = "user_id") public val userId: kotlin.String? = null,
    @Json(name = "custom")
    public val custom: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
) {

    /** Status Enum */
    public sealed class Status(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): Status =
                when (s) {
                    "notfound" -> Notfound
                    "ok" -> Ok
                    else -> Unknown(s)
                }
        }

        public object Notfound : Status("notfound")

        public object Ok : Status("ok")

        public data class Unknown(val unknownValue: kotlin.String) : Status(unknownValue)

        public class StatusAdapter : JsonAdapter<Status>() {
            @FromJson
            override fun fromJson(reader: JsonReader): Status? {
                val s = reader.nextString() ?: return null
                return Status.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: Status?) {
                writer.value(value?.value)
            }
        }
    }
}
