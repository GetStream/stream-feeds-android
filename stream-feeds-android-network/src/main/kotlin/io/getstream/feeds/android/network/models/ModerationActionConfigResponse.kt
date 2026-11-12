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

/** Configuration for a moderation action */
@com.squareup.moshi.JsonClass(generateAdapter = true)
public data class ModerationActionConfigResponse(
    @Json(name = "action") public val action: String,
    @Json(name = "description") public val description: String,
    @Json(name = "entity_type") public val entityType: String,
    @Json(name = "icon") public val icon: String,
    @Json(name = "order") public val order: Int,
    @Json(name = "id") public val id: String? = null,
    @Json(name = "queue_type") public val queueType: String? = null,
    @Json(name = "custom") public val custom: Map<String, Any?>? = null,
)
