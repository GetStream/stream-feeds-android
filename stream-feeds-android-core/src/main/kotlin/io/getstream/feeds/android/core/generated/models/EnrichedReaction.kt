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
data class EnrichedReaction(
    @Json(name = "activity_id") val activityId: kotlin.String,
    @Json(name = "kind") val kind: kotlin.String,
    @Json(name = "user_id") val userId: kotlin.String,
    @Json(name = "id") val id: kotlin.String? = null,
    @Json(name = "parent") val parent: kotlin.String? = null,
    @Json(name = "target_feeds")
    val targetFeeds: kotlin.collections.List<kotlin.String>? = emptyList(),
    @Json(name = "children_counts")
    val childrenCounts: kotlin.collections.Map<kotlin.String, kotlin.Int>? = emptyMap(),
    @Json(name = "created_at")
    val createdAt: io.getstream.feeds.android.core.generated.models.Time? = null,
    @Json(name = "data") val data: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
    @Json(name = "latest_children")
    val latestChildren:
        kotlin.collections.Map<
            kotlin.String,
            kotlin.collections.List<
                io.getstream.feeds.android.core.generated.models.EnrichedReaction
            >,
        >? =
        emptyMap(),
    @Json(name = "own_children")
    val ownChildren:
        kotlin.collections.Map<
            kotlin.String,
            kotlin.collections.List<
                io.getstream.feeds.android.core.generated.models.EnrichedReaction
            >,
        >? =
        emptyMap(),
    @Json(name = "updated_at")
    val updatedAt: io.getstream.feeds.android.core.generated.models.Time? = null,
    @Json(name = "user") val user: io.getstream.feeds.android.core.generated.models.Data? = null,
)
