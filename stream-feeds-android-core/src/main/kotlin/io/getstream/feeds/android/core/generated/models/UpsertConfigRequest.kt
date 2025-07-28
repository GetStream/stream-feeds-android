/*
 * Copyright (c) 2014-2024 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-video-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package io.getstream.feeds.android.core.generated.models

import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.*
import kotlin.io.*
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

/**
 * 
 */

data class UpsertConfigRequest (
    @Json(name = "key")
    val key: kotlin.String,

    @Json(name = "async")
    val async: kotlin.Boolean? = null,

    @Json(name = "team")
    val team: kotlin.String? = null,

    @Json(name = "ai_image_config")
    val aiImageConfig: io.getstream.feeds.android.core.generated.models.AIImageConfig? = null,

    @Json(name = "ai_text_config")
    val aiTextConfig: io.getstream.feeds.android.core.generated.models.AITextConfig? = null,

    @Json(name = "ai_video_config")
    val aiVideoConfig: io.getstream.feeds.android.core.generated.models.AIVideoConfig? = null,

    @Json(name = "automod_platform_circumvention_config")
    val automodPlatformCircumventionConfig: io.getstream.feeds.android.core.generated.models.AutomodPlatformCircumventionConfig? = null,

    @Json(name = "automod_semantic_filters_config")
    val automodSemanticFiltersConfig: io.getstream.feeds.android.core.generated.models.AutomodSemanticFiltersConfig? = null,

    @Json(name = "automod_toxicity_config")
    val automodToxicityConfig: io.getstream.feeds.android.core.generated.models.AutomodToxicityConfig? = null,

    @Json(name = "aws_rekognition_config")
    val awsRekognitionConfig: io.getstream.feeds.android.core.generated.models.AIImageConfig? = null,

    @Json(name = "block_list_config")
    val blockListConfig: io.getstream.feeds.android.core.generated.models.BlockListConfig? = null,

    @Json(name = "bodyguard_config")
    val bodyguardConfig: io.getstream.feeds.android.core.generated.models.AITextConfig? = null,

    @Json(name = "google_vision_config")
    val googleVisionConfig: io.getstream.feeds.android.core.generated.models.GoogleVisionConfig? = null,

    @Json(name = "rule_builder_config")
    val ruleBuilderConfig: io.getstream.feeds.android.core.generated.models.RuleBuilderConfig? = null,

    @Json(name = "velocity_filter_config")
    val velocityFilterConfig: io.getstream.feeds.android.core.generated.models.VelocityFilterConfig? = null,

    @Json(name = "video_call_rule_config")
    val videoCallRuleConfig: io.getstream.feeds.android.core.generated.models.VideoCallRuleConfig? = null
)
