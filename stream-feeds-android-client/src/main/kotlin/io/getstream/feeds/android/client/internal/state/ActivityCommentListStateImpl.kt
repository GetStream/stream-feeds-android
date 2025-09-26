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
package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.state.ActivityCommentListState
import io.getstream.feeds.android.client.api.state.query.ActivityCommentsQuery
import io.getstream.feeds.android.client.api.state.query.toComparator
import io.getstream.feeds.android.client.internal.model.addReply
import io.getstream.feeds.android.client.internal.model.removeReaction
import io.getstream.feeds.android.client.internal.model.update
import io.getstream.feeds.android.client.internal.model.upsertReaction
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import io.getstream.feeds.android.client.internal.utils.treeRemoveFirst
import io.getstream.feeds.android.client.internal.utils.treeUpdateFirst
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

    override fun onQueryMoreComments(result: PaginationResult<ThreadedCommentData>) {
        _pagination = result.pagination
        _comments.update { current ->
            current.mergeSorted(result.models, ThreadedCommentData::id, commentsComparator)
        }
    }

    override fun onCommentAdded(comment: ThreadedCommentData) {
        if (comment.parentId == null) {
            // If the comment is a top-level comment, add it directly
            _comments.update { current ->
                current.upsertSorted(comment, ThreadedCommentData::id, commentsComparator)
            }
        } else {
            // If it's a reply, find the parent and add it to the parent's replies
            // Update the comments list by searching for the parent in all top-level comments
            _comments.update { current ->
                current.map { parent -> addNestedReply(parent, comment) }
            }
        }
    }

    override fun onCommentUpdated(comment: CommentData) {
        _comments.update { current ->
            current.treeUpdateFirst(
                matcher = { it.id == comment.id },
                childrenSelector = { it.replies.orEmpty() },
                updateElement = { it.update(comment) },
                updateChildren = { parent, children -> parent.copy(replies = children) },
                comparator = commentsComparator,
            )
        }
    }

    override fun onCommentRemoved(commentId: String) {
        _comments.update { current ->
            current.treeRemoveFirst(
                matcher = { it.id == commentId },
                childrenSelector = { it.replies.orEmpty() },
                updateChildren = { parent, children ->
                    parent.copy(replies = children, replyCount = parent.replyCount - 1)
                },
            )
        }
    }

    override fun onCommentReactionUpserted(comment: CommentData, reaction: FeedsReactionData) {
        _comments.update { current -> current.map { upsertCommentReaction(it, comment, reaction) } }
    }

    override fun onCommentReactionRemoved(comment: CommentData, reaction: FeedsReactionData) {
        _comments.update { current -> current.map { removeCommentReaction(it, comment, reaction) } }
    }

    private fun addNestedReply(
        parent: ThreadedCommentData,
        reply: ThreadedCommentData,
    ): ThreadedCommentData {
        // If this comment is the parent, add the reply directly
        if (parent.id == reply.parentId) {
            return parent.addReply(reply, commentsComparator)
        }
        // If this comment has replies, recursively search through them
        val replies = parent.replies
        if (!replies.isNullOrEmpty()) {
            val updatedReplies = replies.map { replyComment -> addNestedReply(replyComment, reply) }
            return parent.copy(replies = updatedReplies)
        }
        // No matching parent found in this subtree, return unchanged
        return parent
    }

    private fun upsertCommentReaction(
        comment: ThreadedCommentData,
        update: CommentData,
        reaction: FeedsReactionData,
    ): ThreadedCommentData {
        if (comment.id == update.id) {
            // If this comment matches the target, upsert the reaction
            return comment.upsertReaction(update, reaction, currentUserId)
        }
        if (comment.replies.isNullOrEmpty()) {
            // If there are no replies, return unchanged
            return comment
        }
        // Recursively search through replies
        val updatedReplies =
            comment.replies.map { reply -> upsertCommentReaction(reply, update, reaction) }
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

    /**
     * Handles the result of a query for comments.
     *
     * @param result The pagination result containing the new comments.
     */
    fun onQueryMoreComments(result: PaginationResult<ThreadedCommentData>)

    /**
     * Handles the addition of a new comment.
     *
     * @param comment The comment that was added.
     */
    fun onCommentAdded(comment: ThreadedCommentData)

    /**
     * Handles the update of an existing comment.
     *
     * @param comment The updated comment data.
     */
    fun onCommentUpdated(comment: CommentData)

    /**
     * Handles the removal of a comment.
     *
     * @param commentId The ID of the comment that was removed.
     */
    fun onCommentRemoved(commentId: String)

    /**
     * Handles the addition or update of a reaction to a comment.
     *
     * @param comment The comment the reaction belongs to.
     * @param reaction The reaction data that was added.
     */
    fun onCommentReactionUpserted(comment: CommentData, reaction: FeedsReactionData)

    /**
     * Handles the removal of a reaction from a comment.
     *
     * @param comment The comment from which the reaction was removed.
     * @param reaction The reaction data that was removed.
     */
    fun onCommentReactionRemoved(comment: CommentData, reaction: FeedsReactionData)
}
