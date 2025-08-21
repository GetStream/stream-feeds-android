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
import kotlin.collections.Map
import kotlin.io.*

/**  */
public data class FeedInput(
    @Json(name = "description") public val description: kotlin.String? = null,
    @Json(name = "name") public val name: kotlin.String? = null,
    @Json(name = "visibility") public val visibility: Visibility? = null,
    @Json(name = "filter_tags")
    public val filterTags: kotlin.collections.List<kotlin.String>? = emptyList(),
    @Json(name = "members")
    public val members:
        kotlin.collections.List<io.getstream.feeds.android.network.models.FeedMemberRequest>? =
        emptyList(),
    @Json(name = "custom")
    public val custom: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
) {

    /** Visibility Enum */
    public sealed class Visibility(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): Visibility =
                when (s) {
                    "followers" -> Followers
                    "members" -> Members
                    "private" -> Private
                    "public" -> Public
                    "visible" -> Visible
                    else -> Unknown(s)
                }
        }

        public object Followers : Visibility("followers")

        public object Members : Visibility("members")

        public object Private : Visibility("private")

        public object Public : Visibility("public")

        public object Visible : Visibility("visible")

        public data class Unknown(val unknownValue: kotlin.String) : Visibility(unknownValue)

        public class VisibilityAdapter : JsonAdapter<Visibility>() {
            @FromJson
            override fun fromJson(reader: JsonReader): Visibility? {
                val s = reader.nextString() ?: return null
                return Visibility.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: Visibility?) {
                writer.value(value?.value)
            }
        }
    }
}
