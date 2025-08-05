package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.state.CommentReactionListState
import io.getstream.feeds.android.client.api.state.query.CommentReactionsQuery
import io.getstream.feeds.android.client.api.state.query.CommentReactionsSort
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * An observable state object that manages the current state of a comment reaction list.
 *
 * This class provides reactive state management for a collection of comment reactions.
 * It automatically handles real-time updates when reactions are added or removed from the comment,
 * and maintains pagination state for loading additional reactions.
 */
internal class CommentReactionListStateImpl(
    override val query: CommentReactionsQuery,
): CommentReactionListMutableState {

    private val _reactions: MutableStateFlow<List<FeedsReactionData>> = MutableStateFlow(emptyList())

    internal var queryConfig: QueryConfiguration<CommentReactionsSort>? = null
        private set

    private var _pagination: PaginationData? = null

    private val reactionsSorting: List<CommentReactionsSort>
        get() = query.sort ?: CommentReactionsSort.Default

    override val reactions: StateFlow<List<FeedsReactionData>>
        get() = _reactions.asStateFlow()

    override val pagination: PaginationData?
        get() = _pagination

    override fun onQueryMoreReactions(
        result: PaginationResult<FeedsReactionData>,
        queryConfig: QueryConfiguration<CommentReactionsSort>
    ) {
        _pagination = result.pagination
        // Update the query configuration for future queries
        this.queryConfig = queryConfig
        // Merge the new reactions with the existing ones (keeping the sort order)
        _reactions.value =
            _reactions.value.mergeSorted(result.models, FeedsReactionData::id, reactionsSorting)
    }

    override fun onReactionRemoved(reaction: FeedsReactionData) {
       _reactions.value = _reactions.value.filter { it.id != reaction.id }
    }
}

internal interface CommentReactionListMutableState : CommentReactionListState,
    CommentReactionListStateUpdates

internal interface CommentReactionListStateUpdates {

    /**
     * Handles the successful loading of reactions.
     *
     * @param result The result containing the loaded reactions and pagination data.
     * @param queryConfig The query configuration used for the request.
     */
    fun onQueryMoreReactions(
        result: PaginationResult<FeedsReactionData>,
        queryConfig: QueryConfiguration<CommentReactionsSort>
    )

    /**
     * Handles the removal of a reaction from the comment.
     *
     * @param reaction The reaction that was removed.
     */
    fun onReactionRemoved(reaction: FeedsReactionData)
}
