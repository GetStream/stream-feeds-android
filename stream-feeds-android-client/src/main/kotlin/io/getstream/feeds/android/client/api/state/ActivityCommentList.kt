package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.ThreadedCommentData

/**
 * A class representing a paginated list of comments for a specific activity.
 *
 * This class provides methods to fetch and manage comments for an activity, including
 * pagination support and real-time updates through WebSocket events. It maintains an
 * observable state that automatically updates when comment-related events are received.
 *
 * TODO: Add examples
 *
 * ## Features
 *
 * - **Pagination**: Supports loading comments in pages with configurable limits
 * - **Real-time Updates**: Automatically receives WebSocket events for comment changes
 * - **Threaded Comments**: Supports nested comment replies
 * - **Reactions**: Tracks comment reactions and updates in real-time
 */
public interface ActivityCommentList {

    /**
     * The query configuration used to fetch comments.
     */
    public val query: ActivityCommentsQuery

    /**
     * An observable object representing the current state of the comment list.
     *
     * This property provides access to the current comments, pagination information,
     * and real-time updates. The state automatically updates when WebSocket events
     * are received for comment additions, updates, deletions, and reactions.
     */
    public val state: ActivityCommentListState

    /**
     * Fetches the initial set of comments for the activity.
     *
     * This method retrieves the first page of comments based on the query configuration.
     * The results are automatically stored in the state and can be accessed through
     * the [state.comments] property.
     */
    public suspend fun get(): Result<List<ThreadedCommentData>>

    /**
     * Fetches the next page of comments based on the current pagination state.
     *
     * This method retrieves the next set of comments using the pagination cursor
     * stored in the state. It updates the state with the new comments and returns
     * the results.
     *
     * @param limit The maximum number of comments to fetch in this request. If not specified, uses
     * the limit from the original query.
     * @return A [Result] containing a list of [ThreadedCommentData] for the next page,
     * or an error if the operation fails. Returns an empty list if there are no more comments to
     * fetch.
     */
    public suspend fun queryMoreComments(limit: Int? = null): Result<List<ThreadedCommentData>>
}
