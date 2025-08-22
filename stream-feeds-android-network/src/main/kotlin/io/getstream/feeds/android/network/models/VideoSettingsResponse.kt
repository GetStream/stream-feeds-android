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
public data class VideoSettingsResponse(
    @Json(name = "access_request_enabled") public val accessRequestEnabled: kotlin.Boolean,
    @Json(name = "camera_default_on") public val cameraDefaultOn: kotlin.Boolean,
    @Json(name = "camera_facing") public val cameraFacing: CameraFacing,
    @Json(name = "enabled") public val enabled: kotlin.Boolean,
    @Json(name = "target_resolution")
    public val targetResolution: io.getstream.feeds.android.network.models.TargetResolution,
) {

    /** CameraFacing Enum */
    public sealed class CameraFacing(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): CameraFacing =
                when (s) {
                    "back" -> Back
                    "external" -> External
                    "front" -> Front
                    else -> Unknown(s)
                }
        }

        public object Back : CameraFacing("back")

        public object External : CameraFacing("external")

        public object Front : CameraFacing("front")

        public data class Unknown(val unknownValue: kotlin.String) : CameraFacing(unknownValue)

        public class CameraFacingAdapter : JsonAdapter<CameraFacing>() {
            @FromJson
            override fun fromJson(reader: JsonReader): CameraFacing? {
                val s = reader.nextString() ?: return null
                return CameraFacing.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: CameraFacing?) {
                writer.value(value?.value)
            }
        }
    }
}
