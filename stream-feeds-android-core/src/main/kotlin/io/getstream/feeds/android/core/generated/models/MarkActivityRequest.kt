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

package io.getstream.feeds.android.core.generated.models

import com.squareup.moshi.Json
import kotlin.collections.*
import kotlin.collections.List
import kotlin.io.*

/**  */
data class MarkActivityRequest(
    @Json(name = "mark_all_read") val markAllRead: kotlin.Boolean? = null,
    @Json(name = "mark_all_seen") val markAllSeen: kotlin.Boolean? = null,
    @Json(name = "mark_read") val markRead: kotlin.collections.List<kotlin.String>? = emptyList(),
    @Json(name = "mark_seen") val markSeen: kotlin.collections.List<kotlin.String>? = emptyList(),
    @Json(name = "mark_watched")
    val markWatched: kotlin.collections.List<kotlin.String>? = emptyList(),
)
