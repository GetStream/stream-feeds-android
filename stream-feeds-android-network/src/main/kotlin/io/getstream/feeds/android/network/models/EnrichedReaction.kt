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
public data class EnrichedReaction(
    @Json(name = "activity_id") public val activityId: kotlin.String,
    @Json(name = "kind") public val kind: kotlin.String,
    @Json(name = "user_id") public val userId: kotlin.String,
    @Json(name = "id") public val id: kotlin.String? = null,
    @Json(name = "parent") public val parent: kotlin.String? = null,
    @Json(name = "target_feeds")
    public val targetFeeds: kotlin.collections.List<kotlin.String>? = emptyList(),
    @Json(name = "children_counts")
    public val childrenCounts: kotlin.collections.Map<kotlin.String, kotlin.Int>? = emptyMap(),
    @Json(name = "created_at")
    public val createdAt: io.getstream.feeds.android.network.models.Time? = null,
    @Json(name = "data") public val data: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
    @Json(name = "latest_children")
    public val latestChildren:
        kotlin.collections.Map<
            kotlin.String,
            kotlin.collections.List<io.getstream.feeds.android.network.models.EnrichedReaction>,
        >? =
        emptyMap(),
    @Json(name = "own_children")
    public val ownChildren:
        kotlin.collections.Map<
            kotlin.String,
            kotlin.collections.List<io.getstream.feeds.android.network.models.EnrichedReaction>,
        >? =
        emptyMap(),
    @Json(name = "updated_at")
    public val updatedAt: io.getstream.feeds.android.network.models.Time? = null,
    @Json(name = "user") public val user: io.getstream.feeds.android.network.models.Data? = null,
)
