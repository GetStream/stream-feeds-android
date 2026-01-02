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

package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.state.ActivityCommentListState
import io.getstream.feeds.android.client.api.state.query.ActivityCommentsQuery
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.model.removeComment
import io.getstream.feeds.android.client.internal.model.removeReaction
import io.getstream.feeds.android.client.internal.model.update
import io.getstream.feeds.android.client.internal.model.upsertNestedReply
import io.getstream.feeds.android.client.internal.model.upsertReaction
import io.getstream.feeds.android.client.internal.state.query.toComparator
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import io.getstream.feeds.android.client.internal.utils.upsertSorted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * A class representing a paginated list of comments for a specific activity.
 *
 * This class provides methods to fetch and manage comments for an activity, including pagination
 * support and real-time updates through WebSocket events. It maintains an observable state that
 * automatically updates when comment-related events are received.
 */
internal class ActivityCommentListStateImpl(
    override val query: ActivityCommentsQuery,
    private val currentUserId: String,
) : ActivityCommentListMutableState {

    private val _comments: MutableStateFlow<List<ThreadedCommentData>> =
        MutableStateFlow(emptyList())

    private var _pagination: PaginationData? = null

    private val commentsComparator = query.sort.toComparator()

    override val comments: StateFlow<List<ThreadedCommentData>>
        get() = _comments.asStateFlow()

    override val pagination: PaginationData?
        get() = _pagination

    override fun onActivityRemoved() {
        _comments.update { emptyList() }
    }

    override fun onQueryMoreComments(result: PaginationResult<ThreadedCommentData>) {
        _pagination = result.pagination
        _comments.update { current ->
            current.mergeSorted(result.models, ThreadedCommentData::id, commentsComparator)
        }
    }

    override fun onCommentUpserted(comment: CommentData) {
        if (comment.parentId == null) {
            // If the comment is a top-level comment, add it directly
            _comments.update { current ->
                current.upsertSorted(
                    element = ThreadedCommentData(comment),
                    idSelector = ThreadedCommentData::id,
                    comparator = commentsComparator,
                    update = { it.update(comment) },
                )
            }
        } else {
            // If it's a reply, find the parent and add it to the parent's replies
            // Update the comments list by searching for the parent in all top-level comments
            _comments.update { current ->
                current.map { parent -> parent.upsertNestedReply(comment, commentsComparator) }
            }
        }
    }

    override fun onCommentRemoved(commentId: String) {
        _comments.update { current -> current.removeComment(commentId) }
    }

    override fun onCommentReactionUpserted(
        comment: CommentData,
        reaction: FeedsReactionData,
        enforceUnique: Boolean,
    ) {
        _comments.update { current ->
            current.map { upsertCommentReaction(it, comment, reaction, enforceUnique) }
        }
    }

    override fun onCommentReactionRemoved(comment: CommentData, reaction: FeedsReactionData) {
        _comments.update { current -> current.map { removeCommentReaction(it, comment, reaction) } }
    }

    private fun upsertCommentReaction(
        comment: ThreadedCommentData,
        update: CommentData,
        reaction: FeedsReactionData,
        enforceUnique: Boolean,
    ): ThreadedCommentData {
        if (comment.id == update.id) {
            // If this comment matches the target, upsert the reaction
            return comment.upsertReaction(update, reaction, currentUserId, enforceUnique)
        }
        if (comment.replies.isNullOrEmpty()) {
            // If there are no replies, return unchanged
            return comment
        }
        // Recursively search through replies
        val updatedReplies =
            comment.replies.map { reply ->
                upsertCommentReaction(reply, update, reaction, enforceUnique)
            }
        return comment.copy(replies = updatedReplies)
    }

    private fun removeCommentReaction(
        comment: ThreadedCommentData,
        target: CommentData,
        reaction: FeedsReactionData,
    ): ThreadedCommentData {
        if (comment.id == target.id) {
            // If this comment matches the target, remove the reaction
            return comment.removeReaction(target, reaction, currentUserId)
        }
        if (comment.replies.isNullOrEmpty()) {
            // If there are no replies, return unchanged
            return comment
        }
        // Recursively search through replies
        val updatedReplies =
            comment.replies.map { reply -> removeCommentReaction(reply, target, reaction) }
        return comment.copy(replies = updatedReplies)
    }
}

/** A mutable state interface for managing the state of an activity comment list. */
internal interface ActivityCommentListMutableState :
    ActivityCommentListState, ActivityCommentListStateUpdates

/** An interface that defines the methods for updating the state of an activity comment list. */
internal interface ActivityCommentListStateUpdates {

    /** Handles the deletion of the parent activity. */
    fun onActivityRemoved()

    /**
     * Handles the result of a query for comments.
     *
     * @param result The pagination result containing the new comments.
     */
    fun onQueryMoreComments(result: PaginationResult<ThreadedCommentData>)

    /**
     * Handles the removal of a comment.
     *
     * @param commentId The ID of the comment that was removed.
     */
    fun onCommentRemoved(commentId: String)

    /**
     * Handles the addition or update of a comment.
     *
     * @param comment The comment that was added or updated.
     */
    fun onCommentUpserted(comment: CommentData)

    /**
     * Handles the addition or update of a reaction to a comment.
     *
     * @param comment The comment the reaction belongs to.
     * @param reaction The reaction data that was added.
     * @param enforceUnique Whether to replace existing reactions by the same user.
     */
    fun onCommentReactionUpserted(
        comment: CommentData,
        reaction: FeedsReactionData,
        enforceUnique: Boolean,
    )

    /**
     * Handles the removal of a reaction from a comment.
     *
     * @param comment The comment from which the reaction was removed.
     * @param reaction The reaction data that was removed.
     */
    fun onCommentReactionRemoved(comment: CommentData, reaction: FeedsReactionData)
}
