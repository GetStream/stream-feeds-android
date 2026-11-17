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
public data class FlagRequest(
    @Json(name = "entity_id") public val entityId: String,
    @Json(name = "entity_type") public val entityType: String,
    @Json(name = "entity_creator_id") public val entityCreatorId: String? = null,
    @Json(name = "reason") public val reason: String? = null,
    @Json(name = "custom") public val custom: Map<String, Any?>? = null,
    @Json(name = "moderation_payload")
    public val moderationPayload: io.getstream.feeds.android.network.models.ModerationPayload? =
        null,
)
