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

/** User response object */
data class UserResponse(
    @Json(name = "banned") val banned: kotlin.Boolean,
    @Json(name = "created_at") val createdAt: org.threeten.bp.OffsetDateTime,
    @Json(name = "id") val id: kotlin.String,
    @Json(name = "language") val language: kotlin.String,
    @Json(name = "online") val online: kotlin.Boolean,
    @Json(name = "role") val role: kotlin.String,
    @Json(name = "updated_at") val updatedAt: org.threeten.bp.OffsetDateTime,
    @Json(name = "blocked_user_ids")
    val blockedUserIds: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "teams") val teams: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "custom") val custom: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),
    @Json(name = "avg_response_time") val avgResponseTime: kotlin.Int? = null,
    @Json(name = "deactivated_at") val deactivatedAt: org.threeten.bp.OffsetDateTime? = null,
    @Json(name = "deleted_at") val deletedAt: org.threeten.bp.OffsetDateTime? = null,
    @Json(name = "image") val image: kotlin.String? = null,
    @Json(name = "last_active") val lastActive: org.threeten.bp.OffsetDateTime? = null,
    @Json(name = "name") val name: kotlin.String? = null,
    @Json(name = "revoke_tokens_issued_before")
    val revokeTokensIssuedBefore: org.threeten.bp.OffsetDateTime? = null,
    @Json(name = "teams_role")
    val teamsRole: kotlin.collections.Map<kotlin.String, kotlin.String>? = emptyMap(),
)
