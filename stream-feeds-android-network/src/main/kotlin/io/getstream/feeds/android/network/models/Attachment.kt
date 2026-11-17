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

/** An attachment is a message object that represents a file uploaded by a user. */
@com.squareup.moshi.JsonClass(generateAdapter = true)
public data class Attachment(
    @Json(name = "custom") public val custom: Map<String, Any?> = emptyMap(),
    @Json(name = "asset_url") public val assetUrl: String? = null,
    @Json(name = "author_icon") public val authorIcon: String? = null,
    @Json(name = "author_link") public val authorLink: String? = null,
    @Json(name = "author_name") public val authorName: String? = null,
    @Json(name = "color") public val color: String? = null,
    @Json(name = "fallback") public val fallback: String? = null,
    @Json(name = "footer") public val footer: String? = null,
    @Json(name = "footer_icon") public val footerIcon: String? = null,
    @Json(name = "image_url") public val imageUrl: String? = null,
    @Json(name = "og_scrape_url") public val ogScrapeUrl: String? = null,
    @Json(name = "original_height") public val originalHeight: Int? = null,
    @Json(name = "original_width") public val originalWidth: Int? = null,
    @Json(name = "pretext") public val pretext: String? = null,
    @Json(name = "text") public val text: String? = null,
    @Json(name = "thumb_url") public val thumbUrl: String? = null,
    @Json(name = "title") public val title: String? = null,
    @Json(name = "title_link") public val titleLink: String? = null,
    @Json(name = "type") public val type: String? = null,
    @Json(name = "actions")
    public val actions: List<io.getstream.feeds.android.network.models.Action>? = null,
    @Json(name = "fields")
    public val fields: List<io.getstream.feeds.android.network.models.Field>? = null,
    @Json(name = "giphy") public val giphy: io.getstream.feeds.android.network.models.Images? = null,
)
