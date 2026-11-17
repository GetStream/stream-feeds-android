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
public data class ConfigResponse(
    @Json(name = "async") public val async: Boolean,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "key") public val key: String,
    @Json(name = "team") public val team: String,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "supported_video_call_harm_types")
    public val supportedVideoCallHarmTypes: List<String> = emptyList(),
    @Json(name = "published_version") public val publishedVersion: Int? = null,
    @Json(name = "ai_image_label_definitions")
    public val aiImageLabelDefinitions:
        List<io.getstream.feeds.android.network.models.AIImageLabelDefinition>? =
        null,
    @Json(name = "available_bodyguard_profiles")
    public val availableBodyguardProfiles:
        List<io.getstream.feeds.android.network.models.BodyguardProfileSummary>? =
        null,
    @Json(name = "ai_audio_config")
    public val aiAudioConfig: io.getstream.feeds.android.network.models.AIAudioConfigResponse? =
        null,
    @Json(name = "ai_image_config")
    public val aiImageConfig: io.getstream.feeds.android.network.models.AIImageConfig? = null,
    @Json(name = "ai_image_subclassifications")
    public val aiImageSubclassifications: Map<String, List<String>>? = null,
    @Json(name = "ai_text_config")
    public val aiTextConfig: io.getstream.feeds.android.network.models.AITextConfig? = null,
    @Json(name = "ai_video_config")
    public val aiVideoConfig: io.getstream.feeds.android.network.models.AIVideoConfigResponse? =
        null,
    @Json(name = "automod_platform_circumvention_config")
    public val automodPlatformCircumventionConfig:
        io.getstream.feeds.android.network.models.AutomodPlatformCircumventionConfig? =
        null,
    @Json(name = "automod_semantic_filters_config")
    public val automodSemanticFiltersConfig:
        io.getstream.feeds.android.network.models.AutomodSemanticFiltersConfig? =
        null,
    @Json(name = "automod_toxicity_config")
    public val automodToxicityConfig:
        io.getstream.feeds.android.network.models.AutomodToxicityConfig? =
        null,
    @Json(name = "block_list_config")
    public val blockListConfig: io.getstream.feeds.android.network.models.BlockListConfig? = null,
    @Json(name = "engine")
    public val engine: io.getstream.feeds.android.network.models.ModerationEngineConfigResponse? =
        null,
    @Json(name = "flood_config")
    public val floodConfig: io.getstream.feeds.android.network.models.FloodConfig? = null,
    @Json(name = "intent_config")
    public val intentConfig: io.getstream.feeds.android.network.models.IntentConfigResponse? = null,
    @Json(name = "llm_config")
    public val llmConfig: io.getstream.feeds.android.network.models.LLMConfig? = null,
    @Json(name = "velocity_filter_config")
    public val velocityFilterConfig:
        io.getstream.feeds.android.network.models.VelocityFilterConfig? =
        null,
    @Json(name = "video_call_rule_config")
    public val videoCallRuleConfig: io.getstream.feeds.android.network.models.VideoCallRuleConfig? =
        null,
)
