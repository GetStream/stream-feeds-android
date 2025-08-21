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

/** Represents a call */
public data class CallResponse(
    @Json(name = "backstage") public val backstage: kotlin.Boolean,
    @Json(name = "captioning") public val captioning: kotlin.Boolean,
    @Json(name = "cid") public val cid: kotlin.String,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "current_session_id") public val currentSessionId: kotlin.String,
    @Json(name = "id") public val id: kotlin.String,
    @Json(name = "recording") public val recording: kotlin.Boolean,
    @Json(name = "transcribing") public val transcribing: kotlin.Boolean,
    @Json(name = "type") public val type: kotlin.String,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "blocked_user_ids")
    public val blockedUserIds: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "created_by")
    public val createdBy: io.getstream.feeds.android.network.models.UserResponse,
    @Json(name = "custom")
    public val custom: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),
    @Json(name = "egress")
    public val egress: io.getstream.feeds.android.network.models.EgressResponse,
    @Json(name = "ingress")
    public val ingress: io.getstream.feeds.android.network.models.CallIngressResponse,
    @Json(name = "settings")
    public val settings: io.getstream.feeds.android.network.models.CallSettingsResponse,
    @Json(name = "channel_cid") public val channelCid: kotlin.String? = null,
    @Json(name = "ended_at") public val endedAt: java.util.Date? = null,
    @Json(name = "join_ahead_time_seconds") public val joinAheadTimeSeconds: kotlin.Int? = null,
    @Json(name = "starts_at") public val startsAt: java.util.Date? = null,
    @Json(name = "team") public val team: kotlin.String? = null,
    @Json(name = "session")
    public val session: io.getstream.feeds.android.network.models.CallSessionResponse? = null,
    @Json(name = "thumbnails")
    public val thumbnails: io.getstream.feeds.android.network.models.ThumbnailResponse? = null,
)
