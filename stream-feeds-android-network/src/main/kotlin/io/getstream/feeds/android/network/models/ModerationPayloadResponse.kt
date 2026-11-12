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

/** Content payload for moderation */
@com.squareup.moshi.JsonClass(generateAdapter = true)
public data class ModerationPayloadResponse(
    @Json(name = "audios") public val audios: List<String>? = null,
    @Json(name = "image_ordered_keys") public val imageOrderedKeys: List<String>? = null,
    @Json(name = "images") public val images: List<String>? = null,
    @Json(name = "other_media") public val otherMedia: List<String>? = null,
    @Json(name = "text_ordered_keys") public val textOrderedKeys: List<String>? = null,
    @Json(name = "texts") public val texts: List<String>? = null,
    @Json(name = "videos") public val videos: List<String>? = null,
    @Json(name = "custom") public val custom: Map<String, Any?>? = null,
    @Json(name = "image_ids") public val imageIds: Map<String, String>? = null,
    @Json(name = "text_ids") public val textIds: Map<String, String>? = null,
)
