package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.state.query.CommentRepliesQuery

/**
 * A class representing a paginated list of replies for a specific comment.
 *
 * This class provides methods to fetch and manage replies to a comment, including
 * pagination support and real-time updates through WebSocket events. It maintains an
 * observable state that automatically updates when reply-related events are received.
 *
 * TODO: Add Usage examples
 *
 * ## Features
 *
 * - **Pagination**: Supports loading replies in pages with configurable limits
 * - **Real-time Updates**: Automatically receives WebSocket events for reply changes
 * - **Threaded Replies**: Supports nested reply structures with depth control
 * - **Reactions**: Tracks reply reactions and updates in real-time
 * - **Observable State**: Provides reactive state management for UI updates
 */
public interface CommentReplyList {

    /**
     * The query configuration used to fetch replies.
     */
    public val query: CommentRepliesQuery

    /**
     * An observable object representing the current state of the reply list.
     *
     * This property provides access to the current replies, pagination information,
     * and real-time updates. The state automatically updates when WebSocket events
     * are received for reply additions, updates, deletions, and reactions.
     */
    public val state: CommentReplyListState

    /**
     * Fetches the initial set of replies for the comment.
     *
     * This method retrieves the first page of replies based on the query configuration.
     * The results are automatically stored in the state and can be accessed through
     * the [state.replies] property.
     *
     * @return A [Result] containing a list of [ThreadedCommentData] representing the replies if
     * successful, or an error if the request fails.
     */
    public suspend fun get(): Result<List<ThreadedCommentData>>

    /**
     * Loads the next page of replies if more are available.
     *
     * This method fetches additional replies using the pagination information
     * from the previous request. If no more replies are available, an empty
     * array is returned.
     *
     * @param limit The maximum number of replies to fetch. If null, uses the default limit
     * defined in the query configuration.
     * @return A [Result] containing a list of [ThreadedCommentData] representing the next page of
     * replies if successful, or an error if the request fails.
     */
    public suspend fun queryMoreReplies(limit: Int? = null): Result<List<ThreadedCommentData>>
}