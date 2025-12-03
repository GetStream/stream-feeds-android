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
public data class RuleBuilderCondition(
    @Json(name = "confidence") public val confidence: kotlin.Float? = null,
    @Json(name = "type") public val type: kotlin.String? = null,
    @Json(name = "content_count_rule_params")
    public val contentCountRuleParams:
        io.getstream.feeds.android.network.models.ContentCountRuleParameters? =
        null,
    @Json(name = "content_flag_count_rule_params")
    public val contentFlagCountRuleParams:
        io.getstream.feeds.android.network.models.FlagCountRuleParameters? =
        null,
    @Json(name = "image_content_params")
    public val imageContentParams:
        io.getstream.feeds.android.network.models.ImageContentParameters? =
        null,
    @Json(name = "image_rule_params")
    public val imageRuleParams: io.getstream.feeds.android.network.models.ImageRuleParameters? =
        null,
    @Json(name = "text_content_params")
    public val textContentParams: io.getstream.feeds.android.network.models.TextContentParameters? =
        null,
    @Json(name = "text_rule_params")
    public val textRuleParams: io.getstream.feeds.android.network.models.TextRuleParameters? = null,
    @Json(name = "user_created_within_params")
    public val userCreatedWithinParams:
        io.getstream.feeds.android.network.models.UserCreatedWithinParameters? =
        null,
    @Json(name = "user_custom_property_params")
    public val userCustomPropertyParams:
        io.getstream.feeds.android.network.models.UserCustomPropertyParameters? =
        null,
    @Json(name = "user_flag_count_rule_params")
    public val userFlagCountRuleParams:
        io.getstream.feeds.android.network.models.FlagCountRuleParameters? =
        null,
    @Json(name = "user_rule_params")
    public val userRuleParams: io.getstream.feeds.android.network.models.UserRuleParameters? = null,
    @Json(name = "video_content_params")
    public val videoContentParams:
        io.getstream.feeds.android.network.models.VideoContentParameters? =
        null,
    @Json(name = "video_rule_params")
    public val videoRuleParams: io.getstream.feeds.android.network.models.VideoRuleParameters? =
        null,
)
