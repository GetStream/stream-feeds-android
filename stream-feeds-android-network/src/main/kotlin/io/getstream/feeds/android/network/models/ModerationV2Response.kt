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
import kotlin.io.*

/**  */
public data class ModerationV2Response(
    @Json(name = "action") public val action: kotlin.String,
    @Json(name = "original_text") public val originalText: kotlin.String,
    @Json(name = "blocklist_matched") public val blocklistMatched: kotlin.String? = null,
    @Json(name = "platform_circumvented") public val platformCircumvented: kotlin.Boolean? = null,
    @Json(name = "semantic_filter_matched") public val semanticFilterMatched: kotlin.String? = null,
    @Json(name = "image_harms")
    public val imageHarms: kotlin.collections.List<kotlin.String>? = emptyList(),
    @Json(name = "text_harms")
    public val textHarms: kotlin.collections.List<kotlin.String>? = emptyList(),
)
