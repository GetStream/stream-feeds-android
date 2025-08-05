/*
 * Copyright (c) 2014-2024 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-video-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package io.getstream.feeds.android.core.generated.models

import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.*
import kotlin.io.*
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

/**
 * 
 */

data class DraftResponse (
    @Json(name = "channel_cid")
    val channelCid: kotlin.String,

    @Json(name = "created_at")
    val createdAt: org.threeten.bp.OffsetDateTime,

    @Json(name = "message")
    val message: io.getstream.feeds.android.core.generated.models.DraftPayloadResponse,

    @Json(name = "parent_id")
    val parentId: kotlin.String? = null,

    @Json(name = "channel")
    val channel: io.getstream.feeds.android.core.generated.models.ChannelResponse? = null,

    @Json(name = "parent_message")
    val parentMessage: io.getstream.feeds.android.core.generated.models.MessageResponse? = null,

    @Json(name = "quoted_message")
    val quotedMessage: io.getstream.feeds.android.core.generated.models.MessageResponse? = null
)
