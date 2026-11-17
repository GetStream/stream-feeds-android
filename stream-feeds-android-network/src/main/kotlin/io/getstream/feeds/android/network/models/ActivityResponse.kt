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
public data class ActivityResponse(
    @Json(name = "bookmark_count") public val bookmarkCount: Int,
    @Json(name = "comment_count") public val commentCount: Int,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "hidden") public val hidden: Boolean,
    @Json(name = "id") public val id: String,
    @Json(name = "popularity") public val popularity: Int,
    @Json(name = "preview") public val preview: Boolean,
    @Json(name = "reaction_count") public val reactionCount: Int,
    @Json(name = "restrict_replies")
    public val restrictReplies: io.getstream.feeds.android.network.models.RestrictReplies,
    @Json(name = "score") public val score: Float,
    @Json(name = "share_count") public val shareCount: Int,
    @Json(name = "type") public val type: String,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "visibility")
    public val visibility: io.getstream.feeds.android.network.models.ActivityVisibility,
    @Json(name = "attachments")
    public val attachments: List<io.getstream.feeds.android.network.models.Attachment> =
        emptyList(),
    @Json(name = "comments")
    public val comments: List<io.getstream.feeds.android.network.models.CommentResponse> =
        emptyList(),
    @Json(name = "feeds") public val feeds: List<String> = emptyList(),
    @Json(name = "filter_tags") public val filterTags: List<String> = emptyList(),
    @Json(name = "interest_tags") public val interestTags: List<String> = emptyList(),
    @Json(name = "latest_reactions")
    public val latestReactions:
        List<io.getstream.feeds.android.network.models.FeedsReactionResponse> =
        emptyList(),
    @Json(name = "mentioned_users")
    public val mentionedUsers: List<io.getstream.feeds.android.network.models.UserResponse> =
        emptyList(),
    @Json(name = "own_bookmarks")
    public val ownBookmarks: List<io.getstream.feeds.android.network.models.BookmarkResponse> =
        emptyList(),
    @Json(name = "own_reactions")
    public val ownReactions: List<io.getstream.feeds.android.network.models.FeedsReactionResponse> =
        emptyList(),
    @Json(name = "collections")
    public val collections:
        Map<String, io.getstream.feeds.android.network.models.EnrichedCollectionResponse> =
        emptyMap(),
    @Json(name = "custom") public val custom: Map<String, Any?> = emptyMap(),
    @Json(name = "reaction_groups")
    public val reactionGroups:
        Map<String, io.getstream.feeds.android.network.models.FeedsReactionGroupResponse> =
        emptyMap(),
    @Json(name = "search_data") public val searchData: Map<String, Any?> = emptyMap(),
    @Json(name = "user") public val user: io.getstream.feeds.android.network.models.UserResponse,
    @Json(name = "deleted_at") public val deletedAt: java.util.Date? = null,
    @Json(name = "edited_at") public val editedAt: java.util.Date? = null,
    @Json(name = "expires_at") public val expiresAt: java.util.Date? = null,
    @Json(name = "friend_reaction_count") public val friendReactionCount: Int? = null,
    @Json(name = "is_read") public val isRead: Boolean? = null,
    @Json(name = "is_seen") public val isSeen: Boolean? = null,
    @Json(name = "is_watched") public val isWatched: Boolean? = null,
    @Json(name = "moderation_action") public val moderationAction: String? = null,
    @Json(name = "position") public val position: Int? = null,
    @Json(name = "selector_source") public val selectorSource: String? = null,
    @Json(name = "text") public val text: String? = null,
    @Json(name = "top_level_comment_count") public val topLevelCommentCount: Int? = null,
    @Json(name = "visibility_tag") public val visibilityTag: String? = null,
    @Json(name = "friend_reactions")
    public val friendReactions:
        List<io.getstream.feeds.android.network.models.FeedsReactionResponse>? =
        null,
    @Json(name = "latest_shares")
    public val latestShares: List<io.getstream.feeds.android.network.models.ShareResponse>? = null,
    @Json(name = "reasons") public val reasons: List<String>? = null,
    @Json(name = "current_feed")
    public val currentFeed: io.getstream.feeds.android.network.models.FeedResponse? = null,
    @Json(name = "i18n") public val i18n: Map<String, String>? = null,
    @Json(name = "location")
    public val location: io.getstream.feeds.android.network.models.Location? = null,
    @Json(name = "metrics") public val metrics: Map<String, Int>? = null,
    @Json(name = "moderation")
    public val moderation: io.getstream.feeds.android.network.models.ModerationV2Response? = null,
    @Json(name = "notification_context")
    public val notificationContext: io.getstream.feeds.android.network.models.NotificationContext? =
        null,
    @Json(name = "parent")
    public val parent: io.getstream.feeds.android.network.models.ActivityResponse? = null,
    @Json(name = "poll")
    public val poll: io.getstream.feeds.android.network.models.PollResponseData? = null,
    @Json(name = "score_vars") public val scoreVars: Map<String, Any?>? = null,
)
