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
public data class QueryUsersPayload(
    @Json(name = "filter_conditions") public val filterConditions: Map<String, Any?> = emptyMap(),
    @Json(name = "id_gt") public val idGt: String? = null,
    @Json(name = "id_gte") public val idGte: String? = null,
    @Json(name = "id_lt") public val idLt: String? = null,
    @Json(name = "id_lte") public val idLte: String? = null,
    @Json(name = "include_deactivated_users") public val includeDeactivatedUsers: Boolean? = null,
    @Json(name = "limit") public val limit: Int? = null,
    @Json(name = "offset") public val offset: Int? = null,
    @Json(name = "presence") public val presence: Boolean? = null,
    @Json(name = "sort")
    public val sort: List<io.getstream.feeds.android.network.models.SortParamRequest>? = null,
)
