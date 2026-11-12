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
public data class FeedsV3CommentResponse(
    @Json(name = "bookmark_count") public val bookmarkCount: Int,
    @Json(name = "confidence_score") public val confidenceScore: Double,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "downvote_count") public val downvoteCount: Int,
    @Json(name = "hidden") public val hidden: Boolean,
    @Json(name = "id") public val id: String,
    @Json(name = "object_id") public val objectId: String,
    @Json(name = "object_type") public val objectType: String,
    @Json(name = "reaction_count") public val reactionCount: Int,
    @Json(name = "reply_count") public val replyCount: Int,
    @Json(name = "score") public val score: Int,
    @Json(name = "status")
    public val status: io.getstream.feeds.android.network.models.CommentStatus,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "upvote_count") public val upvoteCount: Int,
    @Json(name = "mentioned_users")
    public val mentionedUsers: List<io.getstream.feeds.android.network.models.UserResponse> =
        emptyList(),
    @Json(name = "own_reactions")
    public val ownReactions: List<io.getstream.feeds.android.network.models.FeedsReactionResponse> =
        emptyList(),
    @Json(name = "user") public val user: io.getstream.feeds.android.network.models.UserResponse,
    @Json(name = "controversy_score") public val controversyScore: Double? = null,
    @Json(name = "deleted_at") public val deletedAt: java.util.Date? = null,
    @Json(name = "edited_at") public val editedAt: java.util.Date? = null,
    @Json(name = "parent_id") public val parentId: String? = null,
    @Json(name = "text") public val text: String? = null,
    @Json(name = "attachments")
    public val attachments: List<io.getstream.feeds.android.network.models.Attachment>? = null,
    @Json(name = "latest_reactions")
    public val latestReactions:
        List<io.getstream.feeds.android.network.models.FeedsReactionResponse>? =
        null,
    @Json(name = "custom") public val custom: Map<String, Any?>? = null,
    @Json(name = "i18n") public val i18n: Map<String, String>? = null,
    @Json(name = "moderation")
    public val moderation: io.getstream.feeds.android.network.models.ModerationV2Response? = null,
    @Json(name = "reaction_groups")
    public val reactionGroups:
        Map<String, io.getstream.feeds.android.network.models.FeedsReactionGroupResponse>? =
        null,
)
