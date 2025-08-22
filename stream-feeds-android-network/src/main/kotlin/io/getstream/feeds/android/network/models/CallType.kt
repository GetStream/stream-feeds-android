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
public data class CallType(
    @Json(name = "AppPK") public val appPK: kotlin.Int,
    @Json(name = "CreatedAt") public val createdAt: java.util.Date,
    @Json(name = "ExternalStorage") public val externalStorage: kotlin.String,
    @Json(name = "Name") public val name: kotlin.String,
    @Json(name = "PK") public val pK: kotlin.Int,
    @Json(name = "UpdatedAt") public val updatedAt: java.util.Date,
    @Json(name = "NotificationSettings")
    public val notificationSettings:
        io.getstream.feeds.android.network.models.NotificationSettings? =
        null,
    @Json(name = "Settings")
    public val settings: io.getstream.feeds.android.network.models.CallSettings? = null,
)
