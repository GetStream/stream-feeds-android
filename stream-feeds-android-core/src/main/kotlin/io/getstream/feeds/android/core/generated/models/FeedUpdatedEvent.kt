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

package io.getstream.feeds.android.core.generated.models

import com.squareup.moshi.Json
import kotlin.collections.*
import kotlin.collections.Map
import kotlin.io.*

/** Emitted when a feed is created. */
data class FeedUpdatedEvent(
    @Json(name = "created_at") val createdAt: org.threeten.bp.OffsetDateTime,
    @Json(name = "fid") val fid: kotlin.String,
    @Json(name = "custom") val custom: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),
    @Json(name = "feed") val feed: io.getstream.feeds.android.core.generated.models.FeedResponse,
    @Json(name = "type") val type: kotlin.String,
    @Json(name = "feed_visibility") val feedVisibility: kotlin.String? = null,
    @Json(name = "received_at") val receivedAt: org.threeten.bp.OffsetDateTime? = null,
    @Json(name = "user")
    val user: io.getstream.feeds.android.core.generated.models.UserResponseCommonFields? = null,
) :
    io.getstream.feeds.android.core.generated.models.WSEvent,
    io.getstream.feeds.android.core.generated.models.WSClientEvent,
    io.getstream.feeds.android.core.generated.models.FeedEvent {

    override fun getWSEventType(): kotlin.String {
        return type
    }

    override fun getWSClientEventType(): kotlin.String {
        return type
    }
}
