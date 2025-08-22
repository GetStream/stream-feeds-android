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
public data class PinActivityResponse(
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "duration") public val duration: kotlin.String,
    @Json(name = "feed") public val feed: kotlin.String,
    @Json(name = "user_id") public val userId: kotlin.String,
    @Json(name = "activity")
    public val activity: io.getstream.feeds.android.network.models.ActivityResponse,
)
