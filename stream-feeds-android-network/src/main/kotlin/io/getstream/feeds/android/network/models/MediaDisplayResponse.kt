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

/** Display policy for a review queue item's images and video */
@com.squareup.moshi.JsonClass(generateAdapter = true)
public data class MediaDisplayResponse(
    @Json(name = "audit_reveal") public val auditReveal: Boolean,
    @Json(name = "mode") public val mode: Mode,
    @Json(name = "reveal_restricted") public val revealRestricted: Boolean,
) {

    /** Mode Enum */
    public sealed class Mode(public val value: String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: String): Mode =
                when (s) {
                    "blur" -> Blur
                    "blur_grayscale" -> BlurGrayscale
                    "normal" -> Normal
                    else -> Unknown(s)
                }
        }

        public object Blur : Mode("blur")

        public object BlurGrayscale : Mode("blur_grayscale")

        public object Normal : Mode("normal")

        public data class Unknown(val unknownValue: String) : Mode(unknownValue)

        public class ModeAdapter : JsonAdapter<Mode>() {
            @FromJson
            override fun fromJson(reader: JsonReader): Mode? {
                val s = reader.nextString() ?: return null
                return Mode.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: Mode?) {
                writer.value(value?.value)
            }
        }
    }
}
