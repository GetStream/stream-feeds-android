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

/**  */
@com.squareup.moshi.JsonClass(generateAdapter = true)
public data class ModerationCallResponse(
    @Json(name = "backstage") public val backstage: Boolean,
    @Json(name = "captioning") public val captioning: Boolean,
    @Json(name = "cid") public val cid: String,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "current_session_id") public val currentSessionId: String,
    @Json(name = "id") public val id: String,
    @Json(name = "recording") public val recording: Boolean,
    @Json(name = "transcribing") public val transcribing: Boolean,
    @Json(name = "translating") public val translating: Boolean,
    @Json(name = "type") public val type: String,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "blocked_user_ids") public val blockedUserIds: List<String> = emptyList(),
    @Json(name = "custom") public val custom: Map<String, Any?> = emptyMap(),
    @Json(name = "channel_cid") public val channelCid: String? = null,
    @Json(name = "ended_at") public val endedAt: java.util.Date? = null,
    @Json(name = "join_ahead_time_seconds") public val joinAheadTimeSeconds: Int? = null,
    @Json(name = "routing_number") public val routingNumber: String? = null,
    @Json(name = "starts_at") public val startsAt: java.util.Date? = null,
    @Json(name = "team") public val team: String? = null,
    @Json(name = "created_by")
    public val createdBy: io.getstream.feeds.android.network.models.UserResponse? = null,
)
