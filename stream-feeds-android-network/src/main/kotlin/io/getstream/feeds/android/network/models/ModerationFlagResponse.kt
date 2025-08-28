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
public data class ModerationFlagResponse(
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "entity_id") public val entityId: kotlin.String,
    @Json(name = "entity_type") public val entityType: kotlin.String,
    @Json(name = "type") public val type: kotlin.String,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "user_id") public val userId: kotlin.String,
    @Json(name = "result")
    public val result: kotlin.collections.List<kotlin.collections.Map<kotlin.String, Any?>> =
        emptyList(),
    @Json(name = "entity_creator_id") public val entityCreatorId: kotlin.String? = null,
    @Json(name = "reason") public val reason: kotlin.String? = null,
    @Json(name = "review_queue_item_id") public val reviewQueueItemId: kotlin.String? = null,
    @Json(name = "labels") public val labels: kotlin.collections.List<kotlin.String>? = emptyList(),
    @Json(name = "custom")
    public val custom: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
    @Json(name = "moderation_payload")
    public val moderationPayload: io.getstream.feeds.android.network.models.ModerationPayload? =
        null,
    @Json(name = "review_queue_item")
    public val reviewQueueItem: io.getstream.feeds.android.network.models.ReviewQueueItemResponse? =
        null,
    @Json(name = "user")
    public val user: io.getstream.feeds.android.network.models.UserResponse? = null,
)
