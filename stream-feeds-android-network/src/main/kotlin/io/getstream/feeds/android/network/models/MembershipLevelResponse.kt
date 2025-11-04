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
public data class MembershipLevelResponse(
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "id") public val id: kotlin.String,
    @Json(name = "name") public val name: kotlin.String,
    @Json(name = "priority") public val priority: kotlin.Int,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "tags") public val tags: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "description") public val description: kotlin.String? = null,
    @Json(name = "custom")
    public val custom: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
)
