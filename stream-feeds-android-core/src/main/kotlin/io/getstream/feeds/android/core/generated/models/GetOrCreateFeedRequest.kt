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
import kotlin.collections.Map
import kotlin.io.*

/** Client request */
data class GetOrCreateFeedRequest(
    @Json(name = "limit") val limit: kotlin.Int? = null,
    @Json(name = "next") val next: kotlin.String? = null,
    @Json(name = "prev") val prev: kotlin.String? = null,
    @Json(name = "view") val view: kotlin.String? = null,
    @Json(name = "watch") val watch: kotlin.Boolean? = null,
    @Json(name = "activity_selector_options")
    val activitySelectorOptions: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
    @Json(name = "data")
    val data: io.getstream.feeds.android.core.generated.models.FeedInput? = null,
    @Json(name = "external_ranking")
    val externalRanking: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
    @Json(name = "filter") val filter: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
    @Json(name = "followers_pagination")
    val followersPagination: io.getstream.feeds.android.core.generated.models.PagerRequest? = null,
    @Json(name = "following_pagination")
    val followingPagination: io.getstream.feeds.android.core.generated.models.PagerRequest? = null,
    @Json(name = "interest_weights")
    val interestWeights: kotlin.collections.Map<kotlin.String, kotlin.Float>? = emptyMap(),
    @Json(name = "member_pagination")
    val memberPagination: io.getstream.feeds.android.core.generated.models.PagerRequest? = null,
)
