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

data class UpdateActivityRequest (
    @Json(name = "expires_at")
    val expiresAt: org.threeten.bp.OffsetDateTime? = null,

    @Json(name = "poll_id")
    val pollId: kotlin.String? = null,

    @Json(name = "text")
    val text: kotlin.String? = null,

    @Json(name = "visibility")
    val visibility: kotlin.String? = null,

    @Json(name = "attachments")
    val attachments: kotlin.collections.List<io.getstream.feeds.android.core.generated.models.Attachment>? = emptyList(),

    @Json(name = "filter_tags")
    val filterTags: kotlin.collections.List<kotlin.String>? = emptyList(),

    @Json(name = "interest_tags")
    val interestTags: kotlin.collections.List<kotlin.String>? = emptyList(),

    @Json(name = "custom")
    val custom: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),

    @Json(name = "location")
    val location: io.getstream.feeds.android.core.generated.models.ActivityLocation? = null
)
