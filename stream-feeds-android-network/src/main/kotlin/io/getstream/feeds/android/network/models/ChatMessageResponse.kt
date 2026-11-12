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
public data class ChatMessageResponse(
    @Json(name = "cid") public val cid: String,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "deleted_reply_count") public val deletedReplyCount: Int,
    @Json(name = "html") public val html: String,
    @Json(name = "id") public val id: String,
    @Json(name = "mentioned_channel") public val mentionedChannel: Boolean,
    @Json(name = "mentioned_here") public val mentionedHere: Boolean,
    @Json(name = "pinned") public val pinned: Boolean,
    @Json(name = "reply_count") public val replyCount: Int,
    @Json(name = "shadowed") public val shadowed: Boolean,
    @Json(name = "silent") public val silent: Boolean,
    @Json(name = "text") public val text: String,
    @Json(name = "type") public val type: String,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "attachments")
    public val attachments: List<io.getstream.feeds.android.network.models.Attachment> =
        emptyList(),
    @Json(name = "latest_reactions")
    public val latestReactions:
        List<io.getstream.feeds.android.network.models.ChatReactionResponse> =
        emptyList(),
    @Json(name = "mentioned_users")
    public val mentionedUsers: List<io.getstream.feeds.android.network.models.UserResponse> =
        emptyList(),
    @Json(name = "own_reactions")
    public val ownReactions: List<io.getstream.feeds.android.network.models.ChatReactionResponse> =
        emptyList(),
    @Json(name = "restricted_visibility")
    public val restrictedVisibility: List<String> = emptyList(),
    @Json(name = "custom") public val custom: Map<String, Any?> = emptyMap(),
    @Json(name = "reaction_counts") public val reactionCounts: Map<String, Int> = emptyMap(),
    @Json(name = "reaction_scores") public val reactionScores: Map<String, Int> = emptyMap(),
    @Json(name = "user") public val user: io.getstream.feeds.android.network.models.UserResponse,
    @Json(name = "command") public val command: String? = null,
    @Json(name = "deleted_at") public val deletedAt: java.util.Date? = null,
    @Json(name = "deleted_for_me") public val deletedForMe: Boolean? = null,
    @Json(name = "message_text_updated_at") public val messageTextUpdatedAt: java.util.Date? = null,
    @Json(name = "mml") public val mml: String? = null,
    @Json(name = "parent_id") public val parentId: String? = null,
    @Json(name = "pin_expires") public val pinExpires: java.util.Date? = null,
    @Json(name = "pinned_at") public val pinnedAt: java.util.Date? = null,
    @Json(name = "poll_id") public val pollId: String? = null,
    @Json(name = "quoted_message_id") public val quotedMessageId: String? = null,
    @Json(name = "show_in_channel") public val showInChannel: Boolean? = null,
    @Json(name = "mentioned_group_ids") public val mentionedGroupIds: List<String>? = null,
    @Json(name = "mentioned_groups")
    public val mentionedGroups: List<io.getstream.feeds.android.network.models.UserGroupResponse>? =
        null,
    @Json(name = "mentioned_roles") public val mentionedRoles: List<String>? = null,
    @Json(name = "thread_participants")
    public val threadParticipants: List<io.getstream.feeds.android.network.models.UserResponse>? =
        null,
    @Json(name = "draft")
    public val draft: io.getstream.feeds.android.network.models.ChatDraftResponse? = null,
    @Json(name = "i18n") public val i18n: Map<String, String>? = null,
    @Json(name = "image_labels") public val imageLabels: Map<String, List<String>>? = null,
    @Json(name = "member")
    public val member: io.getstream.feeds.android.network.models.ChannelMemberPartialResponse? =
        null,
    @Json(name = "moderation")
    public val moderation: io.getstream.feeds.android.network.models.ChatModerationV2Response? =
        null,
    @Json(name = "pinned_by")
    public val pinnedBy: io.getstream.feeds.android.network.models.UserResponse? = null,
    @Json(name = "poll")
    public val poll: io.getstream.feeds.android.network.models.PollResponseData? = null,
    @Json(name = "quoted_message")
    public val quotedMessage: io.getstream.feeds.android.network.models.ChatMessageResponse? = null,
    @Json(name = "reaction_groups")
    public val reactionGroups:
        Map<String, io.getstream.feeds.android.network.models.ChatReactionGroupResponse>? =
        null,
    @Json(name = "reminder")
    public val reminder: io.getstream.feeds.android.network.models.ChatReminderResponseData? = null,
    @Json(name = "shared_location")
    public val sharedLocation:
        io.getstream.feeds.android.network.models.ChatSharedLocationResponseData? =
        null,
)
