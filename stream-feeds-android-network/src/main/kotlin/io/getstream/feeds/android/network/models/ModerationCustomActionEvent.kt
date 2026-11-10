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

/** This event is sent when a custom moderation action is executed */
@com.squareup.moshi.JsonClass(generateAdapter = true)
public data class ModerationCustomActionEvent(
    @Json(name = "action_id") public val actionId: String,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "custom") public val custom: Map<String, Any?> = emptyMap(),
    @Json(name = "review_queue_item")
    public val reviewQueueItem: io.getstream.feeds.android.network.models.ReviewQueueItemResponse,
    @Json(name = "type") public val type: String = "moderation.custom_action",
    @Json(name = "received_at") public val receivedAt: java.util.Date? = null,
    @Json(name = "action_options") public val actionOptions: Map<String, Any?>? = null,
    @Json(name = "message")
    public val message: io.getstream.feeds.android.network.models.MessageResponse? = null,
) :
    io.getstream.feeds.android.network.models.WSClientEvent,
    io.getstream.feeds.android.network.models.WSEvent,
    io.getstream.feeds.android.network.models.FeedEvent {

    override fun getWSClientEventType(): String {
        return type
    }

    override fun getWSEventType(): String {
        return type
    }
}
