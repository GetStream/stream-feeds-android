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
import kotlin.collections.Map
import kotlin.io.*

/**  */
public data class UpsertPushPreferencesResponse(
    @Json(name = "duration") public val duration: kotlin.String,
    @Json(name = "user_channel_preferences")
    public val userChannelPreferences:
        kotlin.collections.Map<
            kotlin.String,
            kotlin.collections.Map<
                kotlin.String,
                io.getstream.feeds.android.network.models.ChannelPushPreferences,
            >,
        > =
        emptyMap(),
    @Json(name = "user_preferences")
    public val userPreferences:
        kotlin.collections.Map<
            kotlin.String,
            io.getstream.feeds.android.network.models.PushPreferences,
        > =
        emptyMap(),
)
