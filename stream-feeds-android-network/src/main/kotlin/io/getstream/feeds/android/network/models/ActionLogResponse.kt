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
import kotlin.collections.Map
import kotlin.io.*

/**  */
public data class ActionLogResponse(
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "id") public val id: kotlin.String,
    @Json(name = "reason") public val reason: kotlin.String,
    @Json(name = "target_user_id") public val targetUserId: kotlin.String,
    @Json(name = "type") public val type: kotlin.String,
    @Json(name = "user_id") public val userId: kotlin.String,
    @Json(name = "custom")
    public val custom: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),
    @Json(name = "review_queue_item")
    public val reviewQueueItem: io.getstream.feeds.android.network.models.ReviewQueueItemResponse? =
        null,
    @Json(name = "target_user")
    public val targetUser: io.getstream.feeds.android.network.models.UserResponse? = null,
    @Json(name = "user")
    public val user: io.getstream.feeds.android.network.models.UserResponse? = null,
)
