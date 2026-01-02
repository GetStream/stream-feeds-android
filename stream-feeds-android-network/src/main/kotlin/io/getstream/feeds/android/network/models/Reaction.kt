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
public data class Reaction(
    @Json(name = "activity_id") public val activityId: kotlin.String,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "kind") public val kind: kotlin.String,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "user_id") public val userId: kotlin.String,
    @Json(name = "deleted_at") public val deletedAt: java.util.Date? = null,
    @Json(name = "id") public val id: kotlin.String? = null,
    @Json(name = "parent") public val parent: kotlin.String? = null,
    @Json(name = "score") public val score: kotlin.Float? = null,
    @Json(name = "target_feeds")
    public val targetFeeds: kotlin.collections.List<kotlin.String>? = emptyList(),
    @Json(name = "children_counts")
    public val childrenCounts: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
    @Json(name = "data") public val data: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
    @Json(name = "latest_children")
    public val latestChildren:
        kotlin.collections.Map<
            kotlin.String,
            kotlin.collections.List<io.getstream.feeds.android.network.models.Reaction>,
        >? =
        emptyMap(),
    @Json(name = "moderation")
    public val moderation: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
    @Json(name = "own_children")
    public val ownChildren:
        kotlin.collections.Map<
            kotlin.String,
            kotlin.collections.List<io.getstream.feeds.android.network.models.Reaction>,
        >? =
        emptyMap(),
    @Json(name = "target_feeds_extra_data")
    public val targetFeedsExtraData: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
    @Json(name = "user") public val user: io.getstream.feeds.android.network.models.User? = null,
)
