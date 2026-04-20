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
public data class AppealItemResponse(
    @Json(name = "appeal_reason") public val appealReason: kotlin.String,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "entity_id") public val entityId: kotlin.String,
    @Json(name = "entity_type") public val entityType: kotlin.String,
    @Json(name = "id") public val id: kotlin.String,
    @Json(name = "status") public val status: kotlin.String,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "decision_reason") public val decisionReason: kotlin.String? = null,
    @Json(name = "attachments")
    public val attachments: kotlin.collections.List<kotlin.String>? = emptyList(),
    @Json(name = "entity_content")
    public val entityContent: io.getstream.feeds.android.network.models.ModerationPayload? = null,
    @Json(name = "user")
    public val user: io.getstream.feeds.android.network.models.UserResponse? = null,
)
