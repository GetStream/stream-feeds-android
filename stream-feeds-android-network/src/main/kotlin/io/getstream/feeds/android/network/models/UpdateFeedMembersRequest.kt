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

/**  */
public data class UpdateFeedMembersRequest(
    @Json(name = "operation") public val operation: Operation,
    @Json(name = "limit") public val limit: kotlin.Int? = null,
    @Json(name = "next") public val next: kotlin.String? = null,
    @Json(name = "prev") public val prev: kotlin.String? = null,
    @Json(name = "members")
    public val members:
        kotlin.collections.List<io.getstream.feeds.android.network.models.FeedMemberRequest>? =
        emptyList(),
) {

    /** Operation Enum */
    public sealed class Operation(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): Operation =
                when (s) {
                    "remove" -> Remove
                    "set" -> Set
                    "upsert" -> Upsert
                    else -> Unknown(s)
                }
        }

        public object Remove : Operation("remove")

        public object Set : Operation("set")

        public object Upsert : Operation("upsert")

        public data class Unknown(val unknownValue: kotlin.String) : Operation(unknownValue)

        public class OperationAdapter : JsonAdapter<Operation>() {
            @FromJson
            override fun fromJson(reader: JsonReader): Operation? {
                val s = reader.nextString() ?: return null
                return Operation.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: Operation?) {
                writer.value(value?.value)
            }
        }
    }
}
