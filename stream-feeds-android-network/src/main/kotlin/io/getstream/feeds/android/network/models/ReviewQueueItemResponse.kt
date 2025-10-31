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
import kotlin.io.*

/**  */
public data class ReviewQueueItemResponse(
    @Json(name = "ai_text_severity") public val aiTextSeverity: kotlin.String,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "entity_id") public val entityId: kotlin.String,
    @Json(name = "entity_type") public val entityType: kotlin.String,
    @Json(name = "flags_count") public val flagsCount: kotlin.Int,
    @Json(name = "id") public val id: kotlin.String,
    @Json(name = "latest_moderator_action") public val latestModeratorAction: kotlin.String,
    @Json(name = "recommended_action") public val recommendedAction: kotlin.String,
    @Json(name = "reviewed_by") public val reviewedBy: kotlin.String,
    @Json(name = "severity") public val severity: kotlin.Int,
    @Json(name = "status") public val status: kotlin.String,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "actions")
    public val actions:
        kotlin.collections.List<io.getstream.feeds.android.network.models.ActionLogResponse> =
        emptyList(),
    @Json(name = "bans")
    public val bans: kotlin.collections.List<io.getstream.feeds.android.network.models.Ban> =
        emptyList(),
    @Json(name = "flags")
    public val flags:
        kotlin.collections.List<io.getstream.feeds.android.network.models.ModerationFlagResponse> =
        emptyList(),
    @Json(name = "languages")
    public val languages: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "completed_at") public val completedAt: java.util.Date? = null,
    @Json(name = "config_key") public val configKey: kotlin.String? = null,
    @Json(name = "entity_creator_id") public val entityCreatorId: kotlin.String? = null,
    @Json(name = "reviewed_at") public val reviewedAt: java.util.Date? = null,
    @Json(name = "teams") public val teams: kotlin.collections.List<kotlin.String>? = emptyList(),
    @Json(name = "activity")
    public val activity: io.getstream.feeds.android.network.models.EnrichedActivity? = null,
    @Json(name = "assigned_to")
    public val assignedTo: io.getstream.feeds.android.network.models.UserResponse? = null,
    @Json(name = "entity_creator")
    public val entityCreator: io.getstream.feeds.android.network.models.EntityCreatorResponse? =
        null,
    @Json(name = "feeds_v2_activity")
    public val feedsV2Activity: io.getstream.feeds.android.network.models.EnrichedActivity? = null,
    @Json(name = "feeds_v2_reaction")
    public val feedsV2Reaction: io.getstream.feeds.android.network.models.Reaction? = null,
    @Json(name = "feeds_v3_activity")
    public val feedsV3Activity: io.getstream.feeds.android.network.models.ActivityResponse? = null,
    @Json(name = "feeds_v3_comment")
    public val feedsV3Comment: io.getstream.feeds.android.network.models.CommentResponse? = null,
    @Json(name = "moderation_payload")
    public val moderationPayload: io.getstream.feeds.android.network.models.ModerationPayload? =
        null,
    @Json(name = "reaction")
    public val reaction: io.getstream.feeds.android.network.models.Reaction? = null,
)
