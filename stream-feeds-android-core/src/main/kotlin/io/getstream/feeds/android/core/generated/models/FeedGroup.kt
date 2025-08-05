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

data class FeedGroup (
    @Json(name = "AppPK")
    val appPK: kotlin.Int,

    @Json(name = "created_at")
    val createdAt: org.threeten.bp.OffsetDateTime,

    @Json(name = "DefaultViewID")
    val defaultViewID: kotlin.String,

    @Json(name = "DefaultVisibility")
    val defaultVisibility: kotlin.String,

    @Json(name = "ID")
    val iD: kotlin.String,

    @Json(name = "updated_at")
    val updatedAt: org.threeten.bp.OffsetDateTime,

    @Json(name = "Custom")
    val custom: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),

    @Json(name = "DeletedAt")
    val deletedAt: org.threeten.bp.OffsetDateTime? = null,

    @Json(name = "LastFeedGetAt")
    val lastFeedGetAt: org.threeten.bp.OffsetDateTime? = null,

    @Json(name = "Notification")
    val notification: io.getstream.feeds.android.core.generated.models.NotificationConfig? = null,

    @Json(name = "Stories")
    val stories: io.getstream.feeds.android.core.generated.models.StoriesConfig? = null
)
