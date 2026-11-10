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
public data class ChatModerationV2Response(
    @Json(name = "action") public val action: String,
    @Json(name = "original_text") public val originalText: String,
    @Json(name = "blocklist_matched") public val blocklistMatched: String? = null,
    @Json(name = "platform_circumvented") public val platformCircumvented: Boolean? = null,
    @Json(name = "semantic_filter_matched") public val semanticFilterMatched: String? = null,
    @Json(name = "blocklists_matched") public val blocklistsMatched: List<String>? = null,
    @Json(name = "image_harms") public val imageHarms: List<String>? = null,
    @Json(name = "text_harms") public val textHarms: List<String>? = null,
)
