package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.model.addReaction
import io.getstream.feeds.android.client.api.model.addReply
import io.getstream.feeds.android.client.api.model.removeReaction
import io.getstream.feeds.android.client.api.model.setCommentData
import io.getstream.feeds.android.client.api.state.ActivityCommentListState
import io.getstream.feeds.android.client.api.state.query.ActivityCommentsQuery
import io.getstream.feeds.android.client.internal.utils.upsert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * A class representing a paginated list of comments for a specific activity.
 *
 * This class provides methods to fetch and manage comments for an activity, including
 * pagination support and real-time updates through WebSocket events. It maintains an
 * observable state that automatically updates when comment-related events are received.
 */
internal class ActivityCommentListStateImpl(
    override val query: ActivityCommentsQuery,
    private val currentUserId: String,
) : ActivityCommentListMutableState {

    private val _comments: MutableStateFlow<List<ThreadedCommentData>> =
        MutableStateFlow(emptyList())

    private var _pagination: PaginationData? = null

    override val comments: StateFlow<List<ThreadedCommentData>>
        get() = _comments

    override val pagination: PaginationData?
        get() = _pagination

    override fun onQueryMoreComments(result: PaginationResult<ThreadedCommentData>) {
        _pagination = result.pagination
        _comments.value = _comments.value + result.models
    }

    override fun onCommentAdded(comment: ThreadedCommentData) {
        if (comment.parentId == null) {
            // If the comment is a top-level comment, add it directly
            _comments.value = _comments.value.upsert(comment, ThreadedCommentData::id)
        } else {
            // If it's a reply, find the parent and add it to the parent's replies
            // Update the comments list by searching for the parent in all top-level comments
            val updatedComments = _comments.value.map { parent ->
                addNestedReply(parent, comment)
            }
            _comments.value = updatedComments
        }
    }

    override fun onCommentUpdated(comment: CommentData) {
        _comments.value = _comments.value.map {
            updateNestedComment(it, comment)
        }
    }

    override fun onCommentRemoved(commentId: String) {
        // First check if it's a top-level comment
        val filteredTopLevel = _comments.value.filter { it.id != commentId }

        if (filteredTopLevel.size != _comments.value.size) {
            // A top-level comment was removed
            _comments.value = filteredTopLevel
        } else {
            // TODO: test this logic
            // It might be a nested reply, search and remove recursively
            val updatedComments = _comments.value.map { comment ->
                removeCommentFromReplies(comment, commentId)
            }
            _comments.value = updatedComments
        }
    }

    override fun onCommentReactionAdded(
        commentId: String,
        reaction: FeedsReactionData
    ) {
        _comments.value = _comments.value.map { comment ->
            addCommentReaction(comment, commentId, reaction)
        }
    }

    override fun onCommentReactionRemoved(
        commentId: String,
        reaction: FeedsReactionData
    ) {
        _comments.value = _comments.value.map { comment ->
            removeCommentReaction(comment, commentId, reaction)
        }
    }

    private fun addNestedReply(
        parent: ThreadedCommentData,
        reply: ThreadedCommentData
    ): ThreadedCommentData {
        // If this comment is the parent, add the reply directly
        if (parent.id == reply.parentId) {
            return parent.addReply(reply)
        }
        // If this comment has replies, recursively search through them
        val replies = parent.replies
        if (!replies.isNullOrEmpty()) {
            val updatedReplies = replies.map { replyComment ->
                addNestedReply(replyComment, reply)
            }
            return parent.copy(replies = updatedReplies)
        }
        // No matching parent found in this subtree, return unchanged
        return parent
    }

    private fun updateNestedComment(
        comment: ThreadedCommentData,
        updated: CommentData
    ): ThreadedCommentData {
        if (comment.id == updated.id) {
            // If this comment matches the updated comment, return it with the new data
            return comment.setCommentData(updated)
        }
        if (comment.replies.isNullOrEmpty()) {
            // If there are no replies, return unchanged
            return comment
        }
        // Recursively search through replies
        val updatedReplies = comment.replies.map { reply ->
            updateNestedComment(reply, updated)
        }
        return comment.copy(replies = updatedReplies)
    }

    private fun removeCommentFromReplies(
        comment: ThreadedCommentData,
        commentIdToRemove: String
    ): ThreadedCommentData {
        // If this comment has no replies, nothing to remove
        if (comment.replies.isNullOrEmpty()) {
            return comment
        }

        // Check if the comment to remove is a direct child
        val filteredReplies = comment.replies.filter { it.id != commentIdToRemove }
        if (filteredReplies.size != comment.replies.size) {
            // Found and removed a direct child, update reply count
            return comment.copy(
                replies = filteredReplies,
                replyCount = comment.replyCount - 1
            )
        }

        // The comment wasn't a direct child, search recursively in nested replies
        val updatedReplies = comment.replies.map { reply ->
            removeCommentFromReplies(reply, commentIdToRemove)
        }

        return comment.copy(replies = updatedReplies)
    }

    private fun addCommentReaction(
        comment: ThreadedCommentData,
        targetId: String,
        reaction: FeedsReactionData,
    ): ThreadedCommentData {
        if (comment.id == targetId) {
            // If this comment matches the target, add the reaction
            return comment.addReaction(reaction, currentUserId)
        }
        if (comment.replies.isNullOrEmpty()) {
            // If there are no replies, return unchanged
            return comment
        }
        // Recursively search through replies
        val updatedReplies = comment.replies.map { reply ->
            addCommentReaction(reply, targetId, reaction)
        }
        return comment.copy(replies = updatedReplies)
    }

    private fun removeCommentReaction(
        comment: ThreadedCommentData,
        targetId: String,
        reaction: FeedsReactionData
    ): ThreadedCommentData {
        if (comment.id == targetId) {
            // If this comment matches the target, remove the reaction
            return comment.removeReaction(reaction, currentUserId)
        }
        if (comment.replies.isNullOrEmpty()) {
            // If there are no replies, return unchanged
            return comment
        }
        // Recursively search through replies
        val updatedReplies = comment.replies.map { reply ->
            removeCommentReaction(reply, targetId, reaction)
        }
        return comment.copy(replies = updatedReplies)
    }
}

/**
 * A mutable state interface for managing the state of an activity comment list.
 */
internal interface ActivityCommentListMutableState : ActivityCommentListState,
    ActivityCommentListStateUpdates

/**
 * An interface that defines the methods for updating the state of an activity comment list.
 */
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
     * Handles the addition of a reaction to a comment.
     *
     * @param commentId The ID of the comment to which the reaction was added.
     * @param reaction The reaction data that was added.
     */
    fun onCommentReactionAdded(commentId: String, reaction: FeedsReactionData)

    /**
     * Handles the removal of a reaction from a comment.
     *
     * @param commentId The ID of the comment from which the reaction was removed.
     * @param reaction The reaction data that was removed.
     */
    fun onCommentReactionRemoved(commentId: String, reaction: FeedsReactionData)
}