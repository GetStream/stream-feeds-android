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
public data class APIError(
    @Json(name = "code") public val code: Int,
    @Json(name = "duration") public val duration: String,
    @Json(name = "message") public val message: String,
    @Json(name = "more_info") public val moreInfo: String,
    @Json(name = "StatusCode") public val statusCode: Int,
    @Json(name = "details") public val details: List<Int> = emptyList(),
    @Json(name = "cooldown_remaining_ms") public val cooldownRemainingMs: Int? = null,
    @Json(name = "unrecoverable") public val unrecoverable: Boolean? = null,
    @Json(name = "exception_fields") public val exceptionFields: Map<String, String>? = null,
)
