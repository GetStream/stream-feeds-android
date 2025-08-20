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

import com.squareup.moshi.Json
import kotlin.collections.*
import kotlin.io.*

/**  */
data class RecordingEgressConfig(
    @Json(name = "audio_only") val audioOnly: kotlin.Boolean? = null,
    @Json(name = "storage_name") val storageName: kotlin.String? = null,
    @Json(name = "composite_app_settings")
    val compositeAppSettings:
        io.getstream.feeds.android.core.generated.models.CompositeAppSettings? =
        null,
    @Json(name = "external_storage")
    val externalStorage: io.getstream.feeds.android.core.generated.models.ExternalStorage? = null,
    @Json(name = "quality")
    val quality: io.getstream.feeds.android.core.generated.models.Quality? = null,
    @Json(name = "video_orientation_hint")
    val videoOrientationHint: io.getstream.feeds.android.core.generated.models.VideoOrientation? =
        null,
)
