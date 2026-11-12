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

/** Contains the draft message content */
@com.squareup.moshi.JsonClass(generateAdapter = true)
public data class DraftPayloadResponse(
    @Json(name = "id") public val id: String,
    @Json(name = "text") public val text: String,
    @Json(name = "custom") public val custom: Map<String, Any?> = emptyMap(),
    @Json(name = "html") public val html: String? = null,
    @Json(name = "mml") public val mml: String? = null,
    @Json(name = "parent_id") public val parentId: String? = null,
    @Json(name = "poll_id") public val pollId: String? = null,
    @Json(name = "quoted_message_id") public val quotedMessageId: String? = null,
    @Json(name = "show_in_channel") public val showInChannel: Boolean? = null,
    @Json(name = "silent") public val silent: Boolean? = null,
    @Json(name = "type") public val type: String? = null,
    @Json(name = "attachments")
    public val attachments: List<io.getstream.feeds.android.network.models.Attachment>? = null,
    @Json(name = "mentioned_users")
    public val mentionedUsers: List<io.getstream.feeds.android.network.models.UserResponse>? = null,
)
