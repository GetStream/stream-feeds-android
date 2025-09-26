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

import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.ActivityDataVisibility
import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.utils.updateIf
import io.getstream.feeds.android.client.internal.utils.upsert
import io.getstream.feeds.android.network.models.ActivityResponse
import kotlin.math.max

/** Converts an [ActivityResponse] to an [ActivityData] model. */
internal fun ActivityResponse.toModel(): ActivityData =
    ActivityData(
        attachments = attachments,
        bookmarkCount = bookmarkCount,
        commentCount = commentCount,
        comments = comments.map { it.toModel() },
        createdAt = createdAt,
        currentFeed = currentFeed?.toModel(),
        custom = custom,
        deletedAt = deletedAt,
        editedAt = editedAt,
        expiresAt = expiresAt,
        feeds = feeds,
        filterTags = filterTags,
        id = id,
        interestTags = interestTags,
        latestReactions = latestReactions.map { it.toModel() },
        location = location,
        mentionedUsers = mentionedUsers.map { it.toModel() },
        moderation = moderation?.toModel(),
        notificationContext = notificationContext,
        ownBookmarks = ownBookmarks.map { it.toModel() },
        ownReactions = ownReactions.map { it.toModel() },
        parent = parent?.toModel(),
        poll = poll?.toModel(),
        popularity = popularity,
        reactionCount = reactionCount,
        reactionGroups = reactionGroups.mapValues { it.value.toModel() },
        score = score,
        searchData = searchData,
        shareCount = shareCount,
        text = text,
        type = type,
        updatedAt = updatedAt,
        user = user.toModel(),
        visibility = visibility.toModel(),
        visibilityTag = visibilityTag,
    )

/** Converts a [ActivityDataVisibility] to a [ActivityDataVisibility]. */
internal fun ActivityResponse.Visibility.toModel(): ActivityDataVisibility =
    when (this) {
        ActivityResponse.Visibility.Private -> ActivityDataVisibility.Private
        ActivityResponse.Visibility.Public -> ActivityDataVisibility.Public
        ActivityResponse.Visibility.Tag -> ActivityDataVisibility.Tag
        is ActivityResponse.Visibility.Unknown -> ActivityDataVisibility.Unknown(unknownValue)
    }

/**
 * Extension function to update the activity while preserving own bookmarks, reactions, and poll
 * votes because "own" data from WS events is not reliable. Optionally, different instances can be
 * provided to be used instead of the current ones.
 */
internal fun ActivityData.update(
    updated: ActivityData,
    ownBookmarks: List<BookmarkData> = this.ownBookmarks,
    ownReactions: List<FeedsReactionData> = this.ownReactions,
): ActivityData =
    updated.copy(
        ownBookmarks = ownBookmarks,
        ownReactions = ownReactions,
        poll = updated.poll?.let { poll?.update(it) ?: it },
    )

/**
 * Adds a comment to the activity, updating the comment count and the list of comments.
 *
 * @param comment The comment to be added.
 * @return A new [ActivityData] instance with the updated comments and comment count.
 */
internal fun ActivityData.addComment(comment: CommentData): ActivityData {
    val updatedComments = this.comments.upsert(comment, CommentData::id)
    val updatedCommentCount =
        if (updatedComments.size > this.comments.size) {
            this.commentCount + 1
        } else {
            this.commentCount
        }
    return this.copy(comments = updatedComments, commentCount = updatedCommentCount)
}

/**
 * Removes a comment from the activity, updating the comment count and the list of comments.
 *
 * @param comment The comment to be removed.
 * @return A new [ActivityData] instance with the updated comments and comment count.
 */
internal fun ActivityData.removeComment(comment: CommentData): ActivityData {
    val updatedComments = this.comments.filter { it.id != comment.id }
    return this.copy(comments = updatedComments, commentCount = max(0, this.commentCount - 1))
}

/**
 * Calls [changeBookmarks] with a [filter] operation to remove the bookmark.
 *
 * @see changeBookmarks
 */
