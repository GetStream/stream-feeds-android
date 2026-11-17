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

/**  */
@com.squareup.moshi.JsonClass(generateAdapter = true)
public data class MarkActivityRequest(
    @Json(name = "mark_all_read") public val markAllRead: Boolean? = null,
    @Json(name = "mark_all_seen") public val markAllSeen: Boolean? = null,
    @Json(name = "mark_read") public val markRead: List<String>? = null,
    @Json(name = "mark_seen") public val markSeen: List<String>? = null,
    @Json(name = "mark_watched") public val markWatched: List<String>? = null,
)
