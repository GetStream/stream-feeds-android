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
package io.getstream.feeds.android.client.internal.model

import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.state.query.CommentsSortDataFields
import io.getstream.feeds.android.client.internal.utils.upsert
import io.getstream.feeds.android.client.internal.utils.upsertSorted
import io.getstream.feeds.android.network.models.ThreadedCommentResponse

/** Converts a [ThreadedCommentResponse] to a [ThreadedCommentData] model. */
internal fun ThreadedCommentResponse.toModel(): ThreadedCommentData =
    ThreadedCommentData(
        attachments = attachments,
        confidenceScore = confidenceScore,
        controversyScore = controversyScore,
        createdAt = createdAt,
        custom = custom,
        deletedAt = deletedAt,
        downvoteCount = downvoteCount,
        id = id,
        latestReactions = latestReactions?.map { it.toModel() }.orEmpty(),
        mentionedUsers = mentionedUsers.map { it.toModel() },
        meta = meta,
        moderation = moderation?.toModel(),
        objectId = objectId,
        objectType = objectType,
        ownReactions = ownReactions.map { it.toModel() },
        parentId = parentId,
        reactionCount = reactionCount,
        reactionGroups = reactionGroups?.mapValues { (_, v) -> v.toModel() }.orEmpty(),
        replies = replies?.map { it.toModel() },
        replyCount = replyCount,
        score = score,
        status = status,
        text = text,
        updatedAt = updatedAt,
        upvoteCount = upvoteCount,
        user = user.toModel(),
    )

/**
 * Sets the comment data for this threaded comment, replacing its properties with those from the
 * provided [CommentData]. [ThreadedCommentData.ownReactions] is not overwritten because "own" data
 * coming from WS event is not reliable. Optionally, a different instance can be provided to be used
 * instead of the current one.
 *
 * @param comment The [CommentData] to set for this threaded comment.
 * @return A new [ThreadedCommentData] instance with the updated comment data.
 */
internal fun ThreadedCommentData.update(
    comment: CommentData,
    ownReactions: List<FeedsReactionData> = this.ownReactions,
): ThreadedCommentData {
    return this.copy(
        attachments = comment.attachments,
        confidenceScore = comment.confidenceScore,
        controversyScore = comment.controversyScore,
        createdAt = comment.createdAt,
        custom = comment.custom,
        deletedAt = comment.deletedAt,
        downvoteCount = comment.downvoteCount,
        id = comment.id,
        latestReactions = comment.latestReactions,
        mentionedUsers = comment.mentionedUsers,
        meta = this.meta,
        moderation = comment.moderation,
        objectId = comment.objectId,
        objectType = comment.objectType,
        ownReactions = ownReactions,
        parentId = comment.parentId,
        reactionCount = comment.reactionCount,
        reactionGroups = comment.reactionGroups,
        replies = this.replies,
        replyCount = comment.replyCount,
        score = comment.score,
        status = comment.status,
        text = comment.text,
        updatedAt = comment.updatedAt,
        upvoteCount = comment.upvoteCount,
        user = comment.user,
    )
}

/**
 * Calls [changeReactions] with a [filter] operation to remove the reaction.
 *
 * @see changeReactions
 */
internal fun ThreadedCommentData.removeReaction(
    updated: CommentData,
    reaction: FeedsReactionData,
    currentUserId: String,
): ThreadedCommentData =
    changeReactions(updated, reaction, currentUserId) { filter { it.id != reaction.id } }

/**
 * Calls [changeReactions] with an [upsert] operation.
 *
 * @see changeReactions
 */
internal fun ThreadedCommentData.upsertReaction(
    updated: CommentData,
    reaction: FeedsReactionData,
    currentUserId: String,
    enforceUnique: Boolean,
): ThreadedCommentData =
    changeReactions(updated, reaction, currentUserId) { upsertReaction(reaction, enforceUnique) }

/**
 * Merges the receiver comment with [updated] and updates own reactions using the provided
 * [updateOwnReactions] function if the reaction belongs to the current user.
 *
 * @param updated The updated comment data to merge with the current one.
 * @param reaction The reaction that was added or removed.
 * @param currentUserId The ID of the current user, used to determine if the reaction belongs to
 *   them.
 * @param updateOwnReactions A function that takes the current list of own reactions and returns the
 *   updated list of own reactions.
 * @return The updated [ThreadedCommentData] instance.
 */
internal inline fun ThreadedCommentData.changeReactions(
    updated: CommentData,
    reaction: FeedsReactionData,
    currentUserId: String,
    updateOwnReactions: List<FeedsReactionData>.() -> List<FeedsReactionData>,
): ThreadedCommentData {
    val updatedOwnReactions =
        if (reaction.user.id == currentUserId) {
            this.ownReactions.updateOwnReactions()
        } else {
            this.ownReactions
        }

    return update(updated, ownReactions = updatedOwnReactions)
}

/**
 * Searches for the parent comment of the given [reply] and upserts it in the replies list.
 *
 * @param reply The reply comment to upsert.
 * @param comparator The comparator used to maintain the sort order of replies.
 * @return A new [ThreadedCommentData] instance with the updated replies.
 */
internal fun ThreadedCommentData.upsertNestedReply(
    reply: CommentData,
    comparator: Comparator<CommentsSortDataFields>,
): ThreadedCommentData =
    when {
        // If this comment is the parent, upsert the reply directly
        id == reply.parentId -> upsertReply(reply, comparator)
        // If this comment has no replies, return it unchanged
        replies.isNullOrEmpty() -> this
        // If this comment has replies, recursively search through them
        else -> {
            val updatedReplies =
                replies.map { replyComment -> replyComment.upsertNestedReply(reply, comparator) }
            copy(replies = updatedReplies)
        }
    }

/**
 * Adds or updates a reply in the replies list and updates the reply count accordingly.
 *
 * @param reply The reply comment to upsert.
 * @return A new [ThreadedCommentData] instance with the updated replies and reply count.
 */
private fun ThreadedCommentData.upsertReply(
    reply: CommentData,
    comparator: Comparator<CommentsSortDataFields>,
): ThreadedCommentData {
    val currentReplies = replies.orEmpty()
    val updatedReplies =
        currentReplies.upsertSorted(
            element = ThreadedCommentData(reply),
            idSelector = ThreadedCommentData::id,
            comparator = comparator,
            update = { it.update(reply) },
        )
    val updatedCount =
        if (updatedReplies.size > currentReplies.size) {
            replyCount + 1
        } else {
            replyCount
        }
    return copy(replies = updatedReplies, replyCount = updatedCount)
}