internal fun ActivityData.deleteBookmark(bookmark: BookmarkData, currentUserId: String) =
    changeBookmarks(bookmark, currentUserId) { filter { it.id != bookmark.id } }

/**
 * Calls [changeBookmarks] with an [upsert] operation.
 *
 * @see changeBookmarks
 */
internal fun ActivityData.upsertBookmark(
    bookmark: BookmarkData,
    currentUserId: String,
): ActivityData = changeBookmarks(bookmark, currentUserId) { upsert(bookmark, BookmarkData::id) }

/**
 * Merges the receiver activity with [bookmark]'s [BookmarkData.activity] and updates own bookmarks
 * using the provided [updateOwnBookmarks] function if the bookmark belongs to the current user.
 *
 * @param bookmark The bookmark that was added or removed.
 * @param currentUserId The ID of the current user, used to determine if the bookmark belongs to
 *   them.
 * @param updateOwnBookmarks A function that takes the current list of own bookmarks and returns the
 *   updated list of own bookmarks.
 * @return The updated [ActivityData] instance.
 */
internal inline fun ActivityData.changeBookmarks(
    bookmark: BookmarkData,
    currentUserId: String,
    updateOwnBookmarks: List<BookmarkData>.() -> List<BookmarkData>,
): ActivityData {
    val updatedOwnBookmarks =
        if (bookmark.user.id == currentUserId) {
            this.ownBookmarks.updateOwnBookmarks()
        } else {
            this.ownBookmarks
        }

    return update(updated = bookmark.activity, ownBookmarks = updatedOwnBookmarks)
}

/**
 * Calls [changeReactions] with a [filter] operation to remove the reaction.
 *
 * @see changeReactions
 */
internal fun ActivityData.removeReaction(
    updated: ActivityData,
    reaction: FeedsReactionData,
    currentUserId: String,
): ActivityData =
    changeReactions(updated, reaction, currentUserId) { filter { it.id != reaction.id } }

/**
 * Calls [changeReactions] with an [upsert] operation.
 *
 * @see changeReactions
 */
internal fun ActivityData.upsertReaction(
    updated: ActivityData,
    reaction: FeedsReactionData,
    currentUserId: String,
): ActivityData =
    changeReactions(updated, reaction, currentUserId) { upsert(reaction, FeedsReactionData::id) }

/**
 * Merges the receiver activity with [updated] and updates own reactions using the provided
 * [updateOwnReactions] function if the reaction belongs to the current user.
 *
 * @param updated The updated activity data to merge with the current activity.
 * @param reaction The reaction that was added or removed.
 * @param currentUserId The ID of the current user, used to determine if the reaction belongs to
 *   them.
 * @param updateOwnReactions A function that takes the current list of own reactions and returns the
 *   updated list of own reactions.
 * @return The updated [ActivityData] instance.
 */
internal inline fun ActivityData.changeReactions(
    updated: ActivityData,
    reaction: FeedsReactionData,
    currentUserId: String,
    updateOwnReactions: List<FeedsReactionData>.() -> List<FeedsReactionData>,
): ActivityData {
    val updatedOwnReactions =
        if (reaction.user.id == currentUserId) {
            this.ownReactions.updateOwnReactions()
        } else {
            this.ownReactions
        }

    return update(updated, ownReactions = updatedOwnReactions)
}

internal fun ActivityData.removeCommentReaction(
    updated: CommentData,
    reaction: FeedsReactionData,
    currentUserId: String,
): ActivityData =
    copy(
        comments =
            comments.updateIf({ it.id == updated.id }) { comment ->
                comment.removeReaction(updated, reaction, currentUserId)
            }
    )

internal fun ActivityData.upsertCommentReaction(
    updated: CommentData,
    reaction: FeedsReactionData,
    currentUserId: String,
): ActivityData =
    copy(
        comments =
            comments.updateIf({ it.id == updated.id }) { comment ->
                comment.upsertReaction(updated, reaction, currentUserId)
            }
    )
