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

/** Block list contains restricted words */
public data class BlockListResponse(
    @Json(name = "name") public val name: kotlin.String,
    @Json(name = "type") public val type: kotlin.String,
    @Json(name = "words") public val words: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "created_at") public val createdAt: java.util.Date? = null,
    @Json(name = "id") public val id: kotlin.String? = null,
    @Json(name = "team") public val team: kotlin.String? = null,
    @Json(name = "updated_at") public val updatedAt: java.util.Date? = null,
)
