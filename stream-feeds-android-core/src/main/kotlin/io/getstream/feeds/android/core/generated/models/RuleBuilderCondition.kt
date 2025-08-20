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
import kotlin.io.*

/**  */
data class RuleBuilderCondition(
    @Json(name = "type") val type: kotlin.String,
    @Json(name = "confidence") val confidence: kotlin.Float? = null,
    @Json(name = "content_count_rule_params")
    val contentCountRuleParams:
        io.getstream.feeds.android.core.generated.models.ContentCountRuleParameters? =
        null,
    @Json(name = "image_content_params")
    val imageContentParams:
        io.getstream.feeds.android.core.generated.models.ImageContentParameters? =
        null,
    @Json(name = "image_rule_params")
    val imageRuleParams: io.getstream.feeds.android.core.generated.models.ImageRuleParameters? =
        null,
    @Json(name = "text_content_params")
    val textContentParams: io.getstream.feeds.android.core.generated.models.TextContentParameters? =
        null,
    @Json(name = "text_rule_params")
    val textRuleParams: io.getstream.feeds.android.core.generated.models.TextRuleParameters? = null,
    @Json(name = "user_created_within_params")
    val userCreatedWithinParams:
        io.getstream.feeds.android.core.generated.models.UserCreatedWithinParameters? =
        null,
    @Json(name = "user_rule_params")
    val userRuleParams: io.getstream.feeds.android.core.generated.models.UserRuleParameters? = null,
    @Json(name = "video_content_params")
    val videoContentParams:
        io.getstream.feeds.android.core.generated.models.VideoContentParameters? =
        null,
    @Json(name = "video_rule_params")
    val videoRuleParams: io.getstream.feeds.android.core.generated.models.VideoRuleParameters? =
        null,
)
