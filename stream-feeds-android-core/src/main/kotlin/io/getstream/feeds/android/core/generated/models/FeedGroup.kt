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
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.io.*

/**  */
data class FeedGroup(
    @Json(name = "AggregationVersion") val aggregationVersion: kotlin.Int,
    @Json(name = "AppPK") val appPK: kotlin.Int,
    @Json(name = "created_at") val createdAt: org.threeten.bp.OffsetDateTime,
    @Json(name = "DefaultVisibility") val defaultVisibility: kotlin.String,
    @Json(name = "ID") val iD: kotlin.String,
    @Json(name = "updated_at") val updatedAt: org.threeten.bp.OffsetDateTime,
    @Json(name = "ActivityProcessors")
    val activityProcessors:
        kotlin.collections.List<
            io.getstream.feeds.android.core.generated.models.ActivityProcessorConfig
        > =
        emptyList(),
    @Json(name = "ActivitySelectors")
    val activitySelectors:
        kotlin.collections.List<
            io.getstream.feeds.android.core.generated.models.ActivitySelectorConfig
        > =
        emptyList(),
    @Json(name = "Custom") val custom: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),
    @Json(name = "DeletedAt") val deletedAt: org.threeten.bp.OffsetDateTime? = null,
    @Json(name = "LastFeedGetAt") val lastFeedGetAt: org.threeten.bp.OffsetDateTime? = null,
    @Json(name = "Aggregation")
    val aggregation: io.getstream.feeds.android.core.generated.models.AggregationConfig? = null,
    @Json(name = "Notification")
    val notification: io.getstream.feeds.android.core.generated.models.NotificationConfig? = null,
    @Json(name = "Ranking")
    val ranking: io.getstream.feeds.android.core.generated.models.RankingConfig? = null,
    @Json(name = "Stories")
    val stories: io.getstream.feeds.android.core.generated.models.StoriesConfig? = null,
)
