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
import kotlin.io.*

/**  */
public data class ConfigResponse(
    @Json(name = "async") public val async: kotlin.Boolean,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "key") public val key: kotlin.String,
    @Json(name = "team") public val team: kotlin.String,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "ai_image_config")
    public val aiImageConfig: io.getstream.feeds.android.network.models.AIImageConfig? = null,
    @Json(name = "ai_text_config")
    public val aiTextConfig: io.getstream.feeds.android.network.models.AITextConfig? = null,
    @Json(name = "ai_video_config")
    public val aiVideoConfig: io.getstream.feeds.android.network.models.AIVideoConfig? = null,
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
    @Json(name = "llm_config")
    public val llmConfig: io.getstream.feeds.android.network.models.LLMConfig? = null,
    @Json(name = "velocity_filter_config")
    public val velocityFilterConfig:
        io.getstream.feeds.android.network.models.VelocityFilterConfig? =
        null,
)
