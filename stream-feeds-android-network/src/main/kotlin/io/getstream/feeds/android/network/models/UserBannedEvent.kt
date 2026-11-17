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

/**
 * This event is sent when a user gets banned. The event contains information about the user that
 * was banned.
 */
@com.squareup.moshi.JsonClass(generateAdapter = true)
public data class UserBannedEvent(
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "custom") public val custom: Map<String, Any?> = emptyMap(),
    @Json(name = "user")
    public val user: io.getstream.feeds.android.network.models.UserResponseCommonFields,
    @Json(name = "type") public val type: String = "user.banned",
    @Json(name = "ban_from_future_channels") public val banFromFutureChannels: Boolean? = null,
    @Json(name = "channel_id") public val channelId: String? = null,
    @Json(name = "channel_member_count") public val channelMemberCount: Int? = null,
    @Json(name = "channel_message_count") public val channelMessageCount: Int? = null,
    @Json(name = "channel_type") public val channelType: String? = null,
    @Json(name = "cid") public val cid: String? = null,
    @Json(name = "expiration") public val expiration: java.util.Date? = null,
    @Json(name = "reason") public val reason: String? = null,
    @Json(name = "received_at") public val receivedAt: java.util.Date? = null,
    @Json(name = "review_queue_item_id") public val reviewQueueItemId: String? = null,
    @Json(name = "shadow") public val shadow: Boolean? = null,
    @Json(name = "team") public val team: String? = null,
    @Json(name = "total_bans") public val totalBans: Int? = null,
    @Json(name = "channel_custom") public val channelCustom: Map<String, Any?>? = null,
    @Json(name = "created_by")
    public val createdBy: io.getstream.feeds.android.network.models.UserResponseCommonFields? = null,
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
