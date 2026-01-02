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

import com.squareup.moshi.Json
import kotlin.collections.*
import kotlin.io.*

/**  */
public data class Images(
    @Json(name = "fixed_height")
    public val fixedHeight: io.getstream.feeds.android.network.models.ImageData,
    @Json(name = "fixed_height_downsampled")
    public val fixedHeightDownsampled: io.getstream.feeds.android.network.models.ImageData,
    @Json(name = "fixed_height_still")
    public val fixedHeightStill: io.getstream.feeds.android.network.models.ImageData,
    @Json(name = "fixed_width")
    public val fixedWidth: io.getstream.feeds.android.network.models.ImageData,
    @Json(name = "fixed_width_downsampled")
    public val fixedWidthDownsampled: io.getstream.feeds.android.network.models.ImageData,
    @Json(name = "fixed_width_still")
    public val fixedWidthStill: io.getstream.feeds.android.network.models.ImageData,
    @Json(name = "original")
    public val original: io.getstream.feeds.android.network.models.ImageData,
)
