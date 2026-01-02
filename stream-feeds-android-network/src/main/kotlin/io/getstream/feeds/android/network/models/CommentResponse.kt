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
public data class CommentResponse(
    @Json(name = "confidence_score") public val confidenceScore: kotlin.Float,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "downvote_count") public val downvoteCount: kotlin.Int,
    @Json(name = "id") public val id: kotlin.String,
    @Json(name = "object_id") public val objectId: kotlin.String,
    @Json(name = "object_type") public val objectType: kotlin.String,
    @Json(name = "reaction_count") public val reactionCount: kotlin.Int,
    @Json(name = "reply_count") public val replyCount: kotlin.Int,
    @Json(name = "score") public val score: kotlin.Int,
    @Json(name = "status") public val status: kotlin.String,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "upvote_count") public val upvoteCount: kotlin.Int,
    @Json(name = "mentioned_users")
    public val mentionedUsers:
        kotlin.collections.List<io.getstream.feeds.android.network.models.UserResponse> =
        emptyList(),
    @Json(name = "own_reactions")
    public val ownReactions:
        kotlin.collections.List<io.getstream.feeds.android.network.models.FeedsReactionResponse> =
        emptyList(),
    @Json(name = "user") public val user: io.getstream.feeds.android.network.models.UserResponse,
    @Json(name = "controversy_score") public val controversyScore: kotlin.Float? = null,
    @Json(name = "deleted_at") public val deletedAt: java.util.Date? = null,
    @Json(name = "parent_id") public val parentId: kotlin.String? = null,
    @Json(name = "text") public val text: kotlin.String? = null,
    @Json(name = "attachments")
    public val attachments:
        kotlin.collections.List<io.getstream.feeds.android.network.models.Attachment>? =
        emptyList(),
    @Json(name = "latest_reactions")
    public val latestReactions:
        kotlin.collections.List<io.getstream.feeds.android.network.models.FeedsReactionResponse>? =
        emptyList(),
    @Json(name = "custom")
    public val custom: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
    @Json(name = "moderation")
    public val moderation: io.getstream.feeds.android.network.models.ModerationV2Response? = null,
    @Json(name = "reaction_groups")
    public val reactionGroups:
        kotlin.collections.Map<
            kotlin.String,
            io.getstream.feeds.android.network.models.ReactionGroupResponse,
        >? =
        emptyMap(),
)
