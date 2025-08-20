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
data class Ban(
    @Json(name = "created_at") val createdAt: org.threeten.bp.OffsetDateTime,
    @Json(name = "shadow") val shadow: kotlin.Boolean,
    @Json(name = "expires") val expires: org.threeten.bp.OffsetDateTime? = null,
    @Json(name = "reason") val reason: kotlin.String? = null,
    @Json(name = "channel")
    val channel: io.getstream.feeds.android.core.generated.models.Channel? = null,
    @Json(name = "created_by")
    val createdBy: io.getstream.feeds.android.core.generated.models.User? = null,
    @Json(name = "target") val target: io.getstream.feeds.android.core.generated.models.User? = null,
)
