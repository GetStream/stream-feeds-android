package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.state.query.CommentRepliesQuery
import kotlinx.coroutines.flow.StateFlow

/**
 * An observable object representing the current state of a comment's reply list.
 *
 * This class manages the state of replies for a specific comment, including
 * the list of replies, pagination information, and real-time updates from
 * WebSocket events. It automatically handles reply updates and reaction changes.
 *
 * TODO: Add Usage examples
 *
 * ## Features
 *
 * - **Observable State**: Uses `@Published` properties for reactive UI updates
 * - **Real-time Updates**: Automatically receives WebSocket events for reply changes
 * - **Pagination Support**: Tracks pagination state for loading more replies
 * - **Change Handlers**: Internal handlers for processing WebSocket events
 */
public interface CommentReplyListState {

    /**
     * The query configuration used to fetch replies.
     */
    public val query: CommentRepliesQuery

    /**
     * The current collection of replies for the comment.
     *
     * This list contains all the replies that have been loaded for the comment.
     * Changes to this array are published and can be observed for UI updates.
     */
    public val replies: StateFlow<List<ThreadedCommentData>>

    /**
     * The pagination information from the last query.
     *
     * This contains the pagination cursors (`next` and `previous`) that can be used
     * to fetch additional pages of replies. The pagination data is automatically
     * updated when new pages are loaded.
     */
    public val pagination: PaginationData?

    /**
     * Indicates whether there are more replies available to load.
     *
     * This property returns `true` if there are additional pages of replies available
     * to fetch. You can use this to determine whether to show a "Load More" button
     * or implement infinite scrolling.
     */
    public val canLoadMore: Boolean
        get() = pagination?.next != null
}
