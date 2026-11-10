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
public data class LLMConfig(
    @Json(name = "enabled") public val enabled: Boolean,
    @Json(name = "rules")
    public val rules: List<io.getstream.feeds.android.network.models.LLMRule> = emptyList(),
    @Json(name = "app_context") public val appContext: String? = null,
    @Json(name = "async") public val async: Boolean? = null,
    @Json(name = "custom_keys_context_description")
    public val customKeysContextDescription: String? = null,
    @Json(name = "context_custom_keys") public val contextCustomKeys: List<String>? = null,
    @Json(name = "severity_descriptions")
    public val severityDescriptions: Map<String, String>? = null,
)
