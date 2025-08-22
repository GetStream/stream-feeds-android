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
public data class FrameRecordingEgressConfig(
    @Json(name = "capture_interval_in_seconds")
    public val captureIntervalInSeconds: kotlin.Int? = null,
    @Json(name = "storage_name") public val storageName: kotlin.String? = null,
    @Json(name = "external_storage")
    public val externalStorage: io.getstream.feeds.android.network.models.ExternalStorage? = null,
    @Json(name = "quality")
    public val quality: io.getstream.feeds.android.network.models.Quality? = null,
)
