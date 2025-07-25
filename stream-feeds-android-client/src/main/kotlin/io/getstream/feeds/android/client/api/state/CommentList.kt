package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.state.query.CommentsQuery

/**
 * A class representing a paginated list of comments for a specific query.
 *
 * This interface provides methods to fetch and manage comments, including pagination support
 * and real-time updates through WebSocket events. It maintains an observable state that
 * automatically updates when comment-related events are received.
 */
public interface CommentList {

    /**
     * The query used to fetch the comments.
     */
    public val query: CommentsQuery

    /**
     * An observable object representing the current state of the comment list.
     */
    public val state: CommentListState

    /**
     * Fetches the first page comments based on the query.
     *
     * @return A [Result] containing a list of [CommentData] if successful, or an error if the
     * request fails.
     */
    public suspend fun get(): Result<List<CommentData>>

    /**
     * Fetches the next page of comments based on the current state.
     *
     * @param limit Optional limit for the number of comments to fetch.
     * @return A [Result] containing a list of [CommentData] if successful, or an error if the
     * request fails. Returns an empty list if there are no more comments to fetch.
     */
    public suspend fun queryMoreComments(limit: Int? = null): Result<List<CommentData>>
}
