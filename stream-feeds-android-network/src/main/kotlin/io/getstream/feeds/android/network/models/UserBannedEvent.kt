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
import kotlin.collections.*
import kotlin.collections.Map
import kotlin.io.*

/**
 * This event is sent when a user gets banned. The event contains information about the user that
 * was banned.
 */
public data class UserBannedEvent(
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "custom")
    public val custom: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),
    @Json(name = "user")
    public val user: io.getstream.feeds.android.network.models.UserResponseCommonFields,
    @Json(name = "type") public val type: kotlin.String,
    @Json(name = "channel_id") public val channelId: kotlin.String? = null,
    @Json(name = "channel_member_count") public val channelMemberCount: kotlin.Int? = null,
    @Json(name = "channel_message_count") public val channelMessageCount: kotlin.Int? = null,
    @Json(name = "channel_type") public val channelType: kotlin.String? = null,
    @Json(name = "cid") public val cid: kotlin.String? = null,
    @Json(name = "expiration") public val expiration: java.util.Date? = null,
    @Json(name = "reason") public val reason: kotlin.String? = null,
    @Json(name = "received_at") public val receivedAt: java.util.Date? = null,
    @Json(name = "shadow") public val shadow: kotlin.Boolean? = null,
    @Json(name = "team") public val team: kotlin.String? = null,
    @Json(name = "total_bans") public val totalBans: kotlin.Int? = null,
    @Json(name = "channel_custom")
    public val channelCustom: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
    @Json(name = "created_by")
    public val createdBy: io.getstream.feeds.android.network.models.UserResponseCommonFields? = null,
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
