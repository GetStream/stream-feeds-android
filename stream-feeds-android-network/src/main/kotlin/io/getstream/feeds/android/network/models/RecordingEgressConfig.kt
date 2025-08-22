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

import com.squareup.moshi.Json
import kotlin.collections.*
import kotlin.io.*

/**  */
public data class RecordingEgressConfig(
    @Json(name = "audio_only") public val audioOnly: kotlin.Boolean? = null,
    @Json(name = "storage_name") public val storageName: kotlin.String? = null,
    @Json(name = "composite_app_settings")
    public val compositeAppSettings:
        io.getstream.feeds.android.network.models.CompositeAppSettings? =
        null,
    @Json(name = "external_storage")
    public val externalStorage: io.getstream.feeds.android.network.models.ExternalStorage? = null,
    @Json(name = "quality")
    public val quality: io.getstream.feeds.android.network.models.Quality? = null,
    @Json(name = "video_orientation_hint")
    public val videoOrientationHint: io.getstream.feeds.android.network.models.VideoOrientation? =
        null,
)
