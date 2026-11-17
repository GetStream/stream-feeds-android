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
public data class RuleBuilderRule(
    @Json(name = "rule_type") public val ruleType: String,
    @Json(name = "cooldown_period") public val cooldownPeriod: String? = null,
    @Json(name = "id") public val id: String? = null,
    @Json(name = "logic") public val logic: String? = null,
    @Json(name = "action_sequences")
    public val actionSequences:
        List<io.getstream.feeds.android.network.models.CallRuleActionSequence>? =
        null,
    @Json(name = "conditions")
    public val conditions: List<io.getstream.feeds.android.network.models.RuleBuilderCondition>? =
        null,
    @Json(name = "groups")
    public val groups: List<io.getstream.feeds.android.network.models.RuleBuilderConditionGroup>? =
        null,
    @Json(name = "action")
    public val action: io.getstream.feeds.android.network.models.RuleBuilderAction? = null,
)
