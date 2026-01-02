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
import kotlin.collections.List
import kotlin.io.*

/**  */
public data class AggregatedActivityResponse(
    @Json(name = "activity_count") public val activityCount: kotlin.Int,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "group") public val group: kotlin.String,
    @Json(name = "score") public val score: kotlin.Float,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "user_count") public val userCount: kotlin.Int,
    @Json(name = "user_count_truncated") public val userCountTruncated: kotlin.Boolean,
    @Json(name = "activities")
    public val activities:
        kotlin.collections.List<io.getstream.feeds.android.network.models.ActivityResponse> =
        emptyList(),
    @Json(name = "is_watched") public val isWatched: kotlin.Boolean? = null,
)
