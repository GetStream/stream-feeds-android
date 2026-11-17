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
public data class GetOrCreateFeedRequest(
    @Json(name = "id_around") public val idAround: String? = null,
    @Json(name = "limit") public val limit: Int? = null,
    @Json(name = "next") public val next: String? = null,
    @Json(name = "overwrite_interest_weights") public val overwriteInterestWeights: Boolean? = null,
    @Json(name = "prev") public val prev: String? = null,
    @Json(name = "view") public val view: String? = null,
    @Json(name = "watch") public val watch: Boolean? = null,
    @Json(name = "discard_actors") public val discardActors: List<String>? = null,
    @Json(name = "data")
    public val data: io.getstream.feeds.android.network.models.FeedInput? = null,
    @Json(name = "enrichment_options")
    public val enrichmentOptions: io.getstream.feeds.android.network.models.EnrichmentOptions? =
        null,
    @Json(name = "external_ranking") public val externalRanking: Map<String, Any?>? = null,
    @Json(name = "filter") public val filter: Map<String, Any?>? = null,
    @Json(name = "followers_pagination")
    public val followersPagination: io.getstream.feeds.android.network.models.PagerRequest? = null,
    @Json(name = "following_pagination")
    public val followingPagination: io.getstream.feeds.android.network.models.PagerRequest? = null,
    @Json(name = "friend_reactions_options")
    public val friendReactionsOptions:
        io.getstream.feeds.android.network.models.FriendReactionsOptions? =
        null,
    @Json(name = "interest_weights") public val interestWeights: Map<String, Float>? = null,
    @Json(name = "member_pagination")
    public val memberPagination: io.getstream.feeds.android.network.models.PagerRequest? = null,
    @Json(name = "viewer_location")
    public val viewerLocation: io.getstream.feeds.android.network.models.Location? = null,
)
