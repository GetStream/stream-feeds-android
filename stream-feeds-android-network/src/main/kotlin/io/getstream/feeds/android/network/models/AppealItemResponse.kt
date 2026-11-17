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
public data class AppealItemResponse(
    @Json(name = "appeal_reason") public val appealReason: String,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "entity_id") public val entityId: String,
    @Json(name = "entity_type") public val entityType: String,
    @Json(name = "id") public val id: String,
    @Json(name = "status") public val status: String,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "ai_text_severity") public val aiTextSeverity: String? = null,
    @Json(name = "appeal_reason_language") public val appealReasonLanguage: String? = null,
    @Json(name = "channel_cid") public val channelCid: String? = null,
    @Json(name = "config_key") public val configKey: String? = null,
    @Json(name = "decision_reason") public val decisionReason: String? = null,
    @Json(name = "recommended_action") public val recommendedAction: String? = null,
    @Json(name = "review_queue_item_id") public val reviewQueueItemId: String? = null,
    @Json(name = "severity") public val severity: Int? = null,
    @Json(name = "actions")
    public val actions: List<io.getstream.feeds.android.network.models.ActionLogResponse>? = null,
    @Json(name = "attachments") public val attachments: List<String>? = null,
    @Json(name = "flag_labels") public val flagLabels: List<String>? = null,
    @Json(name = "flag_types") public val flagTypes: List<String>? = null,
    @Json(name = "flags")
    public val flags: List<io.getstream.feeds.android.network.models.ModerationFlagResponse>? =
        null,
    @Json(name = "languages") public val languages: List<String>? = null,
    @Json(name = "entity_content")
    public val entityContent: io.getstream.feeds.android.network.models.ModerationPayload? = null,
    @Json(name = "moderation_action")
    public val moderationAction: io.getstream.feeds.android.network.models.ActionLogResponse? =
        null,
    @Json(name = "original_moderation_action")
    public val originalModerationAction:
        io.getstream.feeds.android.network.models.ActionLogResponse? =
        null,
    @Json(name = "user")
    public val user: io.getstream.feeds.android.network.models.UserResponse? = null,
)
