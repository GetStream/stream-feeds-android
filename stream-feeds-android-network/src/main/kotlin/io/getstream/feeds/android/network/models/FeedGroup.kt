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
import kotlin.collections.Map
import kotlin.io.*

/**  */
public data class FeedGroup(
    @Json(name = "aggregation_version") public val aggregationVersion: kotlin.Int,
    @Json(name = "app_pk") public val appPk: kotlin.Int,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "default_visibility") public val defaultVisibility: kotlin.String,
    @Json(name = "group_id") public val groupId: kotlin.String,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "activity_processors")
    public val activityProcessors:
        kotlin.collections.List<io.getstream.feeds.android.network.models.ActivityProcessorConfig> =
        emptyList(),
    @Json(name = "activity_selectors")
    public val activitySelectors:
        kotlin.collections.List<io.getstream.feeds.android.network.models.ActivitySelectorConfig> =
        emptyList(),
    @Json(name = "custom")
    public val custom: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),
    @Json(name = "deleted_at") public val deletedAt: java.util.Date? = null,
    @Json(name = "last_feed_get_at") public val lastFeedGetAt: java.util.Date? = null,
    @Json(name = "aggregation")
    public val aggregation: io.getstream.feeds.android.network.models.AggregationConfig? = null,
    @Json(name = "notification")
    public val notification: io.getstream.feeds.android.network.models.NotificationConfig? = null,
    @Json(name = "push_notification")
    public val pushNotification: io.getstream.feeds.android.network.models.PushNotificationConfig? =
        null,
    @Json(name = "ranking")
    public val ranking: io.getstream.feeds.android.network.models.RankingConfig? = null,
    @Json(name = "stories")
    public val stories: io.getstream.feeds.android.network.models.StoriesConfig? = null,
)
