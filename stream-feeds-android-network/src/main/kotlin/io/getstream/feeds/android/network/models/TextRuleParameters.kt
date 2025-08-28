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
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.io.*

/**  */
public data class TextRuleParameters(
    @Json(name = "contains_url") public val containsUrl: kotlin.Boolean? = null,
    @Json(name = "severity") public val severity: kotlin.String? = null,
    @Json(name = "threshold") public val threshold: kotlin.Int? = null,
    @Json(name = "time_window") public val timeWindow: kotlin.String? = null,
    @Json(name = "blocklist_match")
    public val blocklistMatch: kotlin.collections.List<kotlin.String>? = emptyList(),
    @Json(name = "harm_labels")
    public val harmLabels: kotlin.collections.List<kotlin.String>? = emptyList(),
    @Json(name = "llm_harm_labels")
    public val llmHarmLabels: kotlin.collections.Map<kotlin.String, kotlin.String>? = emptyMap(),
)
