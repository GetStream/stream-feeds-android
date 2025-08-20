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
import kotlin.io.*

/**  */
data class ReviewQueueItem(
    @Json(name = "ai_text_severity") val aiTextSeverity: kotlin.String,
    @Json(name = "bounce_count") val bounceCount: kotlin.Int,
    @Json(name = "config_key") val configKey: kotlin.String,
    @Json(name = "content_changed") val contentChanged: kotlin.Boolean,
    @Json(name = "created_at") val createdAt: org.threeten.bp.OffsetDateTime,
    @Json(name = "entity_id") val entityId: kotlin.String,
    @Json(name = "entity_type") val entityType: kotlin.String,
    @Json(name = "flags_count") val flagsCount: kotlin.Int,
    @Json(name = "has_image") val hasImage: kotlin.Boolean,
    @Json(name = "has_text") val hasText: kotlin.Boolean,
    @Json(name = "has_video") val hasVideo: kotlin.Boolean,
    @Json(name = "id") val id: kotlin.String,
    @Json(name = "moderation_payload_hash") val moderationPayloadHash: kotlin.String,
    @Json(name = "recommended_action") val recommendedAction: kotlin.String,
    @Json(name = "reviewed_by") val reviewedBy: kotlin.String,
    @Json(name = "severity") val severity: kotlin.Int,
    @Json(name = "status") val status: kotlin.String,
    @Json(name = "updated_at") val updatedAt: org.threeten.bp.OffsetDateTime,
    @Json(name = "actions")
    val actions:
        kotlin.collections.List<io.getstream.feeds.android.core.generated.models.ActionLog> =
        emptyList(),
    @Json(name = "bans")
    val bans: kotlin.collections.List<io.getstream.feeds.android.core.generated.models.Ban> =
        emptyList(),
    @Json(name = "flag_labels")
    val flagLabels: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "flag_types") val flagTypes: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "flags")
    val flags: kotlin.collections.List<io.getstream.feeds.android.core.generated.models.Flag> =
        emptyList(),
    @Json(name = "languages") val languages: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "reporter_ids")
    val reporterIds: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "teams") val teams: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "archived_at")
    val archivedAt: io.getstream.feeds.android.core.generated.models.NullTime,
    @Json(name = "completed_at")
    val completedAt: io.getstream.feeds.android.core.generated.models.NullTime,
    @Json(name = "reviewed_at")
    val reviewedAt: io.getstream.feeds.android.core.generated.models.NullTime,
    @Json(name = "activity")
    val activity: io.getstream.feeds.android.core.generated.models.EnrichedActivity? = null,
    @Json(name = "assigned_to")
    val assignedTo: io.getstream.feeds.android.core.generated.models.User? = null,
    @Json(name = "call") val call: io.getstream.feeds.android.core.generated.models.Call? = null,
    @Json(name = "entity_creator")
    val entityCreator: io.getstream.feeds.android.core.generated.models.EntityCreator? = null,
    @Json(name = "feeds_v2_activity")
    val feedsV2Activity: io.getstream.feeds.android.core.generated.models.EnrichedActivity? = null,
    @Json(name = "feeds_v2_reaction")
    val feedsV2Reaction: io.getstream.feeds.android.core.generated.models.Reaction? = null,
    @Json(name = "message")
    val message: io.getstream.feeds.android.core.generated.models.Message? = null,
    @Json(name = "moderation_payload")
    val moderationPayload: io.getstream.feeds.android.core.generated.models.ModerationPayload? =
        null,
    @Json(name = "reaction")
    val reaction: io.getstream.feeds.android.core.generated.models.Reaction? = null,
)
