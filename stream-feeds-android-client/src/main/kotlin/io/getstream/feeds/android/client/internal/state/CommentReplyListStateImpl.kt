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
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.state.CommentReplyListState
import io.getstream.feeds.android.client.api.state.query.CommentRepliesQuery
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.model.removeReaction
import io.getstream.feeds.android.client.internal.model.update
import io.getstream.feeds.android.client.internal.model.upsertNestedReply
import io.getstream.feeds.android.client.internal.model.upsertReaction
import io.getstream.feeds.android.client.internal.state.query.toComparator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * An observable object representing the current state of a comment's reply list.
 *
 * This class manages the state of replies for a specific comment, including the list of replies,
 * pagination information, and real-time updates from WebSocket events. It automatically handles
 * reply updates and reaction changes.
 *
 * @property query The query configuration used to fetch replies.
 * @property currentUserId The ID of the current user, used for reaction management.
 */
internal class CommentReplyListStateImpl(
    override val query: CommentRepliesQuery,
    private val currentUserId: String,
) : CommentReplyListMutableState {

    private val _replies: MutableStateFlow<List<ThreadedCommentData>> =
        MutableStateFlow(emptyList())

    private var _pagination: PaginationData? = null

    override val replies: StateFlow<List<ThreadedCommentData>>
        get() = _replies.asStateFlow()

    override val pagination: PaginationData?
        get() = _pagination

    private val comparator = query.sort.toComparator()

    override fun onQueryMoreReplies(result: PaginationResult<ThreadedCommentData>) {
        _pagination = result.pagination
        // Merge the new replies with the existing ones (keeping the sort order)
        _replies.update { current -> current + result.models }
    }

    override fun onCommentRemoved(commentId: String) {
        if (commentId == query.commentId) {
            // If the deleted comment is the parent comment, we clear the entire state
            _replies.update { emptyList() }
        } else {
            onReplyRemoved(commentId)
        }
    }

    private fun onReplyRemoved(commentId: String) {
        _replies.update { current ->
            val filteredTopLevel = current.filter { it.id != commentId }
            if (filteredTopLevel.size != current.size) {
                // A top-level comment was removed, update the state
                filteredTopLevel
            } else {
                // It might be a nested reply, search and remove recursively
                current.map { parent -> removeNestedReply(parent, commentId) }
            }
        }
    }

    override fun onCommentUpserted(comment: CommentData) {
        if (comment.parentId == null) {
            // Comment is not a reply, ignore it
            return
        }
        _replies.update { current -> current.map { it.upsertNestedReply(comment, comparator) } }
    }

    override fun onCommentReactionUpserted(comment: CommentData, reaction: FeedsReactionData) {
        _replies.update { current ->
            current.map { parent -> addNestedReplyReaction(parent, comment, reaction) }
        }
    }

    override fun onCommentReactionRemoved(comment: CommentData, reaction: FeedsReactionData) {
        _replies.update { current ->
            current.map { parent -> removeNestedReplyReaction(parent, comment, reaction) }
        }
    }

    private fun removeNestedReply(
        comment: ThreadedCommentData,
        commentIdToRemove: String,
    ): ThreadedCommentData {
        // If this comment has no replies, nothing to remove
        if (comment.replies.isNullOrEmpty()) {
            return comment
        }
        // Check if the comment to remove is a direct reply
        val filteredReplies = comment.replies.filter { it.id != commentIdToRemove }
        if (filteredReplies.size != comment.replies.size) {
            // Found and removed a direct child, update reply count
            return comment.copy(replies = filteredReplies, replyCount = comment.replyCount - 1)
        }
        // If not found, recursively check each reply
        return comment.copy(
            replies = comment.replies.map { child -> removeNestedReply(child, commentIdToRemove) }
        )
    }

    private fun addNestedReplyReaction(
        parent: ThreadedCommentData,
        comment: CommentData,
        reaction: FeedsReactionData,
    ): ThreadedCommentData {
        // If this comment is the parent, add the reaction directly
        if (parent.id == comment.id) {
            return parent.upsertReaction(comment, reaction, currentUserId)
        }
        // If the parent has no replies, return it unchanged
        if (parent.replies.isNullOrEmpty()) {
            return parent
        }
        // Otherwise, recursively search for the comment in the replies
        return parent.copy(
            replies =
                parent.replies.map { child -> addNestedReplyReaction(child, comment, reaction) }
        )
    }

    private fun removeNestedReplyReaction(
        parent: ThreadedCommentData,
        comment: CommentData,
        reaction: FeedsReactionData,
    ): ThreadedCommentData {
        // If this comment is the parent, remove the reaction directly
        if (parent.id == comment.id) {
            return parent.removeReaction(comment, reaction, currentUserId)
        }
        // If the parent has no replies, return it unchanged
        if (parent.replies.isNullOrEmpty()) {
            return parent
        }
        // Otherwise, recursively search for the comment in the replies
        return parent.copy(
            replies =
                parent.replies.map { child -> removeNestedReplyReaction(child, comment, reaction) }
        )
    }
}

/** A mutable state interface for managing the state of a comment reply list. */
internal interface CommentReplyListMutableState :
    CommentReplyListState, CommentReplyListStateUpdates

/**
 * An interface that defines the methods for handling updates to the state of a comment reply list.
 */
internal interface CommentReplyListStateUpdates {

    /**
     * Handles the result of a query for replies to a comment.
     *
     * @param result The pagination result containing the replies to the comment.
     */
    fun onQueryMoreReplies(result: PaginationResult<ThreadedCommentData>)

    /**
     * Handles the removal of a comment reply.
     *
     * @param commentId The ID of the comment that was removed.
     */
    fun onCommentRemoved(commentId: String)

    /**
     * Handles the addition or update of a new comment reply.
     *
     * @param comment The comment data for the newly added reply.
     */
    fun onCommentUpserted(comment: CommentData)

    /**
     * Handles the addition of a reaction to a comment reply.
     *
     * @param comment The comment that received the reaction.
     * @param reaction The reaction data that was added to the comment.
     */
    fun onCommentReactionUpserted(comment: CommentData, reaction: FeedsReactionData)

    /**
     * Handles the removal of a reaction from a comment reply.
     *
     * @param comment The comment that had the reaction removed.
     * @param reaction The reaction data that was removed from the comment.
     */
    fun onCommentReactionRemoved(comment: CommentData, reaction: FeedsReactionData)
}
