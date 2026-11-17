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

/** Block list contains restricted words */
@com.squareup.moshi.JsonClass(generateAdapter = true)
public data class BlockListResponse(
    @Json(name = "is_confusable_folding_enabled") public val isConfusableFoldingEnabled: Boolean,
    @Json(name = "is_leet_check_enabled") public val isLeetCheckEnabled: Boolean,
    @Json(name = "is_plural_check_enabled") public val isPluralCheckEnabled: Boolean,
    @Json(name = "is_substring_matching_enabled") public val isSubstringMatchingEnabled: Boolean,
    @Json(name = "name") public val name: String,
    @Json(name = "type") public val type: String,
    @Json(name = "words") public val words: List<String> = emptyList(),
    @Json(name = "created_at") public val createdAt: java.util.Date? = null,
    @Json(name = "id") public val id: String? = null,
    @Json(name = "owner_user_id") public val ownerUserId: String? = null,
    @Json(name = "team") public val team: String? = null,
    @Json(name = "updated_at") public val updatedAt: java.util.Date? = null,
)
