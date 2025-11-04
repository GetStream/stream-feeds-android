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
import kotlin.collections.Map
import kotlin.io.*

/** This event is sent when a custom moderation action is executed */
public data class ModerationCustomActionEvent(
    @Json(name = "action_id") public val actionId: kotlin.String,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "custom")
    public val custom: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),
    @Json(name = "review_queue_item")
    public val reviewQueueItem: io.getstream.feeds.android.network.models.ReviewQueueItemResponse,
    @Json(name = "type") public val type: kotlin.String,
    @Json(name = "received_at") public val receivedAt: java.util.Date? = null,
    @Json(name = "action_options")
    public val actionOptions: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
) :
    io.getstream.feeds.android.network.models.WSClientEvent,
    io.getstream.feeds.android.network.models.WSEvent,
    io.getstream.feeds.android.network.models.FeedEvent {

    override fun getWSClientEventType(): kotlin.String {
        return type
    }

    override fun getWSEventType(): kotlin.String {
        return type
    }
}
