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
import kotlin.io.*

/**  */
data class ImageSize(
    @Json(name = "crop") val crop: Crop? = null,
    @Json(name = "height") val height: kotlin.Int? = null,
    @Json(name = "resize") val resize: Resize? = null,
    @Json(name = "width") val width: kotlin.Int? = null,
) {

    /** Crop Enum */
    sealed class Crop(val value: kotlin.String) {
        override fun toString(): String = value

        companion object {
            fun fromString(s: kotlin.String): Crop =
                when (s) {
                    "bottom" -> Bottom
                    "center" -> Center
                    "left" -> Left
                    "right" -> Right
                    "top" -> Top
                    else -> Unknown(s)
                }
        }

        object Bottom : Crop("bottom")

        object Center : Crop("center")

        object Left : Crop("left")

        object Right : Crop("right")

        object Top : Crop("top")

        data class Unknown(val unknownValue: kotlin.String) : Crop(unknownValue)

        class CropAdapter : JsonAdapter<Crop>() {
            @FromJson
            override fun fromJson(reader: JsonReader): Crop? {
                val s = reader.nextString() ?: return null
                return Crop.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: Crop?) {
                writer.value(value?.value)
            }
        }
    }

    /** Resize Enum */
    sealed class Resize(val value: kotlin.String) {
        override fun toString(): String = value

        companion object {
            fun fromString(s: kotlin.String): Resize =
                when (s) {
                    "clip" -> Clip
                    "crop" -> Crop
                    "fill" -> Fill
                    "scale" -> Scale
                    else -> Unknown(s)
                }
        }

        object Clip : Resize("clip")

        object Crop : Resize("crop")

        object Fill : Resize("fill")

        object Scale : Resize("scale")

        data class Unknown(val unknownValue: kotlin.String) : Resize(unknownValue)

        class ResizeAdapter : JsonAdapter<Resize>() {
            @FromJson
            override fun fromJson(reader: JsonReader): Resize? {
                val s = reader.nextString() ?: return null
                return Resize.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: Resize?) {
                writer.value(value?.value)
            }
        }
    }
}
