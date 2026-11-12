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
 * This event is sent when a user gets deleted. The event contains information about the user that
 * was deleted and the deletion options that were used.
 */
@com.squareup.moshi.JsonClass(generateAdapter = true)
public data class UserDeletedEvent(
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "delete_conversation") public val deleteConversation: String,
    @Json(name = "delete_conversation_channels") public val deleteConversationChannels: Boolean,
    @Json(name = "delete_messages") public val deleteMessages: String,
    @Json(name = "delete_user") public val deleteUser: String,
    @Json(name = "hard_delete") public val hardDelete: Boolean,
    @Json(name = "mark_messages_deleted") public val markMessagesDeleted: Boolean,
    @Json(name = "custom") public val custom: Map<String, Any?> = emptyMap(),
    @Json(name = "user")
    public val user: io.getstream.feeds.android.network.models.UserResponseCommonFields,
    @Json(name = "type") public val type: String = "user.deleted",
    @Json(name = "received_at") public val receivedAt: java.util.Date? = null,
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
