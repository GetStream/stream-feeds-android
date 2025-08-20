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
import kotlin.io.*

/**  */
data class CallType(
    @Json(name = "AppPK") val appPK: kotlin.Int,
    @Json(name = "CreatedAt") val createdAt: org.threeten.bp.OffsetDateTime,
    @Json(name = "ExternalStorage") val externalStorage: kotlin.String,
    @Json(name = "Name") val name: kotlin.String,
    @Json(name = "PK") val pK: kotlin.Int,
    @Json(name = "UpdatedAt") val updatedAt: org.threeten.bp.OffsetDateTime,
    @Json(name = "NotificationSettings")
    val notificationSettings:
        io.getstream.feeds.android.core.generated.models.NotificationSettings? =
        null,
    @Json(name = "Settings")
    val settings: io.getstream.feeds.android.core.generated.models.CallSettings? = null,
)
