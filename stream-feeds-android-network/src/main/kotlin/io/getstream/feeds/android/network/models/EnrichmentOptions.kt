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

/**
 * Options to skip specific enrichments to improve performance. Default is false (enrichments are
 * included). Setting a field to true skips that enrichment.
 */
@com.squareup.moshi.JsonClass(generateAdapter = true)
public data class EnrichmentOptions(
    @Json(name = "enrich_own_followings") public val enrichOwnFollowings: Boolean? = null,
    @Json(name = "include_flat_activities") public val includeFlatActivities: Boolean? = null,
    @Json(name = "include_reasons") public val includeReasons: Boolean? = null,
    @Json(name = "include_score_vars") public val includeScoreVars: Boolean? = null,
    @Json(name = "skip_activity") public val skipActivity: Boolean? = null,
    @Json(name = "skip_activity_collections") public val skipActivityCollections: Boolean? = null,
    @Json(name = "skip_activity_comments") public val skipActivityComments: Boolean? = null,
    @Json(name = "skip_activity_current_feed") public val skipActivityCurrentFeed: Boolean? = null,
    @Json(name = "skip_activity_mentioned_users")
    public val skipActivityMentionedUsers: Boolean? = null,
    @Json(name = "skip_activity_own_bookmarks")
    public val skipActivityOwnBookmarks: Boolean? = null,
    @Json(name = "skip_activity_parents") public val skipActivityParents: Boolean? = null,
    @Json(name = "skip_activity_poll") public val skipActivityPoll: Boolean? = null,
    @Json(name = "skip_activity_reactions") public val skipActivityReactions: Boolean? = null,
    @Json(name = "skip_activity_refresh_image_urls")
    public val skipActivityRefreshImageUrls: Boolean? = null,
    @Json(name = "skip_all") public val skipAll: Boolean? = null,
    @Json(name = "skip_feed_member_user") public val skipFeedMemberUser: Boolean? = null,
    @Json(name = "skip_followers") public val skipFollowers: Boolean? = null,
    @Json(name = "skip_following") public val skipFollowing: Boolean? = null,
    @Json(name = "skip_own_capabilities") public val skipOwnCapabilities: Boolean? = null,
    @Json(name = "skip_own_follows") public val skipOwnFollows: Boolean? = null,
    @Json(name = "skip_pins") public val skipPins: Boolean? = null,
)
