/*
 * Copyright (c) 2014-2024 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-video-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package io.getstream.feeds.android.core.generated.models

import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.*
import kotlin.io.*
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

/**
 * 
 */

data class Message (
    @Json(name = "cid")
    val cid: kotlin.String,

    @Json(name = "created_at")
    val createdAt: org.threeten.bp.OffsetDateTime,

    @Json(name = "deleted_reply_count")
    val deletedReplyCount: kotlin.Int,

    @Json(name = "html")
    val html: kotlin.String,

    @Json(name = "id")
    val id: kotlin.String,

    @Json(name = "pinned")
    val pinned: kotlin.Boolean,

    @Json(name = "reply_count")
    val replyCount: kotlin.Int,

    @Json(name = "shadowed")
    val shadowed: kotlin.Boolean,

    @Json(name = "silent")
    val silent: kotlin.Boolean,

    @Json(name = "text")
    val text: kotlin.String,

    @Json(name = "type")
    val type: kotlin.String,

    @Json(name = "updated_at")
    val updatedAt: org.threeten.bp.OffsetDateTime,

    @Json(name = "attachments")
    val attachments: kotlin.collections.List<io.getstream.feeds.android.core.generated.models.Attachment> = emptyList(),

    @Json(name = "latest_reactions")
    val latestReactions: kotlin.collections.List<io.getstream.feeds.android.core.generated.models.Reaction> = emptyList(),

    @Json(name = "mentioned_users")
    val mentionedUsers: kotlin.collections.List<io.getstream.feeds.android.core.generated.models.User> = emptyList(),

    @Json(name = "own_reactions")
    val ownReactions: kotlin.collections.List<io.getstream.feeds.android.core.generated.models.Reaction> = emptyList(),

    @Json(name = "restricted_visibility")
    val restrictedVisibility: kotlin.collections.List<kotlin.String> = emptyList(),

    @Json(name = "custom")
    val custom: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),

    @Json(name = "reaction_counts")
    val reactionCounts: kotlin.collections.Map<kotlin.String, kotlin.Int> = emptyMap(),

    @Json(name = "reaction_groups")
    val reactionGroups: kotlin.collections.Map<kotlin.String, io.getstream.feeds.android.core.generated.models.ReactionGroupResponse> = emptyMap(),

    @Json(name = "reaction_scores")
    val reactionScores: kotlin.collections.Map<kotlin.String, kotlin.Int> = emptyMap(),

    @Json(name = "before_message_send_failed")
    val beforeMessageSendFailed: kotlin.Boolean? = null,

    @Json(name = "command")
    val command: kotlin.String? = null,

    @Json(name = "deleted_at")
    val deletedAt: org.threeten.bp.OffsetDateTime? = null,

    @Json(name = "message_text_updated_at")
    val messageTextUpdatedAt: org.threeten.bp.OffsetDateTime? = null,

    @Json(name = "mml")
    val mml: kotlin.String? = null,

    @Json(name = "parent_id")
    val parentId: kotlin.String? = null,

    @Json(name = "pin_expires")
    val pinExpires: org.threeten.bp.OffsetDateTime? = null,

    @Json(name = "pinned_at")
    val pinnedAt: org.threeten.bp.OffsetDateTime? = null,

    @Json(name = "poll_id")
    val pollId: kotlin.String? = null,

    @Json(name = "quoted_message_id")
    val quotedMessageId: kotlin.String? = null,

    @Json(name = "show_in_channel")
    val showInChannel: kotlin.Boolean? = null,

    @Json(name = "thread_participants")
    val threadParticipants: kotlin.collections.List<io.getstream.feeds.android.core.generated.models.User>? = emptyList(),

    @Json(name = "i18n")
    val i18n: kotlin.collections.Map<kotlin.String, kotlin.String>? = emptyMap(),

    @Json(name = "image_labels")
    val imageLabels: kotlin.collections.Map<kotlin.String, kotlin.collections.List<kotlin.String>>? = emptyMap(),

    @Json(name = "moderation")
    val moderation: io.getstream.feeds.android.core.generated.models.ModerationV2Response? = null,

    @Json(name = "pinned_by")
    val pinnedBy: io.getstream.feeds.android.core.generated.models.User? = null,

    @Json(name = "poll")
    val poll: io.getstream.feeds.android.core.generated.models.Poll? = null,

    @Json(name = "quoted_message")
    val quotedMessage: io.getstream.feeds.android.core.generated.models.Message? = null,

    @Json(name = "reminder")
    val reminder: io.getstream.feeds.android.core.generated.models.MessageReminder? = null,

    @Json(name = "shared_location")
    val sharedLocation: io.getstream.feeds.android.core.generated.models.SharedLocation? = null,

    @Json(name = "user")
    val user: io.getstream.feeds.android.core.generated.models.User? = null
)
