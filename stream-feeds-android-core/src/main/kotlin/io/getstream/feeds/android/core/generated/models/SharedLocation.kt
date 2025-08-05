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

data class SharedLocation (
    @Json(name = "channel_cid")
    val channelCid: kotlin.String,

    @Json(name = "created_at")
    val createdAt: org.threeten.bp.OffsetDateTime,

    @Json(name = "created_by_device_id")
    val createdByDeviceId: kotlin.String,

    @Json(name = "message_id")
    val messageId: kotlin.String,

    @Json(name = "updated_at")
    val updatedAt: org.threeten.bp.OffsetDateTime,

    @Json(name = "user_id")
    val userId: kotlin.String,

    @Json(name = "end_at")
    val endAt: org.threeten.bp.OffsetDateTime? = null,

    @Json(name = "latitude")
    val latitude: kotlin.Float? = null,

    @Json(name = "longitude")
    val longitude: kotlin.Float? = null,

    @Json(name = "channel")
    val channel: io.getstream.feeds.android.core.generated.models.Channel? = null,

    @Json(name = "message")
    val message: io.getstream.feeds.android.core.generated.models.Message? = null
)
