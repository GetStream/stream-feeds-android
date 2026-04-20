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
import kotlin.collections.*
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.io.*

/**  */
public data class FeedsV3ActivityResponse(
    @Json(name = "bookmark_count") public val bookmarkCount: kotlin.Int,
    @Json(name = "comment_count") public val commentCount: kotlin.Int,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "hidden") public val hidden: kotlin.Boolean,
    @Json(name = "id") public val id: kotlin.String,
    @Json(name = "popularity") public val popularity: kotlin.Int,
    @Json(name = "preview") public val preview: kotlin.Boolean,
    @Json(name = "reaction_count") public val reactionCount: kotlin.Int,
    @Json(name = "restrict_replies") public val restrictReplies: kotlin.String,
    @Json(name = "score") public val score: kotlin.Float,
    @Json(name = "share_count") public val shareCount: kotlin.Int,
    @Json(name = "type") public val type: kotlin.String,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "visibility") public val visibility: kotlin.String,
    @Json(name = "attachments")
    public val attachments:
        kotlin.collections.List<io.getstream.feeds.android.network.models.Attachment> =
        emptyList(),
    @Json(name = "comments")
    public val comments:
        kotlin.collections.List<io.getstream.feeds.android.network.models.FeedsV3CommentResponse> =
        emptyList(),
    @Json(name = "feeds") public val feeds: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "filter_tags")
    public val filterTags: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "interest_tags")
    public val interestTags: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "latest_reactions")
    public val latestReactions: kotlin.collections.List<Any?> = emptyList(),
    @Json(name = "mentioned_users")
    public val mentionedUsers:
        kotlin.collections.List<io.getstream.feeds.android.network.models.UserResponse> =
        emptyList(),
    @Json(name = "own_bookmarks")
    public val ownBookmarks: kotlin.collections.List<Any?> = emptyList(),
    @Json(name = "own_reactions")
    public val ownReactions: kotlin.collections.List<Any?> = emptyList(),
    @Json(name = "collections")
    public val collections:
        kotlin.collections.Map<
            kotlin.String,
            io.getstream.feeds.android.network.models.EnrichedCollection,
        > =
        emptyMap(),
    @Json(name = "custom")
    public val custom: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),
    @Json(name = "reaction_groups")
    public val reactionGroups:
        kotlin.collections.Map<
            kotlin.String,
            io.getstream.feeds.android.network.models.FeedsReactionGroup,
        > =
        emptyMap(),
    @Json(name = "search_data")
    public val searchData: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),
    @Json(name = "user") public val user: io.getstream.feeds.android.network.models.UserResponse,
    @Json(name = "deleted_at") public val deletedAt: java.util.Date? = null,
    @Json(name = "edited_at") public val editedAt: java.util.Date? = null,
    @Json(name = "expires_at") public val expiresAt: java.util.Date? = null,
    @Json(name = "moderation_action") public val moderationAction: kotlin.String? = null,
    @Json(name = "text") public val text: kotlin.String? = null,
    @Json(name = "visibility_tag") public val visibilityTag: kotlin.String? = null,
    @Json(name = "metrics")
    public val metrics: kotlin.collections.Map<kotlin.String, kotlin.Int>? = emptyMap(),
    @Json(name = "moderation")
    public val moderation: io.getstream.feeds.android.network.models.ModerationV2Response? = null,
)
