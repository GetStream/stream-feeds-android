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
public data class GetOGResponse(
    @Json(name = "duration") public val duration: kotlin.String,
    @Json(name = "custom")
    public val custom: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),
    @Json(name = "asset_url") public val assetUrl: kotlin.String? = null,
    @Json(name = "author_icon") public val authorIcon: kotlin.String? = null,
    @Json(name = "author_link") public val authorLink: kotlin.String? = null,
    @Json(name = "author_name") public val authorName: kotlin.String? = null,
    @Json(name = "color") public val color: kotlin.String? = null,
    @Json(name = "fallback") public val fallback: kotlin.String? = null,
    @Json(name = "footer") public val footer: kotlin.String? = null,
    @Json(name = "footer_icon") public val footerIcon: kotlin.String? = null,
    @Json(name = "image_url") public val imageUrl: kotlin.String? = null,
    @Json(name = "og_scrape_url") public val ogScrapeUrl: kotlin.String? = null,
    @Json(name = "original_height") public val originalHeight: kotlin.Int? = null,
    @Json(name = "original_width") public val originalWidth: kotlin.Int? = null,
    @Json(name = "pretext") public val pretext: kotlin.String? = null,
    @Json(name = "text") public val text: kotlin.String? = null,
    @Json(name = "thumb_url") public val thumbUrl: kotlin.String? = null,
    @Json(name = "title") public val title: kotlin.String? = null,
    @Json(name = "title_link") public val titleLink: kotlin.String? = null,
    @Json(name = "type") public val type: kotlin.String? = null,
    @Json(name = "actions")
    public val actions: kotlin.collections.List<io.getstream.feeds.android.network.models.Action>? =
        emptyList(),
    @Json(name = "fields")
    public val fields: kotlin.collections.List<io.getstream.feeds.android.network.models.Field>? =
        emptyList(),
    @Json(name = "giphy") public val giphy: io.getstream.feeds.android.network.models.Images? = null,
)
