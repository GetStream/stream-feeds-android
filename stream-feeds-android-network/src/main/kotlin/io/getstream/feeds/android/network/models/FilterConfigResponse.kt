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
public data class FilterConfigResponse(
    @Json(name = "llm_labels") public val llmLabels: List<String> = emptyList(),
    @Json(name = "ai_image_labels") public val aiImageLabels: List<String>? = null,
    @Json(name = "ai_image_ocr_labels") public val aiImageOcrLabels: List<String>? = null,
    @Json(name = "ai_text_labels") public val aiTextLabels: List<String>? = null,
    @Json(name = "config_keys") public val configKeys: List<String>? = null,
    @Json(name = "filterable_custom_keys") public val filterableCustomKeys: List<String>? = null,
    @Json(name = "rule_names") public val ruleNames: List<String>? = null,
    @Json(name = "ai_image_taxonomy") public val aiImageTaxonomy: Map<String, List<String>>? = null,
)
