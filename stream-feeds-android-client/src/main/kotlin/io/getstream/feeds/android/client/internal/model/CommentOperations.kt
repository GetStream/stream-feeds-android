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

package io.getstream.feeds.android.client.internal.model

import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.network.models.CommentResponse

/** Converts a [CommentResponse] to a [CommentData] model. */
internal fun CommentResponse.toModel(): CommentData =
    CommentData(
        attachments = attachments,
        confidenceScore = confidenceScore,
        controversyScore = controversyScore,
        createdAt = createdAt,
        custom = custom ?: emptyMap(),
        deletedAt = deletedAt,
        downvoteCount = downvoteCount,
        id = id,
        latestReactions = latestReactions?.map { it.toModel() }.orEmpty(),
        mentionedUsers = mentionedUsers.map { it.toModel() },
        moderation = moderation?.toModel(),
        objectId = objectId,
        objectType = objectType,
        ownReactions = ownReactions.map { it.toModel() },
        parentId = parentId,
        reactionCount = reactionCount,
        reactionGroups = reactionGroups?.mapValues { it.value.toModel() }.orEmpty(),
        replyCount = replyCount,
        score = score,
        status = status,
        text = text,
        updatedAt = updatedAt,
        upvoteCount = upvoteCount,
        user = user.toModel(),
    )

/**
 * Updates the comment while preserving own reactions because "own" data from WS events is not
 * reliable.
 */
internal fun CommentData.update(
    updated: CommentData,
    ownReactions: List<FeedsReactionData> = this.ownReactions,
): CommentData = updated.copy(ownReactions = ownReactions)

/**
 * Removes a reaction from the comment, updating the latest reactions, reaction groups, reaction
 * count, and own reactions.
 *
 * @param updated The updated comment data.
 * @param reaction The reaction to remove.
 * @param currentUserId The ID of the current user, used to determine if the reaction belongs to
 *   them.
 * @return A new [CommentData] instance with the updated reactions and counts.
 */
internal fun CommentData.removeReaction(
    updated: CommentData,
    reaction: FeedsReactionData,
    currentUserId: String,
): CommentData =
    changeReactions(updated, reaction, currentUserId) { filter { it.id != reaction.id } }

/**
 * Merges the receiver comment with [updated] and upserts the given [reaction] into own reactions if
 * it belongs to the current user.
 *
 * @param updated The updated comment data.
 * @param reaction The reaction to be added.
 * @param currentUserId The ID of the current user, used to determine if the reaction belongs to
 *   them.
 * @param enforceUnique Whether to replace existing reactions by the same user.
 * @return A new [CommentData] instance with the updated reactions and counts.
 */
internal fun CommentData.upsertReaction(
    updated: CommentData,
    reaction: FeedsReactionData,
    currentUserId: String,
    enforceUnique: Boolean,
): CommentData =
    changeReactions(updated, reaction, currentUserId) { upsertReaction(reaction, enforceUnique) }

internal inline fun CommentData.changeReactions(
    updated: CommentData,
    reaction: FeedsReactionData,
    currentUserId: String,
    updateOwnReactions: List<FeedsReactionData>.() -> List<FeedsReactionData>,
): CommentData {
    val updatedOwnReactions =
        if (reaction.user.id == currentUserId) {
            this.ownReactions.updateOwnReactions()
        } else {
            this.ownReactions
        }

    return update(updated, ownReactions = updatedOwnReactions)
}
