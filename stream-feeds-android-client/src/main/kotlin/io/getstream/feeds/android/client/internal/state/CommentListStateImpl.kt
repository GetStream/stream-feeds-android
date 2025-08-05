package io.getstream.feeds.android.client.internal.state

import io.getstream.android.core.query.Filter
import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.state.CommentListState
import io.getstream.feeds.android.client.api.state.query.CommentsQuery
import io.getstream.feeds.android.client.api.state.query.CommentsSort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * An observable state object that manages the current state of a comment list.
 *
 * This interface maintains the current list of comments, pagination information, and provides
 * real-time updates. It automatically handles WebSocket events to keep the comment list
 * synchronized.
 *
 * ## Features
 *
 * - **Observable State**: Uses flow properties for reactive UI updates
 * - **Real-time Updates**: Automatically receives WebSocket events for comment changes
 * - **Pagination Support**: Tracks pagination state for loading more comments
 * - **Change Handlers**: Internal handlers for processing WebSocket events
 *
 * @property query The query used to fetch the comments.
 */
internal class CommentListStateImpl(
    override val query: CommentsQuery,
): CommentListMutableState {

    private val _comments: MutableStateFlow<List<CommentData>> = MutableStateFlow(emptyList())

    internal var filter: Filter? = null
        private set

    internal var sort: CommentsSort? = null
        private set

    private var _pagination: PaginationData? = null

    override val comments: StateFlow<List<CommentData>>
        get() = _comments.asStateFlow()

    override val pagination: PaginationData?
        get() = _pagination

    override fun onQueryMoreComments(
        result: PaginationResult<CommentData>,
        filter: Filter?,
        sort: CommentsSort?
    ) {
        _pagination = result.pagination
        // Update the filter and sort for future queries
        this.filter = filter
        this.sort = sort
        // Merge the new comments with the existing ones (keeping the sort order)
        _comments.value = _comments.value + result.models
    }

    override fun onCommentUpdated(comment: CommentData) {
        _comments.value = _comments.value.map { existingComment ->
            if (existingComment.id == comment.id) {
                // Update the existing comment with the new data
                comment
            } else {
                existingComment
            }
        }
    }
}


internal interface CommentListMutableState: CommentListState, CommentListStateUpdates

/**
 * Interface defining the methods for updating the comment list state.
 */
internal interface CommentListStateUpdates {

    /**
     * Handles the result of querying more comments.
     *
     * @param result The pagination result containing the new comments.
     * @param filter The filter used for the query, if any.
     * @param sort The sorting configuration used for the query, if any.
     */
    fun onQueryMoreComments(
        result: PaginationResult<CommentData>,
        filter: Filter?,
        sort: CommentsSort?,
    )

    /**
     * Handles the update of a comment in the list.
     *
     * @param comment The updated comment data.
     */
    fun onCommentUpdated(comment: CommentData)
}
