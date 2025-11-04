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
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.io.*

/**  */
public data class ActivitySelectorConfig(
    @Json(name = "cutoff_time") public val cutoffTime: java.util.Date,
    @Json(name = "cutoff_window") public val cutoffWindow: kotlin.String? = null,
    @Json(name = "min_popularity") public val minPopularity: kotlin.Int? = null,
    @Json(name = "type") public val type: kotlin.String? = null,
    @Json(name = "sort")
    public val sort: kotlin.collections.List<io.getstream.feeds.android.network.models.SortParam>? =
        emptyList(),
    @Json(name = "filter")
    public val filter: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
)
