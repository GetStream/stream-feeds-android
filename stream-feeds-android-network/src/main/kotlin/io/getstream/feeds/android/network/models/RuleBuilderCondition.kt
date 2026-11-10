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
public data class RuleBuilderCondition(
    @Json(name = "confidence") public val confidence: Double? = null,
    @Json(name = "type") public val type: String? = null,
    @Json(name = "app_channel_count_params")
    public val appChannelCountParams:
        io.getstream.feeds.android.network.models.AppChannelCountParameters? =
        null,
    @Json(name = "app_message_count_params")
    public val appMessageCountParams:
        io.getstream.feeds.android.network.models.AppMessageCountParameters? =
        null,
    @Json(name = "call_custom_property_params")
    public val callCustomPropertyParams:
        io.getstream.feeds.android.network.models.CallCustomPropertyParameters? =
        null,
    @Json(name = "call_type_rule_params")
    public val callTypeRuleParams:
        io.getstream.feeds.android.network.models.CallTypeRuleParameters? =
        null,
    @Json(name = "call_violation_count_params")
    public val callViolationCountParams:
        io.getstream.feeds.android.network.models.CallViolationCountParameters? =
        null,
    @Json(name = "channel_message_count_rule_params")
    public val channelMessageCountRuleParams:
        io.getstream.feeds.android.network.models.ChannelMessageCountRuleParameters? =
        null,
    @Json(name = "closed_caption_rule_params")
    public val closedCaptionRuleParams:
        io.getstream.feeds.android.network.models.ClosedCaptionRuleParameters? =
        null,
    @Json(name = "content_count_rule_params")
    public val contentCountRuleParams:
        io.getstream.feeds.android.network.models.ContentCountRuleParameters? =
        null,
    @Json(name = "content_country_code_params")
    public val contentCountryCodeParams:
        io.getstream.feeds.android.network.models.ContentCountryCodeParameters? =
        null,
    @Json(name = "content_custom_property_count_params")
    public val contentCustomPropertyCountParams:
        io.getstream.feeds.android.network.models.ContentCustomPropertyCountParameters? =
        null,
    @Json(name = "content_custom_property_params")
    public val contentCustomPropertyParams:
        io.getstream.feeds.android.network.models.ContentCustomPropertyParameters? =
        null,
    @Json(name = "content_flag_count_rule_params")
    public val contentFlagCountRuleParams:
        io.getstream.feeds.android.network.models.FlagCountRuleParameters? =
        null,
    @Json(name = "flood_identical_params")
    public val floodIdenticalParams:
        io.getstream.feeds.android.network.models.FloodIdenticalRuleParameters? =
        null,
    @Json(name = "flood_similar_params")
    public val floodSimilarParams:
        io.getstream.feeds.android.network.models.FloodSimilarRuleParameters? =
        null,
    @Json(name = "image_content_params")
    public val imageContentParams:
        io.getstream.feeds.android.network.models.ImageContentParameters? =
        null,
    @Json(name = "image_rule_params")
    public val imageRuleParams: io.getstream.feeds.android.network.models.ImageRuleParameters? =
        null,
    @Json(name = "intent_rule_params")
    public val intentRuleParams: io.getstream.feeds.android.network.models.IntentRuleParameters? =
        null,
    @Json(name = "ip_content_count_rule_params")
    public val ipContentCountRuleParams:
        io.getstream.feeds.android.network.models.IPContentCountRuleParameters? =
        null,
    @Json(name = "ip_flag_count_rule_params")
    public val ipFlagCountRuleParams:
        io.getstream.feeds.android.network.models.IPFlagCountRuleParameters? =
        null,
    @Json(name = "keyframe_ocr_rule_params")
    public val keyframeOcrRuleParams:
        io.getstream.feeds.android.network.models.KeyframeOCRRuleParameters? =
        null,
    @Json(name = "keyframe_rule_params")
    public val keyframeRuleParams:
        io.getstream.feeds.android.network.models.KeyframeRuleParameters? =
        null,
    @Json(name = "ocr_content_params")
    public val ocrContentParams: io.getstream.feeds.android.network.models.OCRContentParameters? =
        null,
    @Json(name = "text_content_params")
    public val textContentParams: io.getstream.feeds.android.network.models.TextContentParameters? =
        null,
    @Json(name = "text_rule_params")
    public val textRuleParams: io.getstream.feeds.android.network.models.TextRuleParameters? = null,
    @Json(name = "user_channel_ban_count_params")
    public val userChannelBanCountParams:
        io.getstream.feeds.android.network.models.UserChannelBanCountRuleParameters? =
        null,
    @Json(name = "user_channel_count_params")
    public val userChannelCountParams:
        io.getstream.feeds.android.network.models.UserChannelCountRuleParameters? =
        null,
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
    @Json(name = "user_identical_content_count_params")
    public val userIdenticalContentCountParams:
        io.getstream.feeds.android.network.models.UserIdenticalContentCountParameters? =
        null,
    @Json(name = "user_identical_image_count_params")
    public val userIdenticalImageCountParams:
        io.getstream.feeds.android.network.models.UserIdenticalImageCountParameters? =
        null,
    @Json(name = "user_reaction_count_params")
    public val userReactionCountParams:
        io.getstream.feeds.android.network.models.UserReactionCountRuleParameters? =
        null,
    @Json(name = "user_role_params")
    public val userRoleParams: io.getstream.feeds.android.network.models.UserRoleParameters? = null,
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
