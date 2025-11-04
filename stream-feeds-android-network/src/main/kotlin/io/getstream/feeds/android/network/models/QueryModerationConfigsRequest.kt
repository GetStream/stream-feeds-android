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
public data class QueryModerationConfigsRequest(
    @Json(name = "limit") public val limit: kotlin.Int? = null,
    @Json(name = "next") public val next: kotlin.String? = null,
    @Json(name = "prev") public val prev: kotlin.String? = null,
    @Json(name = "sort")
    public val sort:
        kotlin.collections.List<io.getstream.feeds.android.network.models.SortParamRequest>? =
        emptyList(),
    @Json(name = "filter")
    public val filter: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
)
