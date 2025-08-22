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
import kotlin.collections.Map
import kotlin.io.*

/**  */
public data class LayoutSettings(
    @Json(name = "external_app_url") public val externalAppUrl: kotlin.String,
    @Json(name = "external_css_url") public val externalCssUrl: kotlin.String,
    @Json(name = "name") public val name: Name,
    @Json(name = "detect_orientation") public val detectOrientation: kotlin.Boolean? = null,
    @Json(name = "options")
    public val options: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
) {

    /** Name Enum */
    public sealed class Name(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): Name =
                when (s) {
                    "custom" -> Custom
                    "grid" -> Grid
                    "mobile" -> Mobile
                    "single-participant" -> SingleParticipant
                    "spotlight" -> Spotlight
                    else -> Unknown(s)
                }
        }

        public object Custom : Name("custom")

        public object Grid : Name("grid")

        public object Mobile : Name("mobile")

        public object SingleParticipant : Name("single-participant")

        public object Spotlight : Name("spotlight")

        public data class Unknown(val unknownValue: kotlin.String) : Name(unknownValue)

        public class NameAdapter : JsonAdapter<Name>() {
            @FromJson
            override fun fromJson(reader: JsonReader): Name? {
                val s = reader.nextString() ?: return null
                return Name.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: Name?) {
                writer.value(value?.value)
            }
        }
    }
}
