package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.CommentReactionsQuery
import kotlinx.coroutines.flow.StateFlow

/**
 * An observable state object that manages the current state of a comment reaction list.
 *
 * This class provides reactive state management for a collection of comment reactions.
 * It automatically handles real-time updates when reactions are added or removed from the comment,
 * and maintains pagination state for loading additional reactions.
 */
public interface CommentReactionListState {

    /**
     * The query configuration used to fetch comment reactions.
     *
     * This contains the comment ID, filters, sorting options, and pagination parameters
     * that define how reactions should be fetched and displayed.
     */
    public val query: CommentReactionsQuery

    /**
     * The current collection of comment reactions.
     *
     * This list contains all the reactions that have been loaded for the comment.
     * The reactions are automatically sorted according to the query configuration.
     * Changes to this array are published and can be observed for UI updates.
     */
    public val reactions: StateFlow<List<FeedsReactionData>>

    /**
     * The pagination information from the last query.
     *
     * This contains the pagination cursors (`next` and `previous`) that can be used
     * to fetch additional pages of reactions. The pagination data is automatically
     * updated when new pages are loaded.
     */
    public val pagination: PaginationData?

    /**
     * Indicates whether there are more reactions available to load.
     *
     * This property returns `true` if there are additional pages of reactions available
     * to fetch. You can use this to determine whether to show a "Load More" button
     * or implement infinite scrolling.
     */
    public val canLoadMore: Boolean
        get() = pagination?.next != null
}