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
public data class ChannelMemberLookup(
    @Json(name = "archived") public val archived: kotlin.Boolean,
    @Json(name = "banned") public val banned: kotlin.Boolean,
    @Json(name = "blocked") public val blocked: kotlin.Boolean,
    @Json(name = "hidden") public val hidden: kotlin.Boolean,
    @Json(name = "pinned") public val pinned: kotlin.Boolean,
    @Json(name = "archived_at") public val archivedAt: java.util.Date? = null,
    @Json(name = "ban_expires") public val banExpires: java.util.Date? = null,
    @Json(name = "pinned_at") public val pinnedAt: java.util.Date? = null,
)
