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
public data class BanOptions(
    @Json(name = "delete_messages")
    public val deleteMessages: io.getstream.feeds.android.network.models.DeleteMessages? = null,
    @Json(name = "duration") public val duration: Int? = null,
    @Json(name = "ip_ban") public val ipBan: Boolean? = null,
    @Json(name = "reason") public val reason: String? = null,
    @Json(name = "shadow_ban") public val shadowBan: Boolean? = null,
)
