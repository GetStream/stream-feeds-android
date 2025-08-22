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
import kotlin.io.*

/**  */
public data class DraftResponse(
    @Json(name = "channel_cid") public val channelCid: kotlin.String,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "message")
    public val message: io.getstream.feeds.android.network.models.DraftPayloadResponse,
    @Json(name = "parent_id") public val parentId: kotlin.String? = null,
    @Json(name = "channel")
    public val channel: io.getstream.feeds.android.network.models.ChannelResponse? = null,
    @Json(name = "parent_message")
    public val parentMessage: io.getstream.feeds.android.network.models.MessageResponse? = null,
    @Json(name = "quoted_message")
    public val quotedMessage: io.getstream.feeds.android.network.models.MessageResponse? = null,
)
