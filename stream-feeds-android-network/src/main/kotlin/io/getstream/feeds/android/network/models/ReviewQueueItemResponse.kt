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
public data class ReviewQueueItemResponse(
    @Json(name = "ai_text_severity") public val aiTextSeverity: String,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "entity_id") public val entityId: String,
    @Json(name = "entity_type") public val entityType: String,
    @Json(name = "escalated") public val escalated: Boolean,
    @Json(name = "flags_count") public val flagsCount: Int,
    @Json(name = "id") public val id: String,
    @Json(name = "latest_moderator_action") public val latestModeratorAction: String,
    @Json(name = "recommended_action") public val recommendedAction: String,
    @Json(name = "reviewed_by") public val reviewedBy: String,
    @Json(name = "severity") public val severity: Int,
    @Json(name = "status") public val status: String,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "actions")
    public val actions: List<io.getstream.feeds.android.network.models.ActionLogResponse> =
        emptyList(),
    @Json(name = "bans")
    public val bans: List<io.getstream.feeds.android.network.models.BanInfoResponse> = emptyList(),
    @Json(name = "flags")
    public val flags: List<io.getstream.feeds.android.network.models.ModerationFlagResponse> =
        emptyList(),
    @Json(name = "languages") public val languages: List<String> = emptyList(),
    @Json(name = "media_display")
    public val mediaDisplay: io.getstream.feeds.android.network.models.MediaDisplayResponse,
    @Json(name = "completed_at") public val completedAt: java.util.Date? = null,
    @Json(name = "confidence_score") public val confidenceScore: Float? = null,
    @Json(name = "config_key") public val configKey: String? = null,
    @Json(name = "country_code") public val countryCode: String? = null,
    @Json(name = "entity_creator_id") public val entityCreatorId: String? = null,
    @Json(name = "escalated_at") public val escalatedAt: java.util.Date? = null,
    @Json(name = "escalated_by") public val escalatedBy: String? = null,
    @Json(name = "reviewed_at") public val reviewedAt: java.util.Date? = null,
    @Json(name = "country_codes") public val countryCodes: List<String>? = null,
    @Json(name = "false_positive_flags") public val falsePositiveFlags: List<String>? = null,
    @Json(name = "teams") public val teams: List<String>? = null,
    @Json(name = "activity")
    public val activity: io.getstream.feeds.android.network.models.EnrichedActivity? = null,
    @Json(name = "appeal")
    public val appeal: io.getstream.feeds.android.network.models.AppealItemResponse? = null,
    @Json(name = "assigned_to")
    public val assignedTo: io.getstream.feeds.android.network.models.UserResponse? = null,
    @Json(name = "call")
    public val call: io.getstream.feeds.android.network.models.ModerationCallResponse? = null,
    @Json(name = "entity_creator")
    public val entityCreator: io.getstream.feeds.android.network.models.EntityCreatorResponse? =
        null,
    @Json(name = "escalation_metadata")
    public val escalationMetadata: io.getstream.feeds.android.network.models.EscalationMetadata? =
        null,
    @Json(name = "feeds_v2_activity")
    public val feedsV2Activity: io.getstream.feeds.android.network.models.EnrichedActivity? = null,
    @Json(name = "feeds_v2_reaction")
    public val feedsV2Reaction: io.getstream.feeds.android.network.models.Reaction? = null,
    @Json(name = "feeds_v3_activity")
    public val feedsV3Activity: io.getstream.feeds.android.network.models.FeedsV3ActivityResponse? =
        null,
    @Json(name = "feeds_v3_comment")
    public val feedsV3Comment: io.getstream.feeds.android.network.models.FeedsV3CommentResponse? =
        null,
    @Json(name = "message")
    public val message: io.getstream.feeds.android.network.models.ChatMessageResponse? = null,
    @Json(name = "moderation_payload")
    public val moderationPayload:
        io.getstream.feeds.android.network.models.ModerationPayloadResponse? =
        null,
    @Json(name = "poll")
    public val poll: io.getstream.feeds.android.network.models.PollResponseData? = null,
    @Json(name = "reaction")
    public val reaction: io.getstream.feeds.android.network.models.Reaction? = null,
)
